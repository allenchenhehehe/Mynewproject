<script setup>
import { ref } from 'vue'
const emits = defineEmits(['login'])
const email = ref('')
const message = ref('')
async function resetPassword() {
    if (email.value) {
        message.value = '密碼重設連結已發送到您的電子郵件。'
        console.log(`模擬重設密碼：發送連結到 ${email.value}`)
        setTimeout(() => emits('login'), 2000)
    } else {
        message.value = '請輸入有效的電子郵件地址。'
    }
}
</script>
<template>
    <div class="min-h-screen flex items-center justify-center p-4 bg-cover bg-center" style="background-image: url('/public/chef.webp')">
        <div class="max-w-md w-full p-12 flex flex-col justify-center bg-white/70 backdrop-blur-[20px] rounded-xl shadow-2xl border border-white/40">
            <h2 class="text-black text-3xl font-semibold text-center mb-8">Find your password</h2>

            <form @submit.prevent="resetPassword" class="space-y-8">
                <div
                    v-if="message"
                    :class="{ 'text-black': message.includes('成功'), 'text-red-600': message.includes('請輸入') }"
                    class="text-m font-medium text-center text-black"
                >
                    {{ message }}
                </div>
                <div class="flex items-center space-x-5">
                    <Icon icon="mdi:email" class="text-3xl text-black" />
                    <input
                        type="text"
                        placeholder="Please enter your Email"
                        v-model="email"
                        class="w-full px-4 py-3 rounded-lg border border-white/50 text-black placeholder-black transition focus:border-black focus:ring-2 focus:ring-black focus:ring-opacity-50 focus:outline-none bg-black/30"
                    />
                </div>
                <div>
                    <button type="submit" class="w-full py-3 bg-black rounded-lg text-white font-semibold transition cursor-pointer">
                        Send reset link
                    </button>
                </div>
            </form>

            <div class="mt-6 text-black text-center">
                <button class="px-3 py-0.5 rounded-full transition inline-flex items-center text-sm hover:bg-white">
                    <a href="#" @click.prevent="$emit('login')" class="font-bold text-black text-xs">Back to Login</a>
                </button>
            </div>
        </div>
    </div>
</template>
