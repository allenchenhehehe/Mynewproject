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
        <div class="max-w-md w-full p-12 flex flex-col justify-center bg-white/70 backdrop-blur-[20px] rounded-xl shadow-2xl border border-white/40">
            <h2 class="text-black text-3xl font-semibold text-center mb-8">Login</h2>

            <form class="space-y-8">
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

                <div class="mt-6 text-black flex justify-between pt-1">
                    <div class="flex items-center text-sm">
                        <input id="remember-me" name="remember-me" type="checkbox" class="h-4 w-4 border-gray-300 rounded" />
                        <label for="remember-me" class="ml-2 block text-gray-900"> Remember Me </label>
                    </div>
                    <button class="px-3 py-0.5 rounded-full transition inline-flex items-center text-sm hover:bg-white">
                        <a href="#" @click.prevent="$emit('forgetpassword')" class="font-bold text-black text-xs">Forget your password ?</a>
                    </button>
                </div>
                <div>
                    <button
                        type="submit"
                        @click.prevent="login"
                        class="w-full py-3 bg-black rounded-lg text-white font-semibold transition cursor-pointer"
                    >
                        Login
                    </button>
                </div>
            </form>

            <div class="mt-6 text-black text-center">
                <span class="mr-5 text-sm">Don't have an account ?</span>
                <button class="px-3 py-0.5 rounded-full transition inline-flex items-center text-sm hover:bg-white">
                    <a href="#" @click.prevent="$emit('signup')" class="font-bold text-black text-xs">Sign up</a>
                </button>
            </div>
        </div>
    </div>
</template>
