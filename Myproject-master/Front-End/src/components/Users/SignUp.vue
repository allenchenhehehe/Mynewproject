<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const emits = defineEmits(['login'])

const email = ref('')
const username = ref('')
const password = ref('')
const errorMessage = ref('')
const errorVisible = ref(false)

async function signup() {
    errorMessage.value = ''

    // 驗證輸入
    if (!username.value || !email.value || !password.value) {
    showErrorTemporarily('請填寫所有欄位')
    return
    }

    // 驗證 email 格式
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(email.value)) {
    showErrorTemporarily('Email 格式不正確')
    return
    }

    // 驗證密碼長度
    if (password.value.length < 6) {
    showErrorTemporarily('密碼至少需要 6 個字元')
    return
    }

    // 呼叫 authStore 註冊
    const result = await authStore.signup({
        userName: username.value,
        email: email.value,
        password: password.value
    })

    if (!result.success) {
    errorMessage.value = result.error
    }
    // 註冊成功後 authStore 會自動切換到 STATUS_APP
    // App.vue 會自動切換到主系統
}
function showErrorTemporarily(message, duration = 1000) {  // 預設 1 秒
  errorMessage.value = message
  errorVisible.value = true
  
  // 在指定時間後清除
  setTimeout(() => {
    errorVisible.value = false
    
    // 動畫結束後才清除訊息
    setTimeout(() => {
      errorMessage.value = ''
    }, 500)  // 等待淡出動畫完成 (0.3秒)
    
  }, duration)
}
</script>

<template>
    <div class="min-h-screen flex items-center justify-center p-4 bg-cover bg-center" style="background-image: url('/chef.webp')">
        <div class="max-w-lg w-full">
            <!-- 主容器 -->
            <div class="border-4 border-black bg-white shadow-[8px_8px_0px_0px_black] p-8 space-y-6">
                <!-- 標題 -->
                <div class="text-center mb-4">
                    <h1 class="text-5xl font-black uppercase tracking-tighter mb-2">SIGN UP</h1>
                    <p class="text-sm text-gray-600 font-bold uppercase tracking-wide">加入 Stock & Stove 社區</p>
                </div>

                <!-- 錯誤訊息 -->
                <div 
                    v-if="errorMessage" 
                    class="bg-red-100 border-2 border-red-500 text-red-700 px-4 py-3 font-bold flex items-center justify-center"
                >
                    {{ errorMessage }}
                </div>

                <!-- 表單 -->
                <form class="space-y-6">
                    <!-- Username 欄位 -->
                    <div class="space-y-2">
                        <label class="text-md font-black uppercase tracking-wide text-gray-600 block">Username</label>
                        <div class="border-2 border-black bg-green-50 flex items-center px-4 py-3 focus-within:bg-green-100 transition-all space-x-4">
                            <Icon icon="mdi:account-plus" class="text-3xl text-black" />
                            <input
                                v-model="username"
                                type="text"
                                placeholder="你的使用者名稱..."
                                class="flex-1 bg-transparent outline-none font-bold text-black placeholder-gray-500"
                            />
                        </div>
                    </div>

                    <!-- Email 欄位 -->
                    <div class="space-y-2">
                        <label class="text-md font-black uppercase tracking-wide text-gray-600 block">Email</label>
                        <div class="border-2 border-black bg-yellow-50 flex items-center px-4 py-3 focus-within:bg-yellow-100 transition-all space-x-4">
                            <Icon icon="mdi:email" class="text-3xl text-black" />
                            <input
                                v-model="email"
                                type="email"
                                placeholder="你的 Email..."
                                class="flex-1 bg-transparent outline-none font-bold text-black placeholder-gray-500"
                            />
                        </div>
                    </div>

                    <!-- Password 欄位 -->
                    <div class="space-y-2">
                        <label class="text-md font-black uppercase tracking-wide text-gray-600 block">Password</label>
                        <div class="border-2 border-black bg-pink-50 flex items-center px-4 py-3 focus-within:bg-pink-100 transition-all space-x-4">
                            <Icon icon="mdi:lock" class="text-3xl text-black" />
                            <input
                                v-model="password"
                                type="password"
                                placeholder="設定密碼..."
                                class="flex-1 bg-transparent outline-none font-bold text-black placeholder-gray-500"
                            />
                        </div>
                    </div>

                    <!-- 提交按鈕 -->
                    <button
                        type="submit"
                        @click.prevent="signup"
                        class="w-full border-2 border-black bg-orange-400 text-black font-black py-4 px-6 uppercase tracking-wide shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all text-lg cursor-pointer mt-2"
                    >
                        立即註冊
                    </button>
                </form>

                <!-- 分隔線 -->
                <div class="relative">
                    <div class="border-t-2 border-black"></div>
                    <div class="absolute inset-x-0 top-0 flex justify-center">
                        <span class="bg-white px-4 font-black text-gray-600 uppercase text-xs tracking-wide">或</span>
                    </div>
                </div>

                <!-- 已有帳戶 -->
                <div class="border-2 border-black bg-purple-100 p-4 text-center">
                    <p class="font-bold text-md mb-2">已經有帳戶？</p>
                    <button
                        @click.prevent="$emit('login')"
                        class="w-full border-2 border-black bg-purple-400 text-black font-black py-3 px-4 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-md cursor-pointer"
                    >
                        回到登入
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 去除瀏覽器默認的 input 樣式 */
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

input[type=number] {
    -moz-appearance: textfield;
}
</style>