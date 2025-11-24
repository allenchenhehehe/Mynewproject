<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

const recipe = ref(null)
const emit = defineEmits(['handlegoback', 'addToShopping'])
const props = defineProps({
    recipe: Object,
    fridgeItems: Array,
})

const isFavorited = ref(false)
const userRating = ref(0)
const commentText = ref('')
const comments = ref([
    {
        id: 1,
        author: '使用者A',
        rating: 5,
        text: '超級好吃！強烈推薦！',
        date: '2024-11-20'
    },
    {
        id: 2,
        author: '使用者B',
        rating: 4,
        text: '味道不錯，但有點辣',
        date: '2024-11-19'
    }
])

function handleGoBack() {
    emit('handlegoback')
}

function toggleFavorite() {
    isFavorited.value = !isFavorited.value
    toast.success(isFavorited.value ? '✅ 已收藏食譜！' : '❌ 已取消收藏', {
        autoClose: 1500,
    })
}

function addAllToShoppingList() {
    const timestamp = Date.now()
    const itemsToAdd = props.recipe.ingredients.map((ingredient, index) => ({
        id: timestamp + index,
        ingredient_id: null,
        ingredient_name: ingredient.ingredient_name,
        quantity: ingredient.quantity,
        unit: ingredient.unit,
        is_purchased: false,
    }))
    emit('addToShopping', itemsToAdd, props.recipe.title, props.recipe.id)
    toast.success(`✅ 已將 ${itemsToAdd.length} 項食材添加到購物清單！`, {
        autoClose: 2000,
    })
}

function getMissingIngredients() {
    return props.recipe.ingredients.filter((recipeIng) => {
        return !props.fridgeItems.some((fridgeItem) => fridgeItem.ingredient_id === recipeIng.ingredient_id)
    })
}

function getMissingIngredientsText() {
    return getMissingIngredients()
        .map((ing) => `${ing.ingredient_name}`)
        .join('、')
}

function getHavingIngredients() {
    return props.recipe.ingredients.filter((recipeIng) => {
        return props.fridgeItems.some((fridgeItem) => fridgeItem.ingredient_id === recipeIng.ingredient_id)
    })
}

function submitComment() {
    if (!commentText.value.trim() || userRating.value === 0) {
        toast.error('請輸入評論並選擇評分！', {
            autoClose: 1500,
        })
        return
    }

    const newComment = {
        id: comments.value.length + 1,
        author: '你',
        rating: userRating.value,
        text: commentText.value,
        date: new Date().toISOString().split('T')[0]
    }

    comments.value.unshift(newComment)
    commentText.value = ''
    userRating.value = 0

    toast.success('✅ 評論已發佈！', {
        autoClose: 1500,
    })
}

function getAverageRating() {
    if (comments.value.length === 0) return 0
    const sum = comments.value.reduce((acc, comment) => acc + comment.rating, 0)
    return (sum / comments.value.length).toFixed(1)
}
</script>

<template>
    <div class="mt-28 max-w-6xl mx-auto px-6 pb-20 font-sans text-black">
        
        <!-- 標題區 -->
        <div class="flex flex-col md:flex-row justify-between items-start mb-10 gap-4">
            <h1 class="text-5xl font-black uppercase tracking-tighter relative inline-block">
                RECIPE
                <span class="absolute -top-4 -right-8 bg-pink-300 text-black text-xs font-bold px-2 py-1 border-2 border-black rotate-12 shadow-[2px_2px_0px_0px_black]">
                    YUMMY!
                </span>
            </h1>
            <button 
                @click="handleGoBack"
                class="border-2 border-black bg-white font-black px-6 py-3 uppercase tracking-wide shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all cursor-pointer"
            >
                返回列表
            </button>
        </div>

        <!-- 主要內容 -->
        <div v-if="props.recipe" class="space-y-6">
            
            <!-- 頂部：圖片 + 基本信息 + 互動區 -->
            <div class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] overflow-hidden">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-0">
                    
                    <!-- 左側：食譜圖片 -->
                    <div class="border-b-2 md:border-b-0 md:border-r-2 border-black bg-gray-300 overflow-hidden">
                        <img 
                            :src="props.recipe.image_url" 
                            :alt="props.recipe.title" 
                            class="w-full h-full object-cover"
                        />
                    </div>

                    <!-- 右側：標題 + 基本信息 + 互動 -->
                    <div class="md:col-span-2 p-6 space-y-4 flex flex-col">
                        
                        <!-- 標題 + 創作者（頂部並排） -->
                        <div class="flex flex-col md:flex-row justify-between items-start gap-4 mb-2">
                            <h2 class="text-4xl font-black uppercase tracking-tighter break-words flex-1">{{ props.recipe.title }}</h2>
                            <div class="border-2 border-black bg-blue-50 p-3 whitespace-nowrap">
                                <span class="text-xs font-black uppercase tracking-wide text-gray-600 block">創作者</span>
                                <div class="font-black text-lg">{{ props.recipe.creator_id }}</div>
                            </div>
                        </div>

                        <!-- 烹飪時間和難度 -->
                        <div class="grid grid-cols-2 gap-3">
                            <div class="border-2 border-black bg-yellow-100 p-4">
                                <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">烹飪時間</div>
                                <div class="text-3xl font-black">{{ props.recipe.coocking_time }}</div>
                                <div class="text-xs font-bold">分鐘</div>
                            </div>
                            <div class="border-2 border-black bg-blue-100 p-4">
                                <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">難度等級</div>
                                <div class="flex gap-1">
                                    <Icon 
                                        v-for="n in props.recipe.difficulty" 
                                        :key="n"
                                        icon="mdi:star"
                                        class="text-sm text-amber-400"
                                    />
                                </div>
                            </div>
                        </div>

                        <!-- 互動按鈕區（收藏） -->
                        <button 
                            @click="toggleFavorite"
                            :class="isFavorited ? 'bg-red-400 text-white' : 'bg-white text-black'"
                            class="w-full border-2 border-black font-black py-3 px-4 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] transition-all cursor-pointer"
                        >
                            {{ isFavorited ? '❤️ 已收藏' : '🤍 收藏' }}
                        </button>

                        <!-- 評分區 -->
                        <div class="border-2 border-black bg-yellow-50 p-4">
                            <div class="text-xs font-black uppercase tracking-wide mb-3">食譜評分</div>
                            <div class="flex justify-between items-center">
                                <div class="flex gap-1">
                                    <span 
                                        v-for="n in 5" 
                                        :key="n"
                                        :class="n <= Math.round(getAverageRating()) ? 'text-amber-400' : 'text-gray-300'"
                                        class="text-2xl"
                                    >
                                        ★
                                    </span>
                                </div>
                                <div class="font-black text-sm">{{ getAverageRating() }} ({{ comments.length }} 則評論)</div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

            <!-- 食材區域 -->
            <div class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] p-6 space-y-4">
                <h3 class="text-2xl font-black uppercase tracking-wide">準備食材</h3>
                
                <div class="space-y-2">
                    <div 
                        v-for="ingredient in props.recipe.ingredients" 
                        :key="ingredient.id"
                        :class="getHavingIngredients().some(h => h.ingredient_id === ingredient.ingredient_id) 
                            ? 'bg-green-100 border-green-400' 
                            : 'bg-red-100 border-red-400'"
                        class="border-2 p-3 flex justify-between items-center font-bold"
                    >
                        <span>{{ ingredient.ingredient_name }}</span>
                        <span class="bg-black text-white px-3 py-1 text-sm font-black border border-black">
                            {{ ingredient.quantity }}{{ ingredient.unit }}
                        </span>
                    </div>
                </div>
            </div>

            <!-- 食材狀態提示 + 購物清單按鈕 -->
            <div class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- 已有食材 -->
                    <div v-if="getHavingIngredients().length > 0" class="border-2 border-green-400 bg-green-100 p-6 shadow-[4px_4px_0px_0px_black]">
                        <div class="font-black text-green-700 uppercase mb-3 text-lg">已有食材</div>
                        <p class="font-bold text-gray-700 break-words">
                            {{ getHavingIngredients().map(ing => ing.ingredient_name).join('、') }}
                        </p>
                    </div>

                    <!-- 缺少的食材 -->
                    <div v-if="getMissingIngredients().length > 0" class="border-2 border-red-400 bg-red-100 p-6 shadow-[4px_4px_0px_0px_black]">
                        <div class="font-black text-red-700 uppercase mb-3 text-lg">缺少食材</div>
                        <p class="font-bold text-gray-700 break-words">
                            {{ getMissingIngredientsText() }}
                        </p>
                    </div>
                </div>

                <!-- 加入購物清單按鈕 -->
                <button 
                    @click="addAllToShoppingList"
                    class="w-full border-2 border-black bg-orange-300 text-black font-black py-4 px-6 uppercase tracking-wide shadow-[4px_4px_0px_0px_black]  hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all text-lg cursor-pointer"
                >
                    加入購物清單
                </button>
            </div>

            <!-- 詳細步驟 -->
            <div class="border-2 border-black bg-yellow-50 shadow-[4px_4px_0px_0px_black] p-6 space-y-4">
                <h3 class="text-2xl font-black uppercase tracking-wide mb-6">📝 詳細步驟</h3>
                <div class="space-y-4">
                    <div 
                        v-for="(step, index) in props.recipe.step.split('\n\n')"
                        :key="index"
                        class="border-l-4 border-black pl-4 py-2"
                    >
                        <div class="font-black mb-1 text-sm text-gray-600 uppercase">步驟 {{ index + 1 }}</div>
                        <p class="text-gray-700 leading-relaxed whitespace-pre-wrap">{{ step }}</p>
                    </div>
                </div>
            </div>

            <!-- 評論區域 -->
            <div class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] p-6 space-y-6">
                <h3 class="text-2xl font-black uppercase tracking-wide">評論區</h3>

                <!-- 發表評論 -->
                <div class="border-2 border-black bg-gray-50 p-6 space-y-4">
                    <h4 class="font-black uppercase tracking-wide">發表你的評論</h4>
                    
                    <!-- 評分選擇 -->
                    <div class="space-y-2">
                        <label class="font-bold text-sm uppercase">你的評分</label>
                        <div class="flex gap-2">
                            <button
                                v-for="n in 5"
                                :key="n"
                                @click="userRating = n"
                                :class="n <= userRating ? 'text-amber-400 scale-110' : 'text-gray-300'"
                                class="text-4xl cursor-pointer transition-all hover:scale-125"
                            >
                                ★
                            </button>
                        </div>
                    </div>

                    <!-- 評論文本 -->
                    <div class="space-y-2">
                        <label class="font-bold text-sm uppercase">評論</label>
                        <textarea
                            v-model="commentText"
                            placeholder="分享你的想法和建議..."
                            class="w-full border-2 border-black p-3 font-bold bg-white focus:bg-yellow-50 focus:outline-none focus:shadow-[2px_2px_0px_0px_black] transition-all h-24 resize-none"
                        />
                    </div>

                    <!-- 提交按鈕 -->
                    <button
                        @click="submitComment"
                        class="w-full border-2 border-black bg-green-400 text-black font-black py-3 px-6 uppercase tracking-wide shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all"
                    >
                        發佈評論
                    </button>
                </div>

                <!-- 評論列表 -->
                <div class="space-y-4">
                    <div
                        v-for="comment in comments"
                        :key="comment.id"
                        class="border-2 border-black bg-gray-50 p-4"
                    >
                        <!-- 評論頭部 -->
                        <div class="flex justify-between items-start mb-2">
                            <div>
                                <div class="font-black text-lg">{{ comment.author }}</div>
                                <div class="text-xs text-gray-600 font-bold">{{ comment.date }}</div>
                            </div>
                            <div class="flex gap-0.5">
                                <span 
                                    v-for="n in 5" 
                                    :key="n"
                                    :class="n <= comment.rating ? 'text-amber-400' : 'text-gray-300'"
                                    class="text-lg"
                                >
                                    ★
                                </span>
                            </div>
                        </div>

                        <!-- 評論文本 -->
                        <p class="text-gray-700 leading-relaxed">{{ comment.text }}</p>
                    </div>

                    <!-- 無評論提示 -->
                    <div v-if="comments.length === 0" class="border-2 border-dashed border-gray-300 p-6 text-center text-gray-500 font-bold">
                        暫無評論，成為第一個評論者吧！
                    </div>
                </div>
            </div>

        </div>

    </div>
</template>

<style scoped>
.break-words {
    word-break: break-word;
}
</style>