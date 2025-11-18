<script setup>
import { ref } from 'vue'

const mockRecipes = [
    {
        id: '1',
        title: '番茄炒蛋',
        description: '簡單快手菜，營養豐富',
        difficulty: 3,
        cooking_time: 15,
        steps: '1. 打蛋 2. 炒蛋 3. 加番茄',
        creator_id: 'system',
        is_public: true,
        ingredients: [
            { ingredient_id: 1, ingredient_name: '番茄', quantity: 2, unit: '個' },
            { ingredient_id: 3, ingredient_name: '雞蛋', quantity: 3, unit: '個' },
        ],
    },
    {
        id: '2',
        title: '炒洋蔥雞蛋',
        description: '經典家常菜，做法簡單',
        difficulty: 2,
        cooking_time: 20,
        steps: '1. 切洋蔥 2. 炒洋蔥 3. 打蛋炒',
        creator_id: 'system',
        is_public: true,
        ingredients: [
            { ingredient_id: 2, ingredient_name: '洋蔥', quantity: 1, unit: '個' },
            { ingredient_id: 3, ingredient_name: '雞蛋', quantity: 2, unit: '個' },
        ],
    },
    {
        id: '3',
        title: '清炒蔬菜',
        description: '健康清爽的蔬菜料理',
        difficulty: 2,
        cooking_time: 15,
        steps: '1. 準備蔬菜 2. 熱鍋下油 3. 炒蔬菜',
        creator_id: 'system',
        is_public: true,
        ingredients: [
            { ingredient_id: 2, ingredient_name: '洋蔥', quantity: 1, unit: '個' },
            { ingredient_id: 1, ingredient_name: '番茄', quantity: 1, unit: '個' },
        ],
    },
    {
        id: '4',
        title: '蛋炒飯',
        description: '快手飯，剩飯利用好選擇',
        difficulty: 2,
        cooking_time: 10,
        steps: '1. 準備冷飯 2. 炒蛋 3. 炒飯',
        creator_id: 'system',
        is_public: true,
        ingredients: [{ ingredient_id: 3, ingredient_name: '雞蛋', quantity: 2, unit: '個' }],
    },
]

const recipes = ref(mockRecipes)
const filterByMyIngredients = ref(false)
const selectedCookingTime = ref('all')

const renderStars = (difficulty) => {
    return '⭐'.repeat(difficulty) + '☆'.repeat(5 - difficulty)
}
</script>

<template>
    <div class="min-h-screen bg-gradient-to-br from-orange-50 to-yellow-50">
        <div class="mt-28 max-w-7xl mx-auto px-4 pb-12">
            <!-- 標題區 -->
            <div class="mb-8">
                <h1 class="text-5xl font-bold text-gray-800 mb-2">🍳 食譜</h1>
                <p class="text-gray-600">發現美味食譜，開始烹飪之旅</p>
            </div>

            <!-- 篩選區 -->
            <div class="bg-white rounded-2xl shadow-lg p-6 mb-8">
                <!-- 食譜類型篩選 -->
                <div class="mb-6">
                    <h3 class="text-sm font-bold text-gray-700 mb-3">食譜類型</h3>
                    <div class="flex gap-3">
                        <button
                            @click="filterByMyIngredients = false"
                            :class="
                                !filterByMyIngredients
                                    ? 'bg-gradient-to-r from-orange-500 to-orange-600 text-white shadow-md'
                                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            "
                            class="px-6 py-2 rounded-full font-semibold transition-all duration-200"
                        >
                            📚 所有食譜
                        </button>
                        <button
                            @click="filterByMyIngredients = true"
                            :class="
                                filterByMyIngredients
                                    ? 'bg-gradient-to-r from-green-500 to-green-600 text-white shadow-md'
                                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            "
                            class="px-6 py-2 rounded-full font-semibold transition-all duration-200"
                        >
                            ✅ 根據我的食材
                        </button>
                    </div>
                </div>

                <!-- 烹飪時間篩選 -->
                <div>
                    <h3 class="text-sm font-bold text-gray-700 mb-3">烹飪時間</h3>
                    <div class="flex gap-2 flex-wrap">
                        <button
                            @click="selectedCookingTime = 'all'"
                            :class="selectedCookingTime === 'all' ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
                            class="px-4 py-2 rounded-full text-sm font-semibold transition-all duration-200"
                        >
                            所有
                        </button>
                        <button
                            @click="selectedCookingTime = '15'"
                            :class="selectedCookingTime === '15' ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
                            class="px-4 py-2 rounded-full text-sm font-semibold transition-all duration-200"
                        >
                            ⚡ 15分以下
                        </button>
                        <button
                            @click="selectedCookingTime = '30'"
                            :class="selectedCookingTime === '30' ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
                            class="px-4 py-2 rounded-full text-sm font-semibold transition-all duration-200"
                        >
                            ⏱ 30分以下
                        </button>
                        <button
                            @click="selectedCookingTime = '60'"
                            :class="selectedCookingTime === '60' ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
                            class="px-4 py-2 rounded-full text-sm font-semibold transition-all duration-200"
                        >
                            🍲 1小時以上
                        </button>
                    </div>
                </div>
            </div>

            <!-- 食譜卡片網格 -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div
                    v-for="recipe in recipes"
                    :key="recipe.id"
                    class="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden group hover:transform hover:scale-105"
                >
                    <!-- 卡片頭部 - 帶漸層背景 -->
                    <div class="bg-gradient-to-r from-orange-400 to-pink-400 h-24 relative overflow-hidden">
                        <div class="absolute inset-0 opacity-20 group-hover:opacity-40 transition-opacity duration-300">
                            <div class="text-6xl">🍳</div>
                        </div>
                    </div>

                    <!-- 卡片內容 -->
                    <div class="p-5">
                        <!-- 標題 -->
                        <h3 class="text-xl font-bold text-gray-800 mb-1 line-clamp-2">{{ recipe.title }}</h3>

                        <!-- 描述 -->
                        <p class="text-sm text-gray-600 mb-4 line-clamp-2">{{ recipe.description }}</p>

                        <!-- 難度和時間 -->
                        <div class="flex items-center justify-between mb-4 pb-4 border-b border-gray-200">
                            <div class="flex items-center gap-1">
                                <span class="text-xs font-semibold text-gray-700">難度</span>
                                <span class="text-sm">{{ renderStars(recipe.difficulty) }}</span>
                            </div>
                            <div class="flex items-center gap-1">
                                <span class="text-lg">⏱</span>
                                <span class="text-sm font-semibold text-gray-700">{{ recipe.cooking_time }} 分</span>
                            </div>
                        </div>

                        <!-- 所需食材預覽 -->
                        <div class="mb-4">
                            <p class="text-xs font-semibold text-gray-700 mb-2">所需食材</p>
                            <div class="flex flex-wrap gap-1">
                                <span
                                    v-for="ing in recipe.ingredients.slice(0, 3)"
                                    :key="ing.ingredient_id"
                                    class="text-xs bg-orange-100 text-orange-700 px-2 py-1 rounded-full"
                                >
                                    {{ ing.ingredient_name }}
                                </span>
                                <span v-if="recipe.ingredients.length > 3" class="text-xs bg-gray-100 text-gray-700 px-2 py-1 rounded-full">
                                    +{{ recipe.ingredients.length - 3 }}
                                </span>
                            </div>
                        </div>

                        <!-- 按鈕 -->
                        <div class="flex gap-2">
                            <button
                                class="flex-1 bg-blue-500 hover:bg-blue-600 text-white px-3 py-2 rounded-lg text-sm font-semibold transition-colors duration-200"
                            >
                                👁 查看詳情
                            </button>
                            <button
                                class="flex-1 bg-green-500 hover:bg-green-600 text-white px-3 py-2 rounded-lg text-sm font-semibold transition-colors duration-200"
                            >
                                🍽 我要做
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 空狀態 -->
            <div v-if="recipes.length === 0" class="text-center py-16">
                <p class="text-4xl mb-4">🍽️</p>
                <p class="text-gray-600 text-lg">暫時沒有符合條件的食譜</p>
            </div>
        </div>
    </div>
</template>
