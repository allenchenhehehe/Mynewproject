<script setup>
import { ref, computed } from 'vue'
import { useRecipeStore } from '@/stores'
import { useFridgeStore } from '@/stores'
import { useNavigationStore } from '@/stores'

// 使用 stores
const recipeStore = useRecipeStore()
const fridgeStore = useFridgeStore()
const navStore = useNavigationStore()

const filterByMyIngredient = ref(false)
const selectByCookingTime = ref('all')
const selectByDifficulty = ref('all')
const searchQuery = ref('')

const fridgeIngredientMap = computed(() => {
  const map = {}
  fridgeStore.fridgeItems.forEach((item) => {
    const key = item.name  // 改用名稱而不是ID
    if (!map[key]) {
      map[key] = 0
    }
    map[key] += item.quantity
  })
  return map
})

function canMakeRecipe(recipe) {
  return recipe.ingredients.every((ingredient) => {
    const fridgeQuantity = fridgeIngredientMap.value[ingredient.ingredient_name] || 0
    return fridgeQuantity >= ingredient.quantity
  })
}

const filterRecipes = computed(() => {
    let result = recipeStore.recipes
  
  // 按搜尋篩選
  if (searchQuery.value.trim()) {
    result = result.filter((recipe) => 
      recipe.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      recipe.description.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  // 按食材篩選
  if (filterByMyIngredient.value) {
    if (Object.keys(fridgeIngredientMap.value).length === 0) {
      return []
    }
    result = result.filter((recipe) => canMakeRecipe(recipe))
  }

  // 按時間篩選
  if (selectByCookingTime.value === 'all') {
    // 保持所有食譜
  } else if (selectByCookingTime.value === '15') {
    result = result.filter((recipe) => recipe.cooking_time <= 15)
  } else if (selectByCookingTime.value === '30') {
    result = result.filter((recipe) => recipe.cooking_time <= 30)
  } else if (selectByCookingTime.value === '60') {
    result = result.filter((recipe) => recipe.cooking_time >= 60)
  }

  // 按難度篩選
  if (selectByDifficulty.value === 'all') {
    // 保持所有食譜
  } else if (selectByDifficulty.value === 'easy') {
    result = result.filter((recipe) => recipe.difficulty == 1)
  } else if (selectByDifficulty.value === 'medium') {
    result = result.filter((recipe) => recipe.difficulty >= 2 && recipe.difficulty <= 3)
  } else if (selectByDifficulty.value === 'hard') {
    result = result.filter((recipe) => recipe.difficulty >= 4)
  } 
  return result
})

function handleCookRecipe(recipe) {
    navStore.goToRecipeDetail(recipe)
}
</script>

<template>
    <div class="mt-28 max-w-7xl mx-auto px-6 pb-20 font-sans text-black">
        
        <!-- 標題 -->
        <div class="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
            <h1 class="text-5xl font-black uppercase tracking-tighter relative inline-block">
                WHAT TO COOK?
                <span class="absolute -top-4 -right-8 bg-orange-300 text-black text-xs font-bold px-2 py-1 border-2 border-black rotate-12 shadow-[2px_2px_0px_0px_black]">
                    LET'S COOK!
                </span>
            </h1>
        </div>

        <!-- 篩選區塊 -->
        <div class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] mb-8 p-6">
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                
                <!-- 左側：篩選選項 -->
                <div class="lg:col-span-2 space-y-6">
                    <!-- 食譜類型 -->
                    <div class = "text-lg">
                        <h3 class="font-black uppercase mb-3 tracking-wide">食譜類型</h3>
                        <div class="flex flex-wrap gap-2">
                            <button
                                @click="filterByMyIngredient = false"
                                :class="!filterByMyIngredient ? 'bg-black text-white border-black' : 'bg-gray-100 text-black border-gray-400'"
                                class="border-2 font-bold px-4 py-2 cursor-pointer hover:shadow-[2px_2px_0px_0px_black] transition-all"
                            >
                                所有食譜
                            </button>
                            <button
                                @click="filterByMyIngredient = true"
                                :class="filterByMyIngredient ? 'bg-black text-white border-black' : 'bg-gray-100 text-black border-gray-400'"
                                class="border-2 font-bold px-4 py-2 cursor-pointer hover:shadow-[2px_2px_0px_0px_black] transition-all"
                            >
                                根據我的食材
                            </button>
                        </div>
                    </div>

                    <!-- 烹飪時間 -->
                    <div class = "text-lg">
                        <h3 class="font-black uppercase mb-3 tracking-wide">烹飪時間</h3>
                        <div class="flex flex-wrap gap-2">
                            <button
                                v-for="time in ['all', '15', '30', '60']"
                                :key="time"
                                @click="selectByCookingTime = time"
                                :class="selectByCookingTime == time ? 'bg-black text-white border-black' : 'bg-gray-100 text-black border-gray-400'"
                                class="border-2 font-bold px-3 py-1 cursor-pointer hover:shadow-[2px_2px_0px_0px_black] transition-all"
                            >
                                {{ time === 'all' ? '所有' : time === '15' ? '< 15分' : time === '30' ? '< 30分' : '> 60分' }}
                            </button>
                        </div>
                    </div>

                    <!-- 難易程度 -->
                    <div class = "text-lg">
                        <h3 class="font-black uppercase mb-3 tracking-wide">難易程度</h3>
                        <div class="flex flex-wrap gap-2">
                            <button
                                v-for="level in ['all', 'easy', 'medium', 'hard']"
                                :key="level"
                                @click="selectByDifficulty = level"
                                :class="selectByDifficulty == level ? 'bg-black text-white border-black' : 'bg-gray-100 text-black border-gray-400'"
                                class="border-2 font-bold px-3 py-1 cursor-pointer hover:shadow-[2px_2px_0px_0px_black] transition-all"
                            >
                                {{ level === 'all' ? '所有' : level === 'easy' ? '簡易' : level === 'medium' ? '中等' : '困難' }}
                            </button>
                        </div>
                    </div>
                </div>

                <!-- 右側：搜尋 + 統計 -->
                <div class="lg:col-span-1 space-y-4">
                    <!-- 搜尋欄 -->
                    <div class = "text-lg">
                        <h3 class="font-black uppercase mb-3 tracking-wide">搜尋食譜</h3>
                        <div class="relative">
                            <input 
                                v-model="searchQuery"
                                type="text" 
                                placeholder="輸入食譜名稱..."
                                class="w-full border-2 border-black px-4 py-2 font-bold bg-yellow-50 focus:bg-yellow-100 focus:outline-none focus:shadow-[2px_2px_0px_0px_black] transition-all pr-10 mb-3"
                            />
                            <button
                                v-if="searchQuery"
                                @click="searchQuery = ''"
                                class="absolute right-2 top-2 font-black text-lg text-gray-500 hover:text-black transition-colors"
                            >
                                ✕
                            </button>
                        </div>
                    </div>

                    <!-- 統計卡片 -->
                    <div class="text-lg border-2 border-black bg-linear-to-br from-orange-100 to-yellow-100 p-4 space-y-2">
                        <div class="flex justify-between items-center pb-2 border-b-2 border-dashed border-black">
                            <span class="font-bold ">總食譜</span>
                            <span class="font-black">{{ recipeStore.recipes.length }}</span>
                        </div>
                        <div class="flex justify-between items-center pb-2 border-b-2 border-dashed border-black">
                            <span class="font-bold">符合條件</span>
                            <span class="font-black">{{ filterRecipes.length }}</span>
                        </div>
                        <div class="flex justify-between items-center">
                            <span class="font-bold">配對度</span>
                            <span class="font-black">
                                {{ recipeStore.recipes.length > 0 ? Math.round((filterRecipes.length / recipeStore.recipes.length) * 100) : 0 }}%
                            </span>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <!-- 食譜卡片 -->
        <div class="text-lg grid grid-cols-1 md:grid-cols-2 gap-6">
            <div
                v-for="recipe in filterRecipes"
                :key="recipe.id"
                class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all overflow-hidden group"
            >
                <!-- 食譜圖片 -->
                <div class="relative overflow-hidden h-90 border-b-2 border-black bg-gray-200">
                    <img 
                        :src="recipe.image_url" 
                        :alt="recipe.title" 
                        class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                    />
                    <div class="absolute top-2 right-2 bg-black text-white border-2 border-white px-3 py-1 font-bold ">
                        {{ recipe.coocking_time }} MIN
                    </div>
                </div>

                <!-- 內容 -->
                <div class="text-lg p-4 space-y-3">
                    <!-- 標題 -->
                    <div>
                        <h3 class="font-black text-xl uppercase mb-1">{{ recipe.title }}</h3>
                        <p class=" text-gray-600 line-clamp-2">{{ recipe.description }}</p>
                    </div>

                    <!-- 難度評分 -->
                    <div class="flex items-center gap-2 py-2 border-t-2 border-b-2 border-dashed border-gray-300">
                        <span class="font-bold uppercase tracking-wide">難度:</span>
                        <div class="flex gap-0.5">
                            <Icon 
                                v-for="n in recipe.difficulty" 
                                :key="n"
                                icon="mdi:star"
                                class="text-sm text-amber-400"
                            />
                        </div>
                    </div>

                    <!-- 按鈕 -->
                    <button 
                        @click="handleCookRecipe(recipe)"
                       class="w-full bg-amber-300 text-black border-2 border-black font-black py-3 uppercase tracking-wide shadow-[4px_4px_0px_0px_black]  hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all cursor-pointer"
                    >
                        我要做!
                    </button>
                </div>
            </div>
        </div>

        <!-- 沒有食譜提示 -->
        <div v-if="filterRecipes.length === 0" class="border-4 border-black bg-yellow-200 shadow-[4px_4px_0px_0px_black] p-8 text-center mt-8">
            <p class="font-black text-2xl mb-2">沒有符合的食譜</p>
            <p class="text-gray-700 font-semibold">試試調整你的篩選條件吧！</p>
        </div>
    </div>
</template>

<style scoped>
.line-clamp-2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
</style>