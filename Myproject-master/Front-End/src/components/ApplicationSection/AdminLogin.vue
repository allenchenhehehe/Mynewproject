<script setup>
import { ref } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { useAuthStore, STATUS_LOGIN, STATUS_ADMIN_PANEL } from '@/stores/authStore'

const adminStore = useAdminStore()
const authStore = useAuthStore()

const email = ref('admin@stockandstove.com')
const password = ref('admin123')
const errorMessage = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!email.value || !password.value) {
    errorMessage.value = '請輸入帳號和密碼'
    return
  }

  errorMessage.value = ''
  loading.value = true

  try {
    const result = await adminStore.login(email.value, password.value)
    
    if (result.success) {
      authStore.setAuthStatus(STATUS_ADMIN_PANEL)
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
  authStore.setAuthStatus(STATUS_LOGIN)
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-orange-400 via-red-400 to-pink-400 flex items-center justify-center px-4 py-8">
    <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] w-full max-w-md p-8">
      
      <div class="text-center mb-6">
        <h1 class="text-4xl font-black uppercase tracking-tighter mb-1">
          ADMIN LOGIN
        </h1>
        <p class="text-sm font-bold text-gray-600">管理員登入</p>
      </div>

      <div
        v-if="errorMessage"
        class="bg-red-100 border-2 border-red-500 text-red-700 px-4 py-3 mb-6 font-bold"
      >
        {{ errorMessage }}
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block mb-2 text-sm font-black uppercase tracking-wider">
            EMAIL
          </label>
          <input
            v-model="email"
            type="email"
            required
            class="w-full border-2 border-black px-4 py-3 bg-gray-50 focus:bg-white focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none font-bold"
            placeholder="admin@stockandstove.com"
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
            placeholder="請輸入密碼"
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

      <button
        @click="goBack"
        class="mt-6 w-full bg-gray-100 border-2 border-black py-3 font-bold uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
      >
        BACK TO LOGIN
      </button>
    </div>
  </div>
</template>