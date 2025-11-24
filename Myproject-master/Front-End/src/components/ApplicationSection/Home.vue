<script setup>
import { computed } from 'vue'
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
    fridgeItems: Array,
    shoppingList: Array,
})

// 定義事件，讓 Home 可以通知 App.vue 切換頁面
const emit = defineEmits(['change-page'])

// 導航函數
const navigateTo = (pageName) => {
    emit('change-page', pageName)
}

// --- 統計邏輯 ---

// 統計資料
const stats = computed(() => {
    const items = props.fridgeItems || []
    return {
        totalItems: items.length,
        // 計算不重複的 ingredient_id
        uniqueIngredients: [...new Set(items.map(item => item.ingredient_id))].length,
        // 計算購物清單內的總品項數 (因為 shoppingList 是分組的)
        shoppingCount: props.shoppingList ? props.shoppingList.reduce((sum, group) => sum + group.items.length, 0) : 0
    }
})

// 即將過期的食材（7天內，且未過期）
const expiringItems = computed(() => {
    const items = props.fridgeItems || []
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const sevenDaysLater = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000)
    
    return items.filter(item => {
        const expiredDate = new Date(item.expired_date)
        expiredDate.setHours(0, 0, 0, 0)
        return expiredDate <= sevenDaysLater && expiredDate >= today
    }).sort((a, b) => new Date(a.expired_date) - new Date(b.expired_date))
})

// 已過期的食材
const expiredItems = computed(() => {
    const items = props.fridgeItems || []
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    
    return items.filter(item => {
        const expiredDate = new Date(item.expired_date)
        expiredDate.setHours(0, 0, 0, 0)
        return expiredDate < today
    })
})

// 計算距離過期的天數
const daysUntilExpiry = (expiredDate) => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const expired = new Date(expiredDate)
    expired.setHours(0, 0, 0, 0)
    const diffTime = expired - today
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
}

// 格式化日期 (MM/DD)
const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-TW', { month: '2-digit', day: '2-digit' })
}

// 獲取今天的日期字串
const todayDateString = computed(() => {
    const date = new Date();
    return date.toLocaleDateString('zh-TW', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' });
})
</script>

<template>
    <div class="pt-24 max-w-6xl mx-auto px-6 pb-12 text-slate-800">
        
        <div class="mb-10">
            <p class="text-slate-500 font-medium tracking-wide mb-1 opacity-80">{{ todayDateString }}</p>
            <h2 class="text-4xl font-extrabold tracking-tight text-slate-900">
                早安，<span class="text-orange-600">大廚</span>
            </h2>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
            
            <div class="lg:col-span-8 flex flex-col gap-6">
                
                <div class="bg-white rounded-3xl p-1 shadow-sm border border-orange-100/50 grid grid-cols-3 divide-x divide-slate-100 gap-2">
                    <div @click="navigateTo('My Fridge')" class="bg-linear-to-r from-blue-50 to-blue-100 rounded-lg p-6 border-l-4 border-blue-500 cursor-pointer">
                        <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">冰箱食材</p>
                        <p class="text-4xl font-black text-slate-800 group-hover:text-orange-600 transition-colors">
                            {{ stats.uniqueIngredients }}<span class="text-lg font-medium text-slate-400 ml-1">種</span>
                        </p>
                    </div>
                    <div @click="navigateTo('My Fridge')" class="bg-linear-to-r from-green-50 to-green-100 rounded-lg p-6 border-l-4 border-green-500 cursor-pointer">
                        <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">總庫存</p>
                        <p class="text-4xl font-black text-slate-800 group-hover:text-orange-600 transition-colors">
                            {{ stats.totalItems }}<span class="text-lg font-medium text-slate-400 ml-1">項</span>
                        </p>
                    </div>
                    <div @click="navigateTo('Shopping List')" class="bg-linear-to-r from-orange-50 to-orange-100 rounded-lg p-6 border-l-4 border-orange-500 cursor-pointer">
                        <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">待購清單</p>
                        <p class="text-4xl font-black text-slate-800 group-hover:text-green-600 transition-colors">
                            {{ stats.shoppingCount }}<span class="text-lg font-medium text-slate-400 ml-1">項</span>
                        </p>
                    </div>
                </div>

                <div class="bg-white rounded-3xl shadow-sm border border-orange-100/50 overflow-hidden flex flex-col h-full min-h-[300px]">
                    <div class="p-6 border-b border-slate-50 flex justify-between items-end">
                        <div>
                            <h3 class="text-xl font-bold text-slate-800">庫存動態</h3>
                            <p class="text-slate-500 text-sm mt-1">
                                {{ expiredItems.length > 0 ? '請盡快處理過期食材' : '食材狀況保持良好' }}
                            </p>
                        </div>
                        <div v-if="expiredItems.length === 0 && expiringItems.length === 0" class="px-3 py-1 bg-green-100 text-green-700 text-xs font-bold rounded-full">
                            全數新鮮
                        </div>
                        <div v-else class="px-3 py-1 bg-orange-100 text-orange-700 text-xs font-bold rounded-full">
                            需要注意
                        </div>
                    </div>

                    <div class="p-6 grow flex flex-col justify-center">
                        <div v-if="expiredItems.length > 0" class="mb-6">
                            <p class="text-xs font-bold text-red-500 uppercase tracking-wider mb-3 flex items-center gap-2">
                                <span class="w-2 h-2 rounded-full bg-red-500 animate-pulse"></span>
                                已過期
                            </p>
                            <div class="flex flex-wrap gap-3">
                                <div v-for="item in expiredItems" :key="item.id" 
                                     class="bg-red-50 border border-red-100 text-red-900 px-4 py-3 rounded-xl flex flex-col min-w-[140px]">
                                    <span class="font-bold text-lg leading-tight">{{ item.name }}</span>
                                    <span class="text-xs text-red-400 mt-1">過期 {{ Math.abs(daysUntilExpiry(item.expired_date)) }} 天</span>
                                </div>
                            </div>
                        </div>

                        <div v-if="expiringItems.length > 0">
                            <p class="text-xs font-bold text-orange-500 uppercase tracking-wider mb-3">7天內建議食用</p>
                            <div class="space-y-3">
                                <div v-for="item in expiringItems" :key="item.id" 
                                     class="flex items-center justify-between p-4 rounded-2xl bg-slate-50 border border-slate-100 hover:border-orange-200 transition-colors group">
                                    <div class="flex flex-col">
                                        <span class="font-bold text-slate-700 text-lg">{{ item.name }}</span>
                                        <span class="text-xs text-slate-400">{{ item.quantity }}{{ item.unit }} · {{ formatDate(item.expired_date) }} 到期</span>
                                    </div>
                                    <div class="text-right">
                                        <span class="block text-2xl font-bold text-orange-500 group-hover:scale-110 transition-transform">
                                            {{ daysUntilExpiry(item.expired_date) }}
                                            <span class="text-xs font-normal text-slate-400 align-middle">天</span>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div v-if="expiredItems.length === 0 && expiringItems.length === 0" class="flex flex-col items-center justify-center py-8 text-center opacity-60">
                            <span class="text-6xl mb-2">🥗</span>
                            <h4 class="text-lg font-bold text-slate-700">冰箱很健康</h4>
                            <p class="text-slate-500 text-sm">沒有即將過期的食材，盡情享受料理吧！</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="lg:col-span-4 flex flex-col gap-6">
                
                <div class="bg-slate-700 text-white rounded-3xl p-8 shadow-xl shadow-orange-900/10 relative overflow-hidden flex flex-col justify-between min-h-80">
                    <div class="absolute -top-10 -right-10 w-48 h-48 bg-orange-500 rounded-full blur-3xl opacity-20"></div>
                    <div class="absolute bottom-0 left-0 w-32 h-32 bg-blue-500 rounded-full blur-3xl opacity-10"></div>
                    
                    <div class="relative z-10">
                        <h3 class="text-2xl font-bold mb-3">靈感枯竭嗎？</h3>
                        <p class="text-slate-400 text-sm leading-relaxed mb-6">
                            我們可以用這 <span class="text-orange-400 font-bold text-lg">{{ stats.uniqueIngredients }}</span> 種食材幫你變出美味晚餐，不再煩惱今晚吃什麼。
                        </p>
                    </div>

                    <button @click="navigateTo('Recipes')" 
                            class="relative z-10 w-full bg-orange-600 hover:bg-orange-500 text-white font-bold py-4 px-6 rounded-xl transition-all shadow-lg active:scale-95 flex items-center justify-center gap-2">
                        開始探索食譜 
                    </button>
                </div>

                <div class="bg-white rounded-3xl shadow-sm border border-orange-100/50 p-6">
                    <h3 class="text-lg font-bold text-slate-800 mb-4">捷徑</h3>
                    <div class="space-y-3">
                        <button @click="navigateTo('My Fridge')" 
                                class="w-full group flex items-center justify-between p-4 rounded-xl border border-slate-200 hover:border-blue-300 hover:bg-blue-50 transition-all bg-slate-50">
                            <span class="font-semibold text-slate-600 group-hover:text-blue-700">管理冰箱</span>
                            <span class="text-xl leading-none text-slate-300 group-hover:text-blue-500">+</span>
                        </button>
                        
                        <button @click="navigateTo('Shopping List')"
                                class="w-full group flex items-center justify-between p-4 rounded-xl border border-slate-200 hover:border-green-300 hover:bg-green-50 transition-all bg-slate-50">
                            <span class="font-semibold text-slate-600 group-hover:text-green-700">購物清單</span>
                            <span class="text-xl leading-none text-slate-300 group-hover:text-green-500">→</span>
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>