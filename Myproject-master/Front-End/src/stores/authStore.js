import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 定義認證狀態常數
export const STATUS_LOGIN = 'login'
export const STATUS_SIGNUP = 'signup'
export const STATUS_APP = 'app'
export const STATUS_FORGET_PASSWORD = 'forgetPassword'

export const useAuthStore = defineStore('auth', () => {
  // State
  const authStatus = ref(STATUS_LOGIN)
  const currentUser = ref(null)
  const token = ref(localStorage.getItem('token') || null)

  // 初始化時檢查 localStorage 中的使用者資訊
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    try {
      currentUser.value = JSON.parse(storedUser)
    } catch (e) {
      console.error('無法解析儲存的使用者資訊', e)
    }
  }

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const isLoggedIn = computed(() => authStatus.value === STATUS_APP)

  // Actions
  function login(email, password) {
    // TODO: 這裡應該呼叫後端 API
    console.log('登入:', email, password)
    currentUser.value = {
      id: 1,
      email: email,
      username: email.split('@')[0]
    }
    token.value = 'dummy-token-' + Date.now()
    localStorage.setItem('user', JSON.stringify(currentUser.value))
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', currentUser.value.id)
    authStatus.value = STATUS_APP
  }

  function signup(email, password, username) {
    // TODO: 這裡應該呼叫後端 API
    console.log('註冊:', email, password, username)
    currentUser.value = {
      id: Date.now(),
      email: email,
      username: username
    }
    token.value = 'dummy-token-' + Date.now()
    localStorage.setItem('user', JSON.stringify(currentUser.value))
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', currentUser.value.id)
    authStatus.value = STATUS_APP
  }

  function logout() {
    currentUser.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('userId')
    authStatus.value = STATUS_LOGIN
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
    token,
    // Getters
    isAuthenticated,
    isLoggedIn,
    // Actions
    login,
    signup,
    logout,
    setAuthStatus,
    forgetPassword
  }
})