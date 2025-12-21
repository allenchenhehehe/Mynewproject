import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from './authStore'

//加上這一行（如果還沒有）
const API_BASE_URL = 'http://localhost:8080/myfridge/api'

export const useAdminStore = defineStore('admin', () => {
  const adminUser = ref(null)
  const loading = ref(false)
  const error = ref(null)
  
  const recipes = ref([])
  const comments = ref([])
  const favorites = ref([])
  const users = ref([])
  
  const storedAdmin = localStorage.getItem('adminUser')
  if (storedAdmin) {
    try {
      adminUser.value = JSON.parse(storedAdmin)
    } catch (e) {
      console.error('無法解析管理員資訊', e)
      localStorage.removeItem('adminUser')
    }
  }

  const isAuthenticated = computed(() => !!adminUser.value)

  // ==================== 認證相關 ====================
  
  async function login(email, password) {
    loading.value = true
    error.value = null
    
    try {
      console.log('開始管理員登入...')
      
      const response = await axios.post(
        `${API_BASE_URL}/auth/login`,
        { email, password },
        { withCredentials: true }
      )
      
      console.log('登入回應:', response.data)
      
      const user = response.data
      
      if (user.role !== 'admin') {
        console.log('不是管理員:', user.role)
        error.value = '權限不足，僅限管理員登入'
        return { success: false, error: error.value }
      }
      
      adminUser.value = {
        id: user.id,
        username: user.userName,
        email: user.email,
        role: user.role
      }
      
      localStorage.setItem('adminUser', JSON.stringify(adminUser.value))
      
      console.log('管理員登入成功:', adminUser.value)
      return { success: true }
      
    } catch (err) {
      console.error('管理員登入失敗:', err)
      error.value = err.response?.data?.error || '登入失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      await axios.post(
        `${API_BASE_URL}/auth/logout`,
        {},
        { withCredentials: true }
      )
    } catch (err) {
      console.error('登出請求失敗:', err)
    } finally {
      adminUser.value = null
      localStorage.removeItem('adminUser')
    }
  }

  async function checkAuth() {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/auth/check`,
        { withCredentials: true }
      )
      
      const user = response.data
      
      if (user.role === 'admin') {
        adminUser.value = {
          id: user.id,
          username: user.userName,
          email: user.email,
          role: user.role
        }
        return true
      } else {
        logout()
        return false
      }
    } catch (err) {
      console.error('checkAuth 失敗:', err)
      logout()
      return false
    }
  }

  // ==================== 使用者管理 ====================
  
  async function fetchUsers() {
    loading.value = true
    error.value = null
    
    try {
      const response = await axios.get(
        `${API_BASE_URL}/admin/users`,
        { withCredentials: true }
      )
      
      users.value = response.data
      return { success: true }
    } catch (err) {
      console.error('載入使用者失敗:', err)
      error.value = err.response?.data?.error || '載入使用者失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  async function updateUserStatus(userId, status) {
    loading.value = true
    error.value = null
    
    try {
      await axios.patch(
        `${API_BASE_URL}/admin/users/${userId}/status`,
        { status },
        { withCredentials: true }
      )
      
      // 更新本地資料
      const user = users.value.find(u => u.id === userId)
      if (user) {
        user.status = status
      }
      
      return { success: true, message: '使用者狀態更新成功' }
    } catch (err) {
      console.error('更新使用者狀態失敗:', err)
      error.value = err.response?.data?.error || '更新狀態失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  async function deleteUser(userId) {
    loading.value = true
    error.value = null
    
    try {
      await axios.delete(
        `${API_BASE_URL}/admin/users/${userId}`,
        { withCredentials: true }
      )
      
      // 從本地移除
      users.value = users.value.filter(u => u.id !== userId)
      
      return { success: true, message: '使用者刪除成功' }
    } catch (err) {
      console.error('刪除使用者失敗:', err)
      error.value = err.response?.data?.error || '刪除使用者失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // ==================== 食譜管理 ====================
  
  async function fetchRecipes() {
    loading.value = true
    error.value = null
    
    try {
      // 改這裡
      const response = await axios.get(
        `${API_BASE_URL}/recipes`,
        { withCredentials: true }
      )
      
      recipes.value = response.data
      return { success: true }
    } catch (err) {
      console.error('載入食譜失敗:', err)
      error.value = err.response?.data?.error || '載入食譜失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  async function deleteRecipe(recipeId) {
    loading.value = true
    error.value = null
    
    try {
      // 改這裡
      await axios.delete(
        `${API_BASE_URL}/admin/recipes/${recipeId}`,
        { withCredentials: true }
      )
      
      recipes.value = recipes.value.filter(r => r.id !== recipeId)
      
      return { success: true, message: '食譜刪除成功' }
    } catch (err) {
      console.error('刪除食譜失敗:', err)
      error.value = err.response?.data?.error || '刪除食譜失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  async function generateRandomRecipe(cuisine = '') {
  loading.value = true
  error.value = null
  
  try {
    const params = cuisine ? `?cuisine=${encodeURIComponent(cuisine)}` : ''
    const response = await axios.get(
      `${API_BASE_URL}/admin/recipes/generate-random${params}`,
      { withCredentials: true }
    )
    
    return { success: true, data: response.data }
  } catch (err) {
    console.error('生成食譜失敗:', err)
    error.value = err.response?.data?.error || '生成食譜失敗'
    return { success: false, error: error.value }
  } finally {
    loading.value = false
  }
}

async function createRecipe(recipeData) {
  loading.value = true
  error.value = null
  
  try {
    const response = await axios.post(
      `${API_BASE_URL}/admin/recipes`,
      recipeData,
      { withCredentials: true }
    )
    
    return { success: true, data: response.data }
  } catch (err) {
    console.error('新增食譜失敗:', err)
    error.value = err.response?.data?.error || '新增食譜失敗'
    return { success: false, error: error.value }
  } finally {
    loading.value = false
  }
}

  // ==================== 評論管理 ====================
  
  async function fetchComments() {
    loading.value = true
    error.value = null
    
    try {
      // 改這裡
      const response = await axios.get(
        `${API_BASE_URL}/admin/comments`,
        { withCredentials: true }
      )
      
      comments.value = response.data
      return { success: true }
    } catch (err) {
      console.error('載入評論失敗:', err)
      error.value = err.response?.data?.error || '載入評論失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  async function deleteComment(commentId) {
    loading.value = true
    error.value = null
    
    try {
      //改這裡
      await axios.delete(
        `${API_BASE_URL}/admin/comments/${commentId}`,
        { withCredentials: true }
      )
      
      comments.value = comments.value.filter(c => c.id !== commentId)
      
      return { success: true, message: '評論刪除成功' }
    } catch (err) {
      console.error('刪除評論失敗:', err)
      error.value = err.response?.data?.error || '刪除評論失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // ==================== 收藏管理 ====================
  
  async function fetchFavorites() {
    loading.value = true
    error.value = null
    
    try {
      //改這裡
      const response = await axios.get(
        `${API_BASE_URL}/admin/favorites`,
        { withCredentials: true }
      )
      
      favorites.value = response.data
      return { success: true }
    } catch (err) {
      console.error('載入收藏失敗:', err)
      error.value = err.response?.data?.error || '載入收藏失敗'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  return {
    adminUser,
    loading,
    error,
    recipes,
    comments,
    favorites,
    isAuthenticated,
    users,
    login,
    logout,
    checkAuth,
    fetchRecipes,
    deleteRecipe,
    fetchComments,
    deleteComment,
    fetchFavorites,
    fetchUsers,  // 新增
    updateUserStatus,  //新增
    deleteUser,  //新增
    generateRandomRecipe,  //新增
    createRecipe
  }
})