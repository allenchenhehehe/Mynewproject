import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/myfridge/api'

export const useFridgeStore = defineStore('fridge', () => {
  // State
  const fridgeItems = ref([])
  const loading = ref(false)
  const error = ref(null)

  // Getters
  const itemCount = computed(() => fridgeItems.value.length)

  const categorizedItems = computed(() => {
    const categories = {}
    fridgeItems.value.forEach((item) => {
      if (!categories[item.category]) {
        categories[item.category] = []
      }
      categories[item.category].push(item)
    })
    return categories
  })

  const expiringItems = computed(() => {
    const today = new Date()
    return fridgeItems.value.filter((item) => {
      if (!item.expired_date) return false
      const expiryDate = new Date(item.expired_date)
      const daysLeft = Math.ceil((expiryDate - today) / (1000 * 60 * 60 * 24))
      return daysLeft <= 7 && daysLeft > 0
    })
  })

  const expiredItems = computed(() => {
    const today = new Date()
    return fridgeItems.value.filter((item) => {
      if (!item.expired_date) return false
      const expiryDate = new Date(item.expired_date)
      return expiryDate < today
    })
  })

  // Actions

  async function fetchItems() {
    loading.value = true
    error.value = null

    try {
      console.log('載入冰箱食材...')

      const response = await axios.get(
        `${API_BASE_URL}/fridge-items`,
        { withCredentials: true }
      )

      console.log('載入成功:', response.data)

      // 轉換後端資料格式為前端格式
      fridgeItems.value = response.data.map(item => ({
        id: item.id,
        ingredient_id: item.ingredientId,
        name: item.ingredientName,
        category: item.category,
        quantity: item.amount,
        unit: item.unit,
        purchased_date: item.purchasedDate,
        expired_date: item.expiredDate
      }))
      return { success: true }

    } catch (err) {
      console.error('載入失敗:', err)
      error.value = err.response?.data?.error || '載入失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  async function addItem(item) {
    loading.value = true
    error.value = null

    try {
      console.log('新增食材:', item)

      const response = await axios.post(
        `${API_BASE_URL}/fridge-items`,
        {
          ingredientName: item.name,
          category: item.category,
          amount: item.quantity,
          unit: item.unit,
          purchasedDate: item.purchased_date,
          expiredDate: item.expired_date || null
        },
        { withCredentials: true }
      )

      console.log('新增成功:', response.data)

      // 重新載入列表
      await fetchItems()

      return { success: true, data: response.data }

    } catch (err) {
      console.error('新增失敗:', err)
      error.value = err.response?.data?.error || '新增失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  async function updateItem(itemId, updatedData) {
    loading.value = true
    error.value = null

    try {
      console.log('更新食材:', itemId, updatedData)

      const response = await axios.put(
        `${API_BASE_URL}/fridge-items/${itemId}`,
        {
          amount: updatedData.quantity,
          unit: updatedData.unit,
          purchasedDate: updatedData.purchased_date,
          expiredDate: updatedData.expired_date || null
        },
        { withCredentials: true }
      )

      console.log('更新成功:', response.data)

      // 重新載入列表
      await fetchItems()

      return { success: true }

    } catch (err) {
      console.error('更新失敗:', err)
      error.value = err.response?.data?.error || '更新失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  async function deleteItem(itemId) {
    loading.value = true
    error.value = null

    try {
      console.log('刪除食材:', itemId)

      await axios.delete(
        `${API_BASE_URL}/fridge-items/${itemId}`,
        { withCredentials: true }
      )

      console.log('刪除成功')

      // 重新載入列表
      await fetchItems()

      return { success: true }

    } catch (err) {
      console.error(' 刪除失敗:', err)
      error.value = err.response?.data?.error || '刪除失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  async function clearExpiredItems() {
    const expiredItemIds = expiredItems.value.map(item => item.id)

    if (expiredItemIds.length === 0) {
      return { success: true, message: '沒有過期食材' }
    }

    loading.value = true

    try {
      // 逐一刪除過期食材
      for (const id of expiredItemIds) {
        await axios.delete(
          `${API_BASE_URL}/fridge-items/${id}`,
          { withCredentials: true }
        )
      }

      console.log(`清除了 ${expiredItemIds.length} 個過期食材`)

      // 重新載入列表
      await fetchItems()

      return { success: true, count: expiredItemIds.length }

    } catch (err) {
      console.error('清除失敗:', err)
      error.value = err.response?.data?.error || '清除失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  function getItemsByCategory(category) {
    return fridgeItems.value.filter((item) => item.category === category)
  }


 


  return {
    // State
    fridgeItems,
    loading,
    error,
    // Getters
    itemCount,
    categorizedItems,
    expiringItems,
    expiredItems,
    // Actions
    fetchItems,
    addItem,
    deleteItem,
    updateItem,
    getItemsByCategory,
    clearExpiredItems
  }
})