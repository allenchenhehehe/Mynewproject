import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/myfridge/api'

export const useShoppingStore = defineStore('shopping', () => {
  // State
  const shoppingList = ref([])
  const loading = ref(false)
  const error = ref(null)

  // Getters
  const totalItems = computed(() => {
    let count = 0
    shoppingList.value.forEach((group) => {
      count += group.items.length
    })
    return count
  })

  const purchasedCount = computed(() => {
    let count = 0
    shoppingList.value.forEach((group) => {
      group.items.forEach((item) => {
        if (item.is_purchased) count++
      })
    })
    return count
  })

  const remainingCount = computed(() => {
    return totalItems.value - purchasedCount.value
  })

  const purchasePercentage = computed(() => {
    return totalItems.value > 0 ? Math.round((purchasedCount.value / totalItems.value) * 100) : 0
  })

  // 只計算食材（不含調味料、油類等）
  const foodItems = computed(() => {
    const foodCategories = ['vegetable', 'fruit', 'meat', 'egg', 'seafood', 'bean','oil', 'seasoning', 'other']
    let count = 0
    let purchased = 0
    shoppingList.value.forEach((group) => {
      group.items.forEach((item) => {
        if (foodCategories.includes(item.category)) {
          count++
          if (item.is_purchased) purchased++
        }
      })
    })
    return {
      total: count,
      purchased: purchased,
      remaining: count - purchased,
      percentage: count > 0 ? Math.round((purchased / count) * 100) : 0
    }
  })

  // Actions

  //取得購物清單
  async function fetchItems() {
    loading.value = true
    error.value = null

    try {

      const response = await axios.get(
        `${API_BASE_URL}/shopping-list`,
        { withCredentials: true }
      )

      console.log('後端回傳:', response.data)

      // 將後端的扁平結構轉換為前端的分組結構
      const grouped = groupByRecipe(response.data)
      shoppingList.value = grouped

      console.log('分組後:', shoppingList.value)

      return { success: true }

    } catch (err) {
      console.error('載入失敗:', err)
      error.value = err.response?.data?.error || '載入失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  //將後端資料扁平化
  function groupByRecipe(items) {
    const groups = {}

    items.forEach(item => {
      const key = item.recipeName || '手動新增'

      if (!groups[key]) {
        groups[key] = {
          recipeId: item.recipeId,
          recipeName: key,
          items: []
        }
      }

      groups[key].items.push({
        id: item.id,
        ingredient_id: item.ingredientId,
        ingredient_name: item.ingredientName,
        quantity: item.amount,
        unit: item.unit,
        category: item.category,
        is_purchased: item.isPurchased
      })
    })

     Object.values(groups).forEach(group => {
      group.items.sort((a, b) => {
        // 1. 先按已購買狀態排序（未購買在前）
        if (a.is_purchased !== b.is_purchased) {
          return a.is_purchased ? 1 : -1
        }
        // 2. 再按 ID 排序（保持原始新增順序）
        return a.id - b.id
      })
    })

    return Object.values(groups)
  }

  //新增項目到購物清單
  async function addManualItem(item) {
    loading.value = true
    error.value = null

    try {
      console.log('新增購物項目:', item)

      const response = await axios.post(
        `${API_BASE_URL}/shopping-list`,
        {
          ingredientName: item.ingredient_name,
          amount: item.quantity,
          unit: item.unit,
          category: item.category,
          recipeId: null,
          recipeName: '手動新增',
          ingredientId: null
        },
        { withCredentials: true }
      )

      console.log('新增成功:', response.data)

      // 重新載入列表
      await fetchItems()

      return { success: true }

    } catch (err) {
      console.error('新增失敗:', err)
      error.value = err.response?.data?.error || '新增失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  //標記已購買未購買
  async function togglePurchased(itemId) {
    // 先在前端更新狀態（樂觀更新）
    let targetItem = null
    shoppingList.value.forEach(group => {
        const found = group.items.find(item => item.id === itemId)
        if (found) {
            targetItem = found
            found.is_purchased = !found.is_purchased  // 先更新前端狀態
        }
    })

    if (!targetItem) {
        console.error('找不到該項目:', itemId)
        return { success: false, error: '找不到該項目' }
    }

    loading.value = true
    error.value = null

    try {
        const newStatus = targetItem.is_purchased

        console.log('切換購買狀態:', itemId, '→', newStatus)

        const response = await axios.patch(
            `${API_BASE_URL}/shopping-list/${itemId}`,
            { isPurchased: newStatus },
            { withCredentials: true }
        )

        console.log('更新成功:', response.data)
        return { success: true }

    } catch (err) {
        console.error('更新失敗:', err)
        error.value = err.response?.data?.error || '更新失敗'
        
        // 如果失敗，恢復前端狀態
        targetItem.is_purchased = !targetItem.is_purchased
        
        return { success: false, error: error.value }

    } finally {
        loading.value = false
    }
}

  //刪除購物項目
  async function deleteItem(itemId) {
    loading.value = true
    error.value = null

    try {
      console.log('刪除購物項目:', itemId)

      await axios.delete(
        `${API_BASE_URL}/shopping-list/${itemId}`,
        { withCredentials: true }
      )

      console.log('刪除成功')

      // 重新載入列表
      await fetchItems()

      return { success: true }

    } catch (err) {
      console.error('刪除失敗:', err)
      error.value = err.response?.data?.error || '刪除失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }


  async function clearPurchased() {
    loading.value = true
    error.value = null

    //清除已購買項目放到冰箱
    try {
      console.log('清除已購買項目...')

      const response = await axios.post(
        `${API_BASE_URL}/shopping-list/clear-purchased`,
        {},
        { withCredentials: true }
      )

      console.log('清除成功:', response.data)

      // 重新載入列表
      await fetchItems()

      return { success: true, count: response.data.count }

    } catch (err) {
      console.error('清除失敗:', err)
      error.value = err.response?.data?.error || '清除失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

//從食譜詳情加入
  async function addToShoppingList(items, recipeName = null, recipeId = null) {
      if (!Array.isArray(items) || items.length === 0) {
        return { success: false, error: '沒有食材可以新增' }
      }

      loading.value = true
      error.value = null

      try {
        console.log('從食譜新增:', recipeName, items)

        // 逐一新增每個食材
        for (const item of items) {
          await axios.post(
            `${API_BASE_URL}/shopping-list`,
            {
              recipeId: recipeId,
              recipeName: recipeName || '未命名食譜',
              ingredientId: item.ingredient_id || null,
              ingredientName: item.ingredient_name,
              amount: item.quantity,
              unit: item.unit,
              category: item.category || 'other'
            },
            { withCredentials: true }
          )
        }

        console.log('新增成功')

        // 重新載入列表
        await fetchItems()

        return { success: true }

      } catch (err) {
        console.error('新增失敗:', err)
        error.value = err.response?.data?.error || '新增失敗'
        return { success: false, error: error.value }

      } finally {
        loading.value = false
      }
    }

  function clearShoppingList() {
    shoppingList.value = []
    console.log('清空購物清單')
  }

  return {
    // State
    shoppingList,
    loading,
    error,
    // Getters
    totalItems,
    purchasedCount,
    remainingCount,
    purchasePercentage,
    foodItems,
    // Actions
    fetchItems,
    addToShoppingList,
    deleteItem,
    togglePurchased,
    clearPurchased,
    addManualItem,
    clearShoppingList
  }
})