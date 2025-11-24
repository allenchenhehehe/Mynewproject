<script setup>
import { ref, defineEmits } from 'vue'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

const emit = defineEmits(['navigate'])
const isMenuOpen = ref(false)

const menuItems = [
    { label: '收藏食譜', action: 'favorites' },
    { label: '我的評論', action: 'comments' },
    { label: '登出', action: 'logout' }
]

function handleMenuClick(action) {
    switch(action) {
        case 'favorites':
            console.log('導航到收藏食譜')
            emit('navigate', 'Favorites')
            break
        case 'comments':
            console.log('導航到我的評論')
            emit('navigate', 'MyComments')
            break
        case 'logout':
            logout()
            break
    }
    
    isMenuOpen.value = false
}

function logout() {
    // 清除 localStorage 的認證信息
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('userId')
    
    // 顯示登出成功提示
    toast.success('已登出！', {
        autoClose: 1500,
    })
    
    // 導航回首頁
    setTimeout(() => {
        emit('navigate', 'Home')
    }, 500)
}
</script>

<template>
    <div class="relative">
        <!-- 帳戶按鈕 -->
        <button
            @click="isMenuOpen = !isMenuOpen"
            class="border-2 border-black bg-gray-300 text-black rounded-full w-12 h-12 flex items-center justify-center font-black text-xl hover:bg-gray-400 hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all"
        >
            U
        </button>

        <!-- 下拉菜單 -->
        <div
            v-show="isMenuOpen"
            class="absolute right-0 mt-2 border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] z-50 min-w-40 overflow-hidden"
        >
            <div class="py-2">
                <button
                    v-for="item in menuItems"
                    :key="item.action"
                    @click="handleMenuClick(item.action)"
                    :class="item.action === 'logout' 
                        ? 'bg-red-100 text-red-700 hover:bg-red-200 border-t-2 border-black'
                        : 'text-black hover:bg-yellow-100'"
                    class="w-full px-4 py-3 text-left font-bold uppercase tracking-wide transition-all text-sm"
                >
                    {{ item.label }}
                </button>
            </div>
        </div>

        <!-- 背景遮罩 -->
        <div
            v-show="isMenuOpen"
            class="fixed inset-0 z-40"
            @click="isMenuOpen = false"
        />
    </div>
</template>