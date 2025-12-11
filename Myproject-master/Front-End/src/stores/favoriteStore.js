import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/myfridge/api'

export const useFavoriteStore = defineStore('favorite', () => {
  // ==================== State ====================
  const favorites = ref([])
  const loading = ref(false)
  const error = ref(null)

  // ==================== Getters ====================
  const favoriteCount = computed(() => favorites.value.length)

  const favoriteRecipeIds = computed(() => {
    return favorites.value.map(fav => fav.recipeId)
  })

  // ==================== Actions ====================


   //取得使用者的所有收藏

  async function fetchFavorites() {
    loading.value = true
    error.value = null

    try {

      const response = await axios.get(
        `${API_BASE_URL}/favorites`,
        { withCredentials: true }
      )

      console.log('後端回傳:', response.data)

      // 轉換後端資料格式為前端格式
      favorites.value = response.data.map(fav => ({
        id: fav.id,
        userId: fav.userId,
        recipeId: fav.recipeId,
        createdAt: fav.savedAt,
        
        // 食譜資訊
        recipeTitle: fav.title,
        recipeDescription: fav.description,
        recipeImageUrl: fav.imageUrl,
        recipeCookingTime: fav.cookingTime,
        recipeDifficulty: fav.difficulty
      }))

      console.log('轉換後:', favorites.value)

      return { success: true }

    } catch (err) {
      console.error('載入失敗:', err)
      error.value = err.response?.data?.error || '載入失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }


   //檢查特定食譜是否已收藏

  async function checkFavorite(recipeId) {
    try {

      const response = await axios.get(
        `${API_BASE_URL}/favorites/check/${recipeId}`,
        { withCredentials: true }
      )

      console.log('收藏狀態:', response.data.isFavorited)

      return response.data.isFavorited

    } catch (err) {
      console.error('檢查失敗:', err)
      return false
    }
  }

  /**
   * 切換收藏狀態（在食譜詳情頁使用）
   */
  async function toggleFavorite(recipeId) {
    loading.value = true
    error.value = null

    try {
      console.log('切換收藏狀態:', recipeId)

      const response = await axios.post(
        `${API_BASE_URL}/favorites/toggle/${recipeId}`,
        {},
        { withCredentials: true }
      )

      console.log('切換成功:', response.data)

      // 更新本地收藏列表
      await fetchFavorites()

      return {
        success: true,
        isFavorited: response.data.isFavorited,
        message: response.data.message
      }

    } catch (err) {
      console.error('切換失敗:', err)
      error.value = err.response?.data?.error || '操作失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }


   // 刪除收藏（在我的收藏頁使用）

  async function removeFavorite(recipeId) {
    loading.value = true
    error.value = null

    try {

      await axios.delete(
        `${API_BASE_URL}/favorites/${recipeId}`,
        { withCredentials: true }
      )

      console.log('刪除成功')

      // 更新本地收藏列表
      await fetchFavorites()

      return { success: true }

    } catch (err) {
      console.error('刪除失敗:', err)
      error.value = err.response?.data?.error || '刪除失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }


   //本地方法：檢查是否已收藏（不發請求，從本地列表檢查）

  function isFavorited(recipeId) {
    return favoriteRecipeIds.value.includes(recipeId)
  }

  // ==================== Return ====================
  return {
    // State
    favorites,
    loading,
    error,

    // Getters
    favoriteCount,
    favoriteRecipeIds,

    // Actions
    fetchFavorites,
    checkFavorite,
    toggleFavorite,
    removeFavorite,
    isFavorited
  }
})