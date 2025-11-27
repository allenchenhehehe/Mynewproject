<script setup>
import { computed } from 'vue'
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

// 導入所有 stores
import {
  useAuthStore,
  STATUS_LOGIN,
  STATUS_SIGNUP,
  STATUS_APP,
  STATUS_FORGET_PASSWORD
} from './stores'
import { useNavigationStore } from './stores'
import { useFridgeStore } from './stores'
import { useShoppingStore } from './stores'
import { useUserStore } from './stores'

// 初始化所有 stores
const authStore = useAuthStore()
const navStore = useNavigationStore()
const fridgeStore = useFridgeStore()
const shoppingStore = useShoppingStore()
const userStore = useUserStore()

// ==================== 認證相關 ====================
const gotoSignup = () => authStore.setAuthStatus(STATUS_SIGNUP)
const gotoLogin = () => authStore.setAuthStatus(STATUS_LOGIN)
const gotoApp = () => authStore.setAuthStatus(STATUS_APP)
const gotoForget = () => authStore.setAuthStatus(STATUS_FORGET_PASSWORD)
</script>

<template>
  <Login 
    v-if="authStore.status === STATUS_LOGIN"
    @signup="gotoSignup" 
    @navbar="gotoApp" 
    @forgetpassword="gotoForget" 
  />
  <SignUp 
    v-if="authStore.status === STATUS_SIGNUP"
    @login="gotoLogin" 
  />
  <ForgetPassword 
    v-if="authStore.status === STATUS_FORGET_PASSWORD"
    @login="gotoLogin" 
  />
  <div v-if="authStore.status === STATUS_APP" class="min-h-screen bg-[#fefae0] text-gray-800 overflow-x-hidden">
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
</template>