import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useShoppingStore = defineStore('shopping', () => {
  // State
  const shoppingList = ref([])

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
    const foodCategories = ['vegetable', 'fruit', 'meat', 'egg', 'seafood', 'other']
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
  function addToShoppingList(items, recipeName = null, recipeId = null) {
    if (Array.isArray(items) && items.length > 0) {
      shoppingList.value.push({
        recipeId: recipeId,
        recipeName: recipeName || '未命名食譜',
        items: items.map((item) => ({
          id: item.id || Date.now() + Math.random(),
          ingredient_name: item.ingredient_name,
          quantity: item.quantity,
          unit: item.unit,
          category: item.category || 'other',
          is_purchased: false
        }))
      })
      console.log('新增到購物清單:', recipeName, '，共', items.length, '個食材')
    }
  }

  function deleteItem(itemId) {
    shoppingList.value.forEach((group) => {
      const index = group.items.findIndex((item) => item.id === itemId)
      if (index > -1) {
        const deletedItem = group.items[index]
        group.items.splice(index, 1)
        console.log('刪除購物項目:', deletedItem.ingredient_name)
      }
    })
  }

  function togglePurchased(itemId) {
    shoppingList.value.forEach((group) => {
      const item = group.items.find((item) => item.id === itemId)
      if (item) {
        item.is_purchased = !item.is_purchased
        console.log(
          item.is_purchased ? '標記已購買:' : '標記未購買:',
          item.ingredient_name
        )
      }
    })
  }

  function clearPurchased() {
    const foodCategories = ['vegetable', 'fruit', 'meat', 'egg', 'seafood', 'other']
    const purchasedItems = []

    // 收集所有已購買的食材（不含調味料、油類）
    shoppingList.value.forEach((group) => {
      group.items.forEach((item) => {
        if (item.is_purchased && foodCategories.includes(item.category)) {
          purchasedItems.push(item)
        }
      })
    })

    // 從清單中移除已購買的食材
    shoppingList.value.forEach((group) => {
      group.items = group.items.filter(
        (item) => !item.is_purchased || !foodCategories.includes(item.category)
      )
    })

    // 移除空的分組
    shoppingList.value = shoppingList.value.filter((group) => group.items.length > 0)

    console.log('清除已購買食材，共', purchasedItems.length, '個')
    return purchasedItems
  }

  function addManualItem(item) {
    let manualGroup = shoppingList.value.find((group) => group.recipeName === '手動新增')

    if (!manualGroup) {
      manualGroup = {
        recipeId: null,
        recipeName: '手動新增',
        items: []
      }
      shoppingList.value.push(manualGroup)
    }

    manualGroup.items.push({
      id: Date.now(),
      ingredient_name: item.ingredient_name,
      quantity: item.quantity,
      unit: item.unit,
      category: item.category || 'other',
      is_purchased: false
    })

    console.log('手動新增食材:', item.ingredient_name)
  }

  function clearShoppingList() {
    shoppingList.value = []
    console.log('清空購物清單')
  }

  return {
    // State
    shoppingList,
    // Getters
    totalItems,
    purchasedCount,
    remainingCount,
    purchasePercentage,
    foodItems,
    // Actions
    addToShoppingList,
    deleteItem,
    togglePurchased,
    clearPurchased,
    addManualItem,
    clearShoppingList
  }
})