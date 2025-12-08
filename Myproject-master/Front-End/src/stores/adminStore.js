import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  // ==================== State ====================
  const isAdminLoggedIn = ref(false)
  const adminUser = ref(null)
  
  // 初始化:檢查 localStorage
  const storedAdmin = localStorage.getItem('adminUser')
  if (storedAdmin) {
    try {
      adminUser.value = JSON.parse(storedAdmin)
      isAdminLoggedIn.value = true
    } catch (e) {
      console.error('無法解析管理員資訊', e)
      localStorage.removeItem('adminUser')
    }
  }

  // ==================== Getters ====================
  const isAuthenticated = computed(() => isAdminLoggedIn.value && !!adminUser.value)

  // ==================== Actions ====================
  
  /**
   * 管理員登入
   */
  async function login(username, password) {
    try {
      // TODO: 替換成真實 API
    //   const response = await fetch('http://localhost:8080/myfridge/api/admin/auth/login', {
    //     method: 'POST',
    //     headers: { 'Content-Type': 'application/json' },
    //     credentials: 'include',  // ✅ 重要!自動處理 Cookie
    //     body: JSON.stringify({ username, password })
    //   })

    //   const result = await response.json()

    //   if (result.success) {
    //     // ✅ 登入成功
    //     adminUser.value = result.admin
    //     isAdminLoggedIn.value = true
    //     localStorage.setItem('adminUser', JSON.stringify(result.admin))
        
    //     return { success: true }
    //   } else {
    //     // ❌ 登入失敗
    //     return { success: false, error: result.error || '登入失敗' }
    //   }
    if (username === 'admin' && password === 'admin123') {
        // 登入成功
        adminUser.value = {
          id: 1,
          username: 'admin',
          role: 'super_admin'
        }
        isAdminLoggedIn.value = true
        localStorage.setItem('adminUser', JSON.stringify(adminUser.value))
        
        console.log('✅ 登入成功:', adminUser.value)
        return { success: true }
      } else {
        // 登入失敗
        console.log('❌ 登入失敗:帳號或密碼錯誤')
        return { success: false, error: '帳號或密碼錯誤' }
      }
    } catch (error) {
      console.error('登入錯誤:', error)
      return { success: false, error: '連線失敗,請稍後再試' }
    }
  }

  /**
   * 管理員登出
   */
//   
function logout() {
    console.log('🧪 Demo 模式登出')
    
    adminUser.value = null
    isAdminLoggedIn.value = false
    localStorage.removeItem('adminUser')
  }

  /**
   * 檢查登入狀態
   */
//   async function checkAuth() {
//     try {
//       // TODO: 替換成真實 API
//       const response = await fetch('http://localhost:8080/myfridge/api/admin/auth/check', {
//         credentials: 'include'
//       })

//       if (response.ok) {
//         return true
//       } else {
//         // Session 已過期
//         logout()
//         return false
//       }
//     } catch (error) {
//       console.error('驗證失敗:', error)
//       return false
//     }
//   }
function checkAuth() {
    console.log('🧪 Demo 模式檢查登入:', isAuthenticated.value)
    return isAuthenticated.value
  }

  // ==================== Return ====================
  return {
    // State
    isAdminLoggedIn,
    adminUser,
    
    // Getters
    isAuthenticated,
    
    // Actions
    login,
    logout,
    checkAuth
  }
})