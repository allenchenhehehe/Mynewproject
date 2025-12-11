import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/myfridge/api'

// 定義認證狀態常數
export const STATUS_LOGIN = 'login'
export const STATUS_SIGNUP = 'signup'
export const STATUS_APP = 'app'
export const STATUS_FORGET_PASSWORD = 'forgetPassword'
export const STATUS_ADMIN = 'admin'
export const STATUS_ADMIN_PANEL = 'adminPanel'

export const useAuthStore = defineStore('auth', () => {
  const initAuthStatus = () => {
    const user = localStorage.getItem('user')
    const adminUser = localStorage.getItem('adminUser')
    
    if (adminUser) {
      return STATUS_ADMIN_PANEL  // 管理員已登入
    } else if (user) {
      return STATUS_APP  // 一般使用者已登入
    }
    return STATUS_LOGIN  // 未登入
  }
  // State
  const authStatus = ref(initAuthStatus())
  const currentUser = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // 初始化時檢查 localStorage 中的使用者資訊
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    try {
      currentUser.value = JSON.parse(storedUser)
    } catch (e) {
      console.error('無法解析儲存的使用者資訊', e)
      localStorage.removeItem('user')
    }
  }

  // Getters
  const isAuthenticated = computed(() => !!currentUser.value)
  const isLoggedIn = computed(() => authStatus.value === STATUS_APP)

  async function checkAuth() {
  try {
    
    const response = await axios.get(
      `${API_BASE_URL}/auth/check`,
      { withCredentials: true }
    )
    
    // 更新使用者資訊
    currentUser.value = response.data
    localStorage.setItem('user', JSON.stringify(response.data))
    authStatus.value = STATUS_APP
    
    return true
    
  } catch (err) {
    console.log('未登入或 Session 過期')
    
    // 清除狀態
    currentUser.value = null
    localStorage.removeItem('user')
    authStatus.value = STATUS_LOGIN
    
    return false
  }
}
  // Actions
  async function login(email, password) {
    // TODO: 這裡應該呼叫後端 API
    try{
      const response = await axios.post(
        `${API_BASE_URL}/auth/login`,
        {
          email: email,
          password: password
        },
        {
          withCredentials: true,  // 重要!允許帶 Cookie
          headers: {
            'Content-Type': 'application/json'
          }
        }
      )
      currentUser.value = response.data
      localStorage.setItem('user', JSON.stringify(response.data))
      authStatus.value = STATUS_APP
      return { success: true, user: response.data }
    }catch (err) {
      console.error('登入失敗:', err)
      
      // 處理錯誤訊息
      if (err.response) {
        // 後端回傳的錯誤
        error.value = err.response.data?.error || '登入失敗'
      } else if (err.request) {
        // 請求發出但沒收到回應
        error.value = '無法連線到伺服器'
      } else {
        // 其他錯誤
        error.value = err.message
      }
      
      return { success: false, error: error.value }
      
    } finally {
      loading.value = false
    }
    
  }

  async function signup(userData) {
    loading.value = true
    error.value = null
    
    try {
  
      const response = await axios.post(
        `${API_BASE_URL}/auth/register`,
        userData,
        {
          withCredentials: true,
          headers: {
            'Content-Type': 'application/json'
          }
        }
      )
      
      return { success: true, user: response.data }
      
    } catch (err) {
      console.error('註冊失敗:', err)
      error.value = err.response?.data?.error || '註冊失敗'
      return { success: false, error: error.value }
      
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    loading.value = true
    
    try {
      await axios.post(
        `${API_BASE_URL}/auth/logout`,
        {},
        { withCredentials: true }
      )
      
    } catch (err) {
      console.error('登出請求失敗:', err)
    } finally {
      // 無論如何都清除本地狀態
      currentUser.value = null
      localStorage.removeItem('user')
      authStatus.value = STATUS_LOGIN
      loading.value = false
    }
  }

  function setAuthStatus(newStatus) {
    authStatus.value = newStatus
  }

  function forgetPassword(email) {
    // TODO: 這裡應該呼叫後端 API 重設密碼
    console.log('忘記密碼:', email)
    authStatus.value = STATUS_FORGET_PASSWORD
  }

  return {
    // State
    authStatus,
    currentUser,
    loading,
    error,
    // Getters
    isAuthenticated,
    isLoggedIn,
    // Actions
    login,
    signup,
    logout,
    setAuthStatus,
    forgetPassword,
    checkAuth
  }
})