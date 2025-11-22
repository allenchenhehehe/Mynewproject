<script setup>
import { computed } from 'vue'
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
    fridgeItems: Array,
    shoppingList: Array,
})

const emit = defineEmits(['gotorecipedetail'])

// 統計資料
const stats = computed(() => {
    const items = props.fridgeItems || []
    return {
        totalItems: items.length,
        uniqueIngredients: [...new Set(items.map(item => item.ingredient_id))].length,
        shoppingCount: props.shoppingList ? props.shoppingList.reduce((sum, group) => sum + group.items.length, 0) : 0
    }
})

// 即將過期的食材（7天內）
const expiringItems = computed(() => {
    const items = props.fridgeItems || []
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const sevenDaysLater = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000)
    
    return items.filter(item => {
        const expiredDate = new Date(item.expired_date)
        expiredDate.setHours(0, 0, 0, 0)
        return expiredDate <= sevenDaysLater && expiredDate > today
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

// 格式化日期
const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-TW', { month: '2-digit', day: '2-digit' })
}

// 導向食譜頁面
const gotoRecipes = () => {
    // 這裡會通過 App.vue 切換頁面
    // 因為 Home 沒有直接控制頁面的能力，所以需要通過 App.vue
}
</script>

<template>
    <div class="mt-28 max-w-7xl mx-auto px-4 pb-8">
        <!-- 歡迎區塊 - 快速統計 -->
        <div class="bg-white rounded-lg shadow-md p-6 md:p-8 mb-6">
            <h2 class="text-3xl md:text-4xl font-bold mb-6 text-gray-800">歡迎回來！</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <!-- 冰箱食材卡片 -->
                <div class="bg-linear-to-r from-blue-50 to-blue-100 rounded-lg p-6 border-l-4 border-blue-500">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-gray-600 text-sm mb-2">冰箱食材</p>
                            <p class="text-4xl font-bold text-blue-600">{{ stats.uniqueIngredients }}</p>
                            <p class="text-xs text-gray-500 mt-1">種類</p>
                        </div>
                        <div class="text-5xl">🧊</div>
                    </div>
                </div>

                <!-- 購物清單卡片 -->
                <div class="bg-linear-to-r from-green-50 to-green-100 rounded-lg p-6 border-l-4 border-green-500">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-gray-600 text-sm mb-2">待購物品</p>
                            <p class="text-4xl font-bold text-green-600">{{ stats.shoppingCount }}</p>
                            <p class="text-xs text-gray-500 mt-1">項</p>
                        </div>
                        <div class="text-5xl">🛒</div>
                    </div>
                </div>

                <!-- 總食材數卡片 -->
                <div class="bg-linear-to-r from-orange-50 to-orange-100 rounded-lg p-6 border-l-4 border-orange-500">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="text-gray-600 text-sm mb-2">總食材數</p>
                            <p class="text-4xl font-bold text-orange-600">{{ stats.totalItems }}</p>
                            <p class="text-xs text-gray-500 mt-1">項</p>
                        </div>
                        <div class="text-5xl">📦</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 重要提醒區塊 -->
        <div class="bg-white rounded-lg shadow-md p-6 md:p-8 mb-6">
            <h3 class="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2">
                <span class="text-2xl">⚠️</span> 重要提醒
            </h3>

            <!-- 已過期警告 -->
            <div v-if="expiredItems.length > 0" class="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded">
                <p class="text-red-700 font-bold mb-3">❌ 已過期食材（{{ expiredItems.length }}項）</p>
                <div class="flex flex-wrap gap-2">
                    <span 
                        v-for="item in expiredItems" 
                        :key="item.id"
                        class="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm font-semibold"
                    >
                        {{ item.name }} ({{ formatDate(item.expired_date) }})
                    </span>
                </div>
            </div>

            <!-- 即將過期警告 -->
            <div v-if="expiringItems.length > 0" class="p-4 bg-yellow-50 border-l-4 border-yellow-500 rounded">
                <p class="text-yellow-700 font-bold mb-3">⏰ 即將過期（7天內）{{ expiringItems.length }}項</p>
                <div class="space-y-2">
                    <div 
                        v-for="item in expiringItems" 
                        :key="item.id"
                        class="flex justify-between items-center bg-white p-3 rounded border border-yellow-200"
                    >
                        <div>
                            <p class="font-semibold text-gray-800">{{ item.name }}</p>
                            <p class="text-sm text-gray-500">{{ item.quantity }}{{ item.unit }}</p>
                        </div>
                        <div class="text-right">
                            <p class="font-bold text-yellow-600">{{ daysUntilExpiry(item.expired_date) }}天</p>
                            <p class="text-xs text-gray-500">{{ formatDate(item.expired_date) }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 沒有過期提醒 -->
            <div v-if="expiredItems.length === 0 && expiringItems.length === 0" class="p-4 bg-green-50 border-l-4 border-green-500 rounded">
                <p class="text-green-700 font-bold">✅ 太好了！所有食材都還新鮮</p>
            </div>
        </div>

        <!-- 今天想煮甚麼 -->
        <div class="bg-white rounded-lg shadow-md p-6 md:p-8 mb-6">
            <h3 class="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2">
                <span class="text-2xl">👨‍🍳</span> 今天想煮甚麼？
            </h3>
            <div class="bg-blue-50 border-l-4 border-blue-500 rounded p-4">
                <p class="text-gray-700 mb-4">
                    根據你現有的 <span class="font-bold text-blue-600">{{ stats.uniqueIngredients }}</span> 種食材，
                    你可以做出很多美味料理！
                </p>
                <button 
                    class="w-full md:w-auto bg-orange-500 text-white font-bold py-3 px-6 rounded-lg hover:bg-orange-600 transition-colors"
                >
                    🔍 瀏覽所有食譜
                </button>
            </div>
        </div>

        <!-- 快捷操作 -->
        <div class="bg-white rounded-lg shadow-md p-6 md:p-8">
            <h3 class="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2">
                <span class="text-2xl">⚡</span> 快速操作
            </h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <button class="bg-linear-to-r from-blue-500 to-blue-600 text-white font-bold py-4 px-6 rounded-lg hover:shadow-lg transition-all flex items-center justify-center gap-2 text-base md:text-lg">
                    <span class="text-xl">➕</span> 新增食材到冰箱
                </button>
                <button class="bg-linear-to-r from-green-500 to-green-600 text-white font-bold py-4 px-6 rounded-lg hover:shadow-lg transition-all flex items-center justify-center gap-2 text-base md:text-lg">
                    <span class="text-xl">🛍️</span> 檢查購物清單
                </button>
            </div>
        </div>
    </div>
</template>
