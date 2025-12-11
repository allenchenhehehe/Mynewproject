<script setup>
import { ref, computed, onMounted } from 'vue'  //加上 onMounted
import { useFavoriteStore } from '@/stores'
import { useNavigationStore } from '@/stores'
import { useRecipeStore } from '@/stores'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

// 使用 stores
const favoriteStore = useFavoriteStore() 
const navStore = useNavigationStore()
const recipeStore = useRecipeStore()

const sortBy = ref('recent') // recent, name, difficulty

onMounted(async () => {
    await favoriteStore.fetchFavorites()
})

const sortedFavorites = computed(() => {
    let sorted = [...favoriteStore.favorites]
    
    switch(sortBy.value) {
        case 'recent':
            sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            break
        case 'name':
            sorted.sort((a, b) => a.recipeTitle.localeCompare(b.recipeTitle, 'zh'))
            break
        case 'difficulty':
            sorted.sort((a, b) => b.recipeDifficulty - a.recipeDifficulty)
            break
    }
    
    return sorted
})

function viewRecipe(favorite) {
    const fullRecipe = recipeStore.recipes.find(r => r.id === favorite.recipeId)
    
    if (fullRecipe) {
        navStore.goToRecipeDetail(fullRecipe)
    } else {
        toast.error('找不到食譜詳情', {
            autoClose: 1000,
        })
    }
}

async function removeFavorite(recipeId) {
    if (confirm('確定要取消收藏嗎？')) {
        const result = await favoriteStore.removeFavorite(recipeId)
        if (result.success) {
            toast.success('取消收藏成功', {
                autoClose: 1000,
            })
        } else {
            toast.error(result.error || '操作失敗', {
                autoClose: 1000,
            })
        }
    }
}

function formatDate(dateString) {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-TW', { year: 'numeric', month: '2-digit', day: '2-digit' })
}
</script>

<template>
    <div class="mt-28 max-w-6xl mx-auto px-6 pb-20 font-sans text-black">
        
        <!-- 標題 -->
        <div class="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
            <h1 class="text-5xl font-black uppercase tracking-tighter relative inline-block">
                FAVORITES
                <span class="absolute -top-4 -right-8 bg-red-300 text-black text-xs font-bold px-2 py-1 border-2 border-black rotate-12 shadow-[2px_2px_0px_0px_black]">
                    LOVED!
                </span>
            </h1>
        </div>

        <!-- Loading 狀態 -->
        <div v-if="favoriteStore.loading && favoriteStore.favorites.length === 0" 
             class="text-center py-20">
            <div class="inline-block border-4 border-black bg-yellow-300 px-8 py-4 font-black text-xl animate-pulse">
                LOADING...
            </div>
        </div>

        <!-- 排序區 -->
        <div v-if="!favoriteStore.loading || favoriteStore.favorites.length > 0"
             class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] mb-8 p-6">
            <h3 class="font-black uppercase mb-4 tracking-wide">排序方式</h3>
            <div class="flex flex-wrap gap-2">
                <button
                    v-for="option in [
                        { label: '最新收藏', value: 'recent' },
                        { label: '名稱排序', value: 'name' },
                        { label: '難度排序', value: 'difficulty' }
                    ]"
                    :key="option.value"
                    @click="sortBy = option.value"
                    :class="sortBy === option.value 
                        ? 'bg-black text-white border-black' 
                        : 'bg-gray-100 text-black border-gray-400'"
                    class="border-2 font-bold px-4 py-2 cursor-pointer hover:shadow-[2px_2px_0px_0px_black] transition-all"
                >
                    {{ option.label }}
                </button>
            </div>
        </div>

        <!-- 收藏食譜卡片 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div
                v-for="favorite in sortedFavorites"
                :key="favorite.id"
                class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all overflow-hidden group"
            >
                <!-- 食譜圖片 -->
                <div class="relative overflow-hidden h-48 border-b-2 border-black bg-gray-200">
                    <img 
                        :src="favorite.recipeImageUrl" 
                        :alt="favorite.recipeTitle" 
                        class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                    />
                    <div class="absolute top-2 right-2 bg-red-400 text-white border-2 border-white px-3 py-1 font-bold text-sm">
                        {{ formatDate(favorite.createdAt) }}
                    </div>
                </div>

                <!-- 內容 -->
                <div class="p-4 space-y-3">
                    <!-- 標題 -->
                    <div>
                        <h3 class="font-black text-xl uppercase mb-1">{{ favorite.recipeTitle }}</h3>
                        <p class="text-sm text-gray-600 line-clamp-2">{{ favorite.recipeDescription }}</p>
                    </div>

                    <!-- 信息區 -->
                    <div class="grid grid-cols-2 gap-2 py-2 border-t-2 border-b-2 border-dashed border-gray-300">
                        <div class="text-xs font-bold">
                            <span class="text-gray-600">烹飪時間</span>
                            <div class="font-black text-lg">{{ favorite.recipeCookingTime }}分</div>
                        </div>
                        <div class="text-xs font-bold">
                            <span class="text-gray-600">難度</span>
                            <div class="flex gap-0.5 mt-1">
                                <Icon 
                                    v-for="n in favorite.recipeDifficulty" 
                                    :key="n"
                                    icon="mdi:star"
                                    class="text-sm text-amber-400"
                                />
                            </div>
                        </div>
                    </div>

                    <!-- 按鈕 -->
                    <div class="flex gap-2">
                        <button 
                            @click="viewRecipe(favorite)"
                            class="flex-1 bg-blue-400 text-black border-2 border-black font-black py-2 px-3 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-sm"
                        >
                            查看食譜
                        </button>
                        <button 
                            @click="removeFavorite(favorite.recipeId)"
                            class="flex-1 bg-red-300 text-black border-2 border-black font-black py-2 px-3 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-sm"
                        >
                            取消收藏
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 空狀態 -->
        <div v-if="favoriteStore.favorites.length === 0 && !favoriteStore.loading" 
             class="border-4 border-black bg-yellow-200 shadow-[4px_4px_0px_0px_black] p-8 text-center mt-8">
            <p class="font-black text-2xl mb-2">還沒有收藏任何食譜</p>
            <p class="text-gray-700 font-semibold">快去食譜頁面收藏你喜歡的食譜吧！</p>
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