<script setup>
import { ref } from 'vue'
const emits = defineEmits(['login'])
const email = ref('')
const username = ref('')
const password = ref('')
async function signup() {
    const isSignUpSuccessful = true
    if (isSignUpSuccessful) {
        emits('login')
        console.log("模擬註冊成功。觸發 'login' 事件，導航到登入畫面。")
    }
}
</script>
<template>
    <div class="min-h-screen flex items-center justify-center p-4 bg-cover bg-center" style="background-image: url('/public/chef.webp')">
        <div class="max-w-md w-full p-9 flex flex-col justify-center bg-white/70 backdrop-blur-[20px] rounded-xl shadow-2xl border border-white/40">
            <h2 class="text-black text-3xl font-semibold text-center mb-8">Sign Up</h2>

            <form class="space-y-8">
                <div class="flex items-center space-x-5">
                    <Icon icon="mdi:account-plus" class="text-3xl text-black" />
                    <input
                        type="text"
                        placeholder="Username"
                        class="w-full px-4 py-3 rounded-lg border border-white/50 text-black placeholder-black transition focus:border-black focus:ring-2 focus:ring-black focus:ring-opacity-50 focus:outline-none bg-black/30"
                    />
                </div>
                <div class="flex items-center space-x-5">
                    <Icon icon="mdi:email" class="text-3xl text-black" />
                    <input
                        type="text"
                        placeholder="Email"
                        class="w-full px-4 py-3 rounded-lg border border-white/50 text-black placeholder-black transition focus:border-black focus:ring-2 focus:ring-black focus:ring-opacity-50 focus:outline-none bg-black/30"
                    />
                </div>

                <div class="flex items-center space-x-5">
                    <Icon icon="mdi:lock" class="text-3xl text-black" />
                    <input
                        type="password"
                        placeholder="Password"
                        class="space-y-1 w-full px-4 py-3 rounded-lg border border-white/50 text-black placeholder-black transition focus:border-black focus:ring-2 focus:ring-black focus:ring-opacity-50 focus:outline-none bg-black/30"
                    />
                </div>
                <div>
                    <button
                        type="submit"
                        @click.prevent="signup"
                        class="w-full py-3 bg-black rounded-lg text-white font-semibold transition cursor-pointer"
                    >
                        Submit
                    </button>
                </div>
                <div class="mt-6 text-black text-center">
                    <span class="mr-5 text-sm">Already have acount ?</span>
                    <button class="px-3 py-0.5 rounded-full transition inline-flex items-center text-sm hover:bg-white">
                        <a href="#" @click.prevent="$emit('login')" class="font-bold text-black text-xs">Login</a>
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
