<script setup>
import { ref, computed } from 'vue'

const expandedCategories = ref({
    expiring: true,
    vegetables: false,
    proteins: false,
    seasonings: false,
    oils: false,
})

// 模擬食材數據
const ingredients = ref({
    expiring: [
        { id: 1, name: '番茄', quantity: 5, unit: '個', purchaseDate: '2025-11-03', expiryDate: '2025-11-12', daysLeft: 2, isExpired: true },
        { id: 2, name: '牛奶', quantity: 1, unit: '瓶（1L）', purchaseDate: '2025-11-05', expiryDate: '2025-11-16', daysLeft: 6, isWarning: true },
    ],
    vegetables: [
        { id: 3, name: '洋蔥', quantity: 3, unit: '個', purchaseDate: '2025-11-08', expiryDate: '2025-12-15' },
        { id: 4, name: '大蒜', quantity: 2, unit: '頭', purchaseDate: '2025-11-07', expiryDate: '2025-12-30' },
        { id: 5, name: '青椒', quantity: 2, unit: '個', purchaseDate: '2025-11-06', expiryDate: '2025-11-20' },
        { id: 6, name: '菠菜', quantity: 200, unit: 'g', purchaseDate: '2025-11-04', expiryDate: '2025-11-18' },
    ],
    proteins: [
        { id: 7, name: '雞蛋', quantity: 10, unit: '個', purchaseDate: '2025-11-01', expiryDate: '2025-12-01' },
        { id: 8, name: '豆腐', quantity: 1, unit: '盒', purchaseDate: '2025-11-09', expiryDate: '2025-11-15' },
        { id: 9, name: '起司', quantity: 200, unit: 'g', purchaseDate: '2025-10-20', expiryDate: '2025-12-20' },
    ],
    seasonings: [
        { id: 10, name: '鹽', quantity: 500, unit: 'g', purchaseDate: '2025-07-10', expiryDate: '2027-07-10' },
        { id: 11, name: '黑胡椒', quantity: 100, unit: 'g', purchaseDate: '2025-08-20', expiryDate: '2026-08-20' },
        { id: 12, name: '洋蔥粉', quantity: 50, unit: 'g', purchaseDate: '2025-09-20', expiryDate: '2026-09-20' },
        { id: 13, name: '番茄醬', quantity: 300, unit: 'g', purchaseDate: '2025-10-15', expiryDate: '2025-12-15' },
        { id: 14, name: '生抽', quantity: 500, unit: 'ml', purchaseDate: '2025-08-05', expiryDate: '2026-08-05' },
    ],
    oils: [
        { id: 15, name: '橄欖油', quantity: 500, unit: 'ml', purchaseDate: '2025-09-15', expiryDate: '2026-09-15' },
        { id: 16, name: '芝麻油', quantity: 250, unit: 'ml', purchaseDate: '2025-09-10', expiryDate: '2026-09-10' },
    ],
})

const categories = [
    { key: 'expiring', name: '即將過期', icon: '⚠️', color: 'from-red-50 to-red-100' },
    { key: 'vegetables', name: '蔬菜', icon: '🥬', color: 'from-green-50 to-green-100' },
    { key: 'proteins', name: '蛋白質', icon: '🍗', color: 'from-orange-50 to-orange-100' },
    { key: 'seasonings', name: '調味料', icon: '🧂', color: 'from-yellow-50 to-yellow-100' },
    { key: 'oils', name: '油類', icon: '🫒', color: 'from-amber-50 to-amber-100' },
]

const toggleCategory = (key) => {
    expandedCategories.value[key] = !expandedCategories.value[key]
}

const toggleAllCategories = () => {
    const allCollapsed = Object.values(expandedCategories.value).every((v) => !v)
    Object.keys(expandedCategories.value).forEach((key) => {
        expandedCategories.value[key] = allCollapsed
    })
}

const deleteIngredient = (categoryKey, id) => {
    ingredients.value[categoryKey] = ingredients.value[categoryKey].filter((item) => item.id !== id)
}

const expiringCount = computed(() => ingredients.value.expiring.length)
const totalCount = computed(() => {
    return Object.values(ingredients.value).reduce((sum, arr) => sum + arr.length, 0)
})
</script>

<template>
    <div class="mt-28 max-w-4xl mx-auto px-4">
        <!-- 頁面頭部 -->
        <div class="flex justify-between items-center mb-8">
            <h1 class="text-4xl font-bold text-gray-800">我的冰箱</h1>
            <button class="bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-6 rounded-full transition-colors duration-200">
                + 新增食材
            </button>
        </div>

        <!-- 警告摘要 -->
        <div v-if="expiringCount > 0" class="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded-r-lg flex justify-between items-center">
            <div class="text-red-700 font-bold">注意：你有 {{ expiringCount }} 個食材即將在 7 天內過期</div>
            <a href="#" class="text-orange-600 font-semibold">查看詳情</a>
        </div>

        <!-- 快速操作按鈕(看不太懂) -->
        <div class="mb-6">
            <button
                @click="toggleAllCategories"
                class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-2 px-4 rounded-lg transition-colors duration-200"
            >
                全部摺疊 / 展開
            </button>
        </div>

        <!-- 分類列表 -->
        <div class="space-y-6">
            <div v-for="category in categories" :key="category.key" class="bg-white rounded-lg shadow-md overflow-hidden">
                <!-- 分類頭部 -->
                <div
                    @click="toggleCategory(category.key)"
                    class="cursor-pointer p-4 flex justify-between items-center hover:bg-gray-50 transition-colors duration-200"
                    :class="expandedCategories[category.key] ? 'bg-linear-to-r ' + category.color + ' border-l-4 border-orange-500' : 'bg-white'"
                >
                    <div class="flex items-center gap-4 flex-1">
                        <span class="text-2xl">{{ category.icon }}</span>
                        <span class="font-bold text-gray-800">{{ category.name }}</span>
                        <span class="bg-orange-500 text-white px-3 py-1 rounded-full text-sm font-bold">
                            {{ ingredients[category.key].length }}
                        </span>
                    </div>
                    <span class="text-lg transition-transform duration-300" :class="expandedCategories[category.key] ? 'rotate-0' : '-rotate-90'">
                        ▼
                    </span>
                </div>

                <!-- 分類內容 -->
                <div v-if="expandedCategories[category.key]" class="border-t border-gray-200 p-4 space-y-3 animate-in fade-in duration-300">
                    <div v-if="ingredients[category.key].length === 0" class="text-center text-gray-400 py-6">此分類暫無食材</div>

                    <div
                        v-for="ingredient in ingredients[category.key]"
                        :key="ingredient.id"
                        class="p-3 rounded-lg border-l-4 transition-all duration-200 flex justify-between items-center hover:shadow-md"
                        :class="
                            ingredient.isExpired
                                ? 'bg-red-50 border-red-500'
                                : ingredient.isWarning
                                  ? 'bg-yellow-50 border-orange-500'
                                  : 'bg-gray-50 border-orange-500'
                        "
                    >
                        <div class="flex-1">
                            <div class="font-bold text-gray-800 mb-1">
                                {{ ingredient.name }}
                                <span
                                    v-if="ingredient.isExpired || ingredient.isWarning"
                                    class="ml-2 text-xs bg-red-500 text-white px-2 py-1 rounded-full font-bold"
                                >
                                    {{ ingredient.isExpired ? '即將過期' : '警告' }}
                                </span>
                            </div>
                            <div class="text-sm text-gray-600 flex gap-4">
                                <span>📦 {{ ingredient.quantity }} {{ ingredient.unit }}</span>
                                <span>📅 購買：{{ ingredient.purchaseDate }}</span>
                                <span v-if="ingredient.daysLeft" class="text-orange-600 font-semibold">⏰ 剩餘 {{ ingredient.daysLeft }} 天</span>
                                <span v-else>過期：{{ ingredient.expiryDate }}</span>
                            </div>
                        </div>
                        <div class="flex gap-2 ml-4">
                            <button
                                class="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded transition-colors duration-200 text-sm"
                            >
                                編輯
                            </button>
                            <button
                                @click="deleteIngredient(category.key, ingredient.id)"
                                class="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded transition-colors duration-200 text-sm"
                            >
                                刪除
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
