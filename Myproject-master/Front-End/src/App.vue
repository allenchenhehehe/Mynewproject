<script setup>
import { computed, onMounted } from 'vue'
import Login from './components/Users/Login.vue'
import SignUp from './components/Users/SignUp.vue'
import Navbar from './components/Users/Navbar.vue'
import ForgetPassword from './components/Users/ForgetPassword.vue'
import Home from './components/ApplicationSection/Home.vue'
import MyFridge from './components/ApplicationSection/MyFridge.vue'
import Recipes from './components/ApplicationSection/Recipes.vue'
import RecipeDetail from './components/ApplicationSection/RecipeDetail.vue'
import ShoppingList from './components/ApplicationSection/ShoppingList.vue'
import Favorites from './components/ApplicationSection/Favorites.vue'
import MyComments from './components/ApplicationSection/MyComments.vue'
import AdminLogin from './components/ApplicationSection/AdminLogin.vue'
import AdminPanel from './components/ApplicationSection/AdminPanel.vue'
// 導入所有 stores
import {
  useAuthStore,
  STATUS_LOGIN,
  STATUS_SIGNUP,
  STATUS_APP,
  STATUS_FORGET_PASSWORD,
  STATUS_ADMIN,
  STATUS_ADMIN_PANEL
} from './stores'
import { useNavigationStore } from './stores'
import { useFridgeStore } from './stores'
import { useShoppingStore } from './stores'
import { useUserStore } from './stores'
import { useAdminStore } from './stores'


// 初始化所有 stores
const authStore = useAuthStore()
const navStore = useNavigationStore()
const fridgeStore = useFridgeStore()
const shoppingStore = useShoppingStore()
const userStore = useUserStore()
const adminStore = useAdminStore()

// ==================== 認證相關 ====================
const gotoSignup = () => authStore.setAuthStatus(STATUS_SIGNUP)
const gotoLogin = () => authStore.setAuthStatus(STATUS_LOGIN)
const gotoApp = () => authStore.setAuthStatus(STATUS_APP)
const gotoForget = () => authStore.setAuthStatus(STATUS_FORGET_PASSWORD)
const goToAdminLogin = () => {
  // 先登出使用者
  authStore.logout()
  // 設定為管理員登入狀態
  authStore.setAuthStatus(STATUS_ADMIN)
}
onMounted(async () => {
  console.log('App mounted, checking auth...')
  
  // 如果 localStorage 有 user,驗證 Session 是否還有效
  if (authStore.authStatus === STATUS_APP) {
    const isValid = await authStore.checkAuth()
    
    if (!isValid) {
      console.log('Session 已過期,回到登入頁')
      // checkAuth() 已經設定 authStatus = STATUS_LOGIN
    }
  }
})
</script>

<template>
  <Login 
    v-if="authStore.authStatus === STATUS_LOGIN"
    @signup="gotoSignup" 
    @navbar="gotoApp" 
    @forgetpassword="gotoForget" 
    @admin="goToAdminLogin"
  />
  <SignUp 
    v-if="authStore.authStatus === STATUS_SIGNUP"
    @login="gotoLogin" 
  />
  <ForgetPassword 
    v-if="authStore.authStatus === STATUS_FORGET_PASSWORD"
    @login="gotoLogin" 
  />
  <div v-if="authStore.authStatus === STATUS_APP" class="min-h-screen bg-[#fefae0] text-gray-800 overflow-x-hidden">
    <Navbar />
    <!-- 首頁 -->
    <Home 
      v-show="navStore.currentPage === 'Home'"
    />
    <!-- 冰箱 -->
    <MyFridge 
      v-show="navStore.currentPage === 'MyFridge'"
    />
    <!-- 食譜列表 -->
    <Recipes 
      v-show="navStore.currentPage === 'Recipes'"
    />
    <!-- 食譜詳情 -->
    <RecipeDetail 
      v-show="navStore.currentPage === 'RecipeDetail'"    
    />
    <!-- 購物清單 -->
    <ShoppingList 
      v-show="navStore.currentPage === 'ShoppingList'"
    />
    <!-- 收藏食譜 -->
    <Favorites 
      v-show="navStore.currentPage === 'Favorites'"
    />
    <!-- 我的評論 -->
    <MyComments 
      v-show="navStore.currentPage === 'MyComments'"
    />
  </div>
  <!--  新增:管理員登入 -->
  <AdminLogin v-if="authStore.authStatus === STATUS_ADMIN" />
  
  <!--  新增:管理員後台 -->
  <AdminPanel v-if="authStore.authStatus === STATUS_ADMIN_PANEL" />
</template>