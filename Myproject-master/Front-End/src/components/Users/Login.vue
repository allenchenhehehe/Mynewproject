<script setup>
import { ref } from 'vue'

const email = ref('')
const password = ref('')
const emits = defineEmits(['navbar', 'signup', 'forgetpassword'])

async function login() {
    const isLoginSuccessful = true
    if (isLoginSuccessful) {
        emits('navbar')
    }
}
</script>

<template>
    <div class="min-h-screen flex items-center justify-center p-4 bg-cover bg-center" style="background-image: url('/public/chef.webp')">
        <div class="max-w-lg w-full" >
            <!-- 主容器 -->
            <div class="border-4 border-black bg-white shadow-[8px_8px_0px_0px_black] p-12 space-y-8">
                <!-- 標題 -->
                <div class="text-center mb-4">
                    <h1 class="text-5xl font-black uppercase tracking-tighter mb-2">LOGIN</h1>
                    <p class="text-md text-gray-600 font-bold uppercase tracking-wide">歡迎回到 Stock & Stove</p>
                </div>

                <!-- 表單 -->
                <form class="space-y-6">
                    <!-- Email 欄位 -->
                    <div class="space-y-2">
                        <label class="text-sm font-black uppercase tracking-wide text-gray-600 block">Email</label>
                        <div class="border-2 border-black bg-yellow-50 flex items-center px-4 py-3 focus-within:bg-yellow-100 transition-all">
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
                        <label class="text-sm font-black uppercase tracking-wide text-gray-600 block">Password</label>
                        <div class="border-2 border-black bg-pink-50 flex items-center px-4 py-3 focus-within:bg-pink-100 transition-all">
                            <input
                                v-model="password"
                                type="password"
                                placeholder="你的密碼..."
                                class="flex-1 bg-transparent outline-none font-bold text-black placeholder-gray-500"
                            />
                        </div>
                    </div>

                    <!-- 記住我 + 忘記密碼 -->
                    <div class="flex items-center justify-between py-2">
                        <label class="flex items-center gap-2 cursor-pointer font-bold text-md">
                            <input type="checkbox" class="w-5 h-5 border-2 border-black accent-black cursor-pointer" />
                            <span>記住我</span>
                        </label>
                        <button 
                            type="button"
                            @click.prevent="$emit('forgetpassword')"
                            class="text-md font-black uppercase tracking-wide text-black cursor-pointer"
                        >
                            忘記密碼？
                        </button>
                    </div>

                    <!-- 登入按鈕 -->
                    <button
                        type="submit"
                        @click.prevent="login"
                        class="w-full border-2 border-black bg-black text-white font-black py-4 px-6 uppercase tracking-wide shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all text-lg cursor-pointer mt-2"
                    >
                        登 入
                    </button>
                </form>

                <!-- 分隔線 -->
                <div class="relative">
                    <div class="border-t-2 border-black"></div>
                    <div class="absolute inset-x-0 top-0 flex justify-center">
                        <span class="bg-white px-4 font-black text-gray-600 uppercase text-xs tracking-wide">或</span>
                    </div>
                </div>

                <!-- 沒有帳戶 -->
                <div class="border-2 border-black bg-blue-100 p-4 text-center">
                    <p class="font-bold text-md mb-2">還沒有帳戶？</p>
                    <button
                        @click.prevent="$emit('signup')"
                        class="w-full border-2 border-black bg-blue-400 text-black font-black py-3 px-4 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-md cursor-pointer"
                    >
                        立即註冊
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