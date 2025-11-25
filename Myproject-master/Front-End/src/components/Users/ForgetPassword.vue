<script setup>
import { ref } from 'vue'

const emits = defineEmits(['login'])
const email = ref('')
const message = ref('')

async function resetPassword() {
    if (!email.value || !email.value.includes('@')) {
        message.value = '請輸入有效的電子郵件地址。'
        setTimeout(() => {
            message.value = ''
        }, 2000)
        return
    }
    
    message.value = '密碼重設連結已發送到您的電子郵件。'
    console.log(`模擬重設密碼：發送連結到 ${email.value}`)
    setTimeout(() => emits('login'), 1500)
}
</script>

<template>
    <div class="min-h-screen flex items-center justify-center p-4 bg-cover bg-center" style="background-image: url('/chef.webp')">
        <div class="max-w-lg w-full">
            <!-- 主容器 -->
            <div class="border-4 border-black bg-white shadow-[8px_8px_0px_0px_black] p-12 space-y-8">
                <!-- 標題 -->
                <div class="text-center mb-8">
                    <h1 class="text-5xl font-black uppercase tracking-tighter mb-2">忘記密碼</h1>
                    <p class="text-sm text-gray-600 font-bold uppercase tracking-wide">別擔心，我們幫你找回來</p>
                </div>

                <!-- 提示訊息 -->
                <div
                    v-if="message"
                    :class="message.includes('已發送') 
                        ? 'bg-green-100 border-green-400 text-green-700' 
                        : 'bg-red-100 border-red-400 text-red-700'"
                    class="border-2 p-4 font-bold text-center uppercase text-sm"
                >
                    {{ message }}
                </div>

                <!-- 表單 -->
                <form @submit.prevent="resetPassword" class="space-y-6">
                    <!-- Email 欄位 -->
                    <div class="space-y-2">
                        <label class="text-xs font-black uppercase tracking-wide text-gray-600 block">請輸入你的 Email</label>
                        <div class="border-2 border-black bg-blue-50 flex items-center px-4 py-3 focus-within:bg-blue-100 transition-all space-x-4">
                            <Icon icon="mdi:email" class="text-3xl text-black" />
                            <input
                                v-model="email"
                                type="text"
                                placeholder="你的電子郵件..."
                                class="flex-1 bg-transparent outline-none font-bold text-black placeholder-gray-500"
                                @invalid.prevent
                            />
                        </div>
                    </div>

                    <!-- 提交按鈕 -->
                    <button
                        type="submit"
                        class="w-full border-2 border-black bg-yellow-400 text-black font-black py-4 px-6 uppercase tracking-wide shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all text-lg cursor-pointer"
                    >
                        發送重設連結
                    </button>
                </form>

                <!-- 分隔線 -->
                <div class="relative">
                    <div class="border-t-2 border-black"></div>
                    <div class="absolute inset-x-0 top-0 flex justify-center">
                        <span class="bg-white px-4 font-black text-gray-600 uppercase text-sm tracking-wide">或</span>
                    </div>
                </div>

                <!-- 返回登入 -->
                <button
                    @click.prevent="$emit('login')"
                    class="w-full border-2 border-black bg-gray-300 text-black font-black py-3 px-4 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-md cursor-pointer"
                >
                    回到登入
                </button>
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