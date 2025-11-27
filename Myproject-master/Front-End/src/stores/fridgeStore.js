import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// Mock 數據 - 冰箱食材
const mockFridgeData = [
    {
        id: 1,
        ingredient_id: 1,
        name: '洋蔥',
        category: 'vegetable',
        quantity: 5,
        unit: '個',
        purchased_date: '2025-11-03',
        expired_date: '2025-11-12',
    },
    {
        id: 2,
        ingredient_id: 2,
        name: '雞肉',
        category: 'meat',
        quantity: 200,
        unit: '克',
        purchased_date: '2025-11-05',
        expired_date: '2025-11-30',
    },
    {
        id: 3,
        ingredient_id: 3,
        name: '雞蛋',
        category: 'egg',
        quantity: 5,
        unit: '顆',
        purchased_date: '2025-11-08',
        expired_date: '2025-11-30',
    },
    {
        id: 4,
        ingredient_id: 4,
        name: '番茄',
        category: 'vegetable',
        quantity: 5,
        unit: '個',
        purchased_date: '2025-11-03',
        expired_date: '2025-11-30',
    },
    {
        id: 5,
        ingredient_id: 4,
        name: '番茄',
        category: 'vegetable',
        quantity: 3,
        unit: '個',
        purchased_date: '2025-11-30',
        expired_date: '2025-12-5',
    },
    {
        id: 6,
        ingredient_id: 101,
        name: '蔥花',
        category: 'vegetable',
        quantity: 3,
        unit: '匙',
        purchased_date: '2025-11-30',
        expired_date: '2025-12-5',
    },
]

export const useFridgeStore = defineStore('fridge', () => {
  // State
  const fridgeItems = ref([...mockFridgeData])

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
  function addItem(item) {
    const newItem = {
      id: Date.now(),
      ...item
    }
    fridgeItems.value.push(newItem)
    console.log('新增食材到冰箱:', item.name)
  }

  function deleteItem(itemId) {
    const index = fridgeItems.value.findIndex((item) => item.id === itemId)
    if (index > -1) {
      const deletedItem = fridgeItems.value[index]
      fridgeItems.value.splice(index, 1)
      console.log('刪除食材:', deletedItem.name)
    }
  }

  function updateItem(itemId, updatedData) {
    const item = fridgeItems.value.find((item) => item.id === itemId)
    if (item) {
      Object.assign(item, updatedData)
      console.log('更新食材:', item.name)
    }
  }

  function updateFridge(items) {
    // 清空冰箱並添加新的食材清單
    fridgeItems.value = items.map((item) => ({
      id: item.id || Date.now() + Math.random(),
      ...item
    }))
    console.log('更新冰箱清單，現有', fridgeItems.value.length, '個食材')
  }

  function addMultipleItems(itemsArray) {
    itemsArray.forEach((item) => {
      addItem(item)
    })
  }

  function getItemsByCategory(category) {
    return fridgeItems.value.filter((item) => item.category === category)
  }

  function clearExpiredItems() {
    const beforeCount = fridgeItems.value.length
    fridgeItems.value = fridgeItems.value.filter((item) => {
      if (!item.expired_date) return true
      const expiryDate = new Date(item.expired_date)
      const today = new Date()
      return expiryDate >= today
    })
    console.log('清除過期食材，刪除了', beforeCount - fridgeItems.value.length, '個項目')
  }

  return {
    // State
    fridgeItems,
    // Getters
    itemCount,
    categorizedItems,
    expiringItems,
    expiredItems,
    // Actions
    addItem,
    deleteItem,
    updateItem,
    updateFridge,
    addMultipleItems,
    getItemsByCategory,
    clearExpiredItems
  }
})