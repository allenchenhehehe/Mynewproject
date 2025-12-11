import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/myfridge/api'

export const useCommentStore = defineStore('comment', () => {
  // ==================== State ====================
  const comments = ref([])
  const userComments = ref([])
  const loading = ref(false)
  const error = ref(null)

  // ==================== Getters ====================
  const commentCount = computed(() => comments.value.length)

  // ==================== Actions ====================


  //取得食譜的所有評論
  async function fetchCommentsByRecipe(recipeId) {
    loading.value = true
    error.value = null

    try {

      const response = await axios.get(
        `${API_BASE_URL}/comments/recipe/${recipeId}`
      )

      // 轉換後端資料格式為前端格式
      comments.value = response.data.map(comment => ({
        id: comment.id,
        userId: comment.userId,
        recipeId: comment.recipeId,
        rating: comment.rating,
        text: comment.text,
        createdAt: comment.createdAt,
        updatedAt: comment.updatedAt,
        userName: comment.userName,
        recipeTitle: comment.recipeTitle
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


  //取得使用者的所有評論

  async function fetchUserComments() {
    loading.value = true
    error.value = null

    try {

      const response = await axios.get(
        `${API_BASE_URL}/comments/user`,
        { withCredentials: true }
      )

      userComments.value = response.data.map(comment => ({
        id: comment.id,
        userId: comment.userId,
        recipeId: comment.recipeId,
        rating: comment.rating,
        text: comment.text,
        createdAt: comment.createdAt,
        updatedAt: comment.updatedAt,
        userName: comment.userName,
        recipeTitle: comment.recipeTitle
      }))

      console.log('轉換後:', userComments.value)

      return { success: true }

    } catch (err) {
      console.error('載入失敗:', err)
      error.value = err.response?.data?.error || '載入失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }


  //新增評論

  async function addComment(recipeId, rating, text) {
    loading.value = true
    error.value = null

    try {

      const response = await axios.post(
        `${API_BASE_URL}/comments`,
        {
          recipeId,
          rating,
          text
        },
        { withCredentials: true }
      )

      // 重新載入食譜評論
      await fetchCommentsByRecipe(recipeId)

      return { success: true, comment: response.data }

    } catch (err) {
      console.error('新增失敗:', err)
      error.value = err.response?.data?.error || '新增失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }


  //更新評論

  async function updateComment(commentId, recipeId, rating, text) {
    loading.value = true
    error.value = null

    try {

      const response = await axios.put(
        `${API_BASE_URL}/comments/${commentId}`,
        {
          rating,
          text
        },
        { withCredentials: true }
      )

      // 重新載入食譜評論
      await fetchCommentsByRecipe(recipeId)

      return { success: true, message: response.data.message }

    } catch (err) {
      console.error('更新失敗:', err)
      error.value = err.response?.data?.error || '更新失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }


  //刪除評論

  async function deleteComment(commentId, recipeId) {
    loading.value = true
    error.value = null

    try {

      await axios.delete(
        `${API_BASE_URL}/comments/${commentId}`,
        { withCredentials: true }
      )

      // 重新載入食譜評論
      await fetchCommentsByRecipe(recipeId)

      return { success: true }

    } catch (err) {
      console.error('刪除失敗:', err)
      error.value = err.response?.data?.error || '刪除失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  //計算平均評分

  function getAverageRating() {
    if (comments.value.length === 0) return 0
    const sum = comments.value.reduce((acc, comment) => acc + comment.rating, 0)
    return (sum / comments.value.length).toFixed(1)
  }

  //清空評論列表
  function clearComments() {
    comments.value = []
  }

  // ==================== Return ====================
  return {
    // State
    comments,
    userComments,
    loading,
    error,

    // Getters
    commentCount,

    // Actions
    fetchCommentsByRecipe,
    fetchUserComments,
    addComment,
    updateComment,
    deleteComment,
    getAverageRating,
    clearComments
  }
})