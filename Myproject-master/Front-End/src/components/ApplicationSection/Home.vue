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
    <div class="pt-24 max-w-6xl mx-auto px-6 pb-16 font-sans text-black">
        
        <div class="flex flex-col md:flex-row justify-between items-start md:items-end mb-10 gap-4">
            <div>
                <div class="bg-black text-[#fefae0] px-3 py-1 inline-block font-bold text-sm mb-2 rotate-2">TODAY: {{ todayDateString }}</div>
                <h1 class="text-5xl md:text-6xl font-black tracking-tighter uppercase transform -skew-x-6">
                    STOCK & <span class="text-orange-500">STOVE</span>
                </h1>
            </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
            <div @click="navigateTo('My Fridge')" 
                 class="bg-white border-2 border-black p-6 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] hover:translate-x-1 hover:translate-y-1 hover:shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] transition-all cursor-pointer">
                <h3 class="font-black text-xl mb-2 bg-blue-200 inline-block px-2">FRIDGE</h3>
                <div class="flex justify-between items-end">
                    <span class="text-5xl font-black">{{ stats.uniqueIngredients }}</span>
                    <span class="font-bold text-sm">TYPES</span>
                </div>
            </div>
            
            <div @click="navigateTo('Shopping List')"
                 class="bg-white border-2 border-black p-6 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] hover:translate-x-1 hover:translate-y-1 hover:shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] transition-all cursor-pointer">
                <h3 class="font-black text-xl mb-2 bg-green-200 inline-block px-2">SHOPPING</h3>
                <div class="flex justify-between items-end">
                    <span class="text-5xl font-black">{{ stats.shoppingCount }}</span>
                    <span class="font-bold text-sm">ITEMS</span>
                </div>
            </div>

            <div class="bg-orange-400 border-2 border-black p-6 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
                <h3 class="font-black text-xl mb-2 bg-white inline-block px-2">TOTAL</h3>
                <div class="flex justify-between items-end">
                    <span class="text-5xl font-black text-white">{{ stats.totalItems }}</span>
                    <span class="font-bold text-sm">IN STOCK</span>
                </div>
            </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div class="bg-white border-2 border-black p-8 shadow-[8px_8px_0px_0px_rgba(0,0,0,1)]">
                <div class="flex justify-between items-center mb-6 border-b-2 border-black pb-4">
                    <h2 class="text-3xl font-black uppercase">Alerts</h2>
                    <div v-if="expiredItems.length > 0" class="bg-red-500 text-white font-bold px-3 py-1 animate-pulse">
                        ! EXPIRED !
                    </div>
                </div>

                <div v-if="expiredItems.length > 0" class="mb-6 space-y-2">
                     <div v-for="item in expiredItems" :key="item.id" class="bg-red-100 border-2 border-red-500 p-2 flex justify-between font-bold">
                        <span>{{ item.name }}</span>
                        <span>{{ formatDate(item.expired_date) }}</span>
                     </div>
                </div>

                <div v-if="expiringItems.length > 0" class="space-y-3">
                    <p class="font-bold text-sm bg-yellow-200 inline-block px-2 border border-black">EXPIRING SOON</p>
                    <div v-for="item in expiringItems" :key="item.id" class="flex justify-between items-center border-b-2 border-dashed border-gray-300 pb-2">
                        <span class="font-bold text-lg">{{ item.name }}</span>
                        <div class="text-right">
                            <span class="block font-black text-orange-500">{{ daysUntilExpiry(item.expired_date) }} DAYS LEFT</span>
                        </div>
                    </div>
                </div>

                <div v-if="expiredItems.length === 0 && expiringItems.length === 0" class="text-center py-10">
                    <p class="text-2xl font-black text-gray-300">ALL GOOD</p>
                </div>
            </div>

            <div class="flex flex-col gap-6">
                <button @click="navigateTo('Recipes')" 
                        class="group relative bg-[#9b87f5] h-full min-h-[200px] border-2 border-black shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] hover:shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-1 hover:translate-y-1 transition-all overflow-hidden">
                    <div class="absolute inset-0 flex items-center justify-center opacity-10 group-hover:scale-150 transition-transform duration-700">
                        <span class="text-9xl font-black">?</span>
                    </div>
                    <div class="relative z-10 p-8 flex flex-col items-start h-full justify-between">
                        <h2 class="text-4xl font-black text-white text-stroke-black leading-none">
                            WHAT'S FOR<br>DINNER?
                        </h2>
                        <div class="bg-white border-2 border-black px-4 py-2 font-bold hover:bg-black hover:text-white transition-colors">
                            COOK NOW ->
                        </div>
                    </div>
                </button>
                
                <div class="grid grid-cols-2 gap-4">
                     <button @click="navigateTo('My Fridge')" 
                             class="bg-[#a7f3d0] border-2 border-black p-4 font-bold shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all">
                        ADD ITEM +
                     </button>
                     <button @click="navigateTo('Shopping List')"
                             class="bg-[#fecaca] border-2 border-black p-4 font-bold shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_rgba(0,0,0,1)] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all">
                        CHECK LIST
                     </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 增加文字描邊效果 */
.text-stroke-black {
  -webkit-text-stroke: 1.5px black;
  text-shadow: 2px 2px 0px black;
}
</style>