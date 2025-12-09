<script setup>
import { ref, defineEmits } from 'vue'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'
import { useAuthStore } from '@/stores/authStore'

const emit = defineEmits(['navigate'])
const isMenuOpen = ref(false)
const authStore = useAuthStore()

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

async function logout() {
    console.log('🚪 開始登出...')
    console.log('登出前 authStatus:', authStore.authStatus)
    
    // 顯示登出提示
    toast.success('已登出！', {
        autoClose: 1500,
    })
    
    // 呼叫 authStore 的 logout (這會清除所有狀態並設定 authStatus = STATUS_LOGIN)
    await authStore.logout()
    
    console.log('登出後 authStatus:', authStore.authStatus)
    console.log('登出完成')
    

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