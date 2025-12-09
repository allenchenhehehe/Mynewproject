<!-- <script setup>
import { ref } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { useNavigationStore } from '@/stores/navigationStore'

const adminStore = useAdminStore()
const navStore = useNavigationStore()

const username = ref('admin')
const password = ref('admin123')
const errorMessage = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMessage.value = '請輸入帳號和密碼'
    return
  }

  errorMessage.value = ''
  loading.value = true

  try {
    const result = await adminStore.login(username.value, password.value)
    
    if (result.success) {
      navStore.goToAdminPanel()  // 登入成功 → 跳轉後台
    } else {
      errorMessage.value = result.error || '登入失敗'
    }
  } catch (error) {
    errorMessage.value = '登入失敗: ' + error.message
  } finally {
    loading.value = false
  }
}

function goBack() {
  navStore.goHome()  // 回到首頁
}
</script> -->

<!-- <template>
  <div class="min-h-screen bg-gradient-to-br from-orange-400 via-red-400 to-pink-400 flex items-center justify-center px-4 py-8">
    <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] w-full max-w-md p-8">
      
      
      <div class="text-center mb-6">
        <div class="text-6xl mb-2">🔐</div>
        <h1 class="text-4xl font-black uppercase tracking-tighter mb-1">
          ADMIN LOGIN
        </h1>
        <p class="text-sm font-bold text-gray-600">管理員登入</p>
      </div>

      
      <div
        v-if="errorMessage"
        class="bg-red-100 border-2 border-red-500 text-red-700 px-4 py-3 mb-6 font-bold"
      >
        ⚠️ {{ errorMessage }}
      </div>

      
      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block mb-2 text-sm font-black uppercase tracking-wider">
            USERNAME
          </label>
          <input
            v-model="username"
            type="text"
            required
            class="w-full border-2 border-black px-4 py-3 bg-gray-50 focus:bg-white focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none font-bold"
            placeholder="admin"
          />
        </div>

        <div>
          <label class="block mb-2 text-sm font-black uppercase tracking-wider">
            PASSWORD
          </label>
          <input
            v-model="password"
            type="password"
            required
            class="w-full border-2 border-black px-4 py-3 bg-gray-50 focus:bg-white focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none font-bold"
            placeholder="••••••••"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-orange-300 border-2 border-black py-4 font-black uppercase text-lg shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ loading ? 'LOGGING IN...' : 'LOGIN' }}
        </button>
      </form>

      
      <div class="mt-6 text-center">
        <div class="bg-yellow-100 border-2 border-black px-3 py-2 text-xs font-bold inline-block">
          🎮 DEMO: admin / admin123
        </div>
      </div>

      
      <button
        @click="goBack"
        class="mt-6 w-full bg-gray-100 border-2 border-black py-3 font-bold uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
      >
        ← BACK TO HOME
      </button>
    </div>
  </div>
</template> -->
<script setup>
import { ref } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { useAuthStore, STATUS_LOGIN, STATUS_ADMIN_PANEL } from '@/stores/authStore'

const adminStore = useAdminStore()
const authStore = useAuthStore()

const username = ref('admin')
const password = ref('admin123')
const errorMessage = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMessage.value = '請輸入帳號和密碼'
    return
  }

  errorMessage.value = ''
  loading.value = true

  try {
    const result = await adminStore.login(username.value, password.value)
    
    if (result.success) {
      authStore.setAuthStatus(STATUS_ADMIN_PANEL)  // 登入成功 → 跳轉後台
    } else {
      errorMessage.value = result.error || '登入失敗'
    }
  } catch (error) {
    errorMessage.value = '登入失敗: ' + error.message
  } finally {
    loading.value = false
  }
}

function goBack() {
  // ✅ 回到使用者登入頁面
  authStore.setAuthStatus(STATUS_LOGIN)
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-orange-400 via-red-400 to-pink-400 flex items-center justify-center px-4 py-8">
    <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] w-full max-w-md p-8">
      
      <!-- Logo -->
      <div class="text-center mb-6">
        <div class="text-6xl mb-2">🔐</div>
        <h1 class="text-4xl font-black uppercase tracking-tighter mb-1">
          ADMIN LOGIN
        </h1>
        <p class="text-sm font-bold text-gray-600">管理員登入</p>
      </div>

      <!-- 錯誤訊息 -->
      <div
        v-if="errorMessage"
        class="bg-red-100 border-2 border-red-500 text-red-700 px-4 py-3 mb-6 font-bold"
      >
        ⚠️ {{ errorMessage }}
      </div>

      <!-- 登入表單 -->
      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block mb-2 text-sm font-black uppercase tracking-wider">
            USERNAME
          </label>
          <input
            v-model="username"
            type="text"
            required
            class="w-full border-2 border-black px-4 py-3 bg-gray-50 focus:bg-white focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none font-bold"
            placeholder="admin"
          />
        </div>

        <div>
          <label class="block mb-2 text-sm font-black uppercase tracking-wider">
            PASSWORD
          </label>
          <input
            v-model="password"
            type="password"
            required
            class="w-full border-2 border-black px-4 py-3 bg-gray-50 focus:bg-white focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none font-bold"
            placeholder="••••••••"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-orange-300 border-2 border-black py-4 font-black uppercase text-lg shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ loading ? 'LOGGING IN...' : 'LOGIN' }}
        </button>
      </form>

      <!-- Demo 提示 -->
      <div class="mt-6 text-center">
        <div class="bg-yellow-100 border-2 border-black px-3 py-2 text-xs font-bold inline-block">
          🎮 DEMO: admin / admin123
        </div>
      </div>

      <!-- 返回按鈕 -->
      <button
        @click="goBack"
        class="mt-6 w-full bg-gray-100 border-2 border-black py-3 font-bold uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
      >
        ← BACK TO LOGIN
      </button>
    </div>
  </div>
</template>