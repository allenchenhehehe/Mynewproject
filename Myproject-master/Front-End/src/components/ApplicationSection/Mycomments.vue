<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores'

// 使用 userStore
const userStore = useUserStore()

const sortBy = ref('recent') // recent, rating, likes
const editingId = ref(null)
const editText = ref('')

const sortedComments = computed(() => {
    if (!userStore.allComments || userStore.allComments.length === 0) return []
    
    let sorted = [...userStore.allComments]
    
    switch(sortBy.value) {
        case 'recent':
            sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            break
        case 'rating':
            sorted.sort((a, b) => b.rating - a.rating)
            break
        case 'likes':
            sorted.sort((a, b) => (b.likes || 0) - (a.likes || 0))
            break
    }
    
    return sorted
})

const stats = computed(() => {
    if (!userStore.allComments || userStore.allComments.length === 0) {
        return {
            total: 0,
            avgRating: 0,
            totalLikes: 0
        }
    }
    return {
        total: userStore.allComments.length,
        avgRating: (userStore.allComments.reduce((sum, c) => sum + c.rating, 0) / userStore.allComments.length).toFixed(1),
        totalLikes: userStore.allComments.reduce((sum, c) => sum + (c.likes || 0), 0)
    }
})

function startEdit(comment) {
    editingId.value = comment.id
    editText.value = comment.text
}

function cancelEdit() {
    editingId.value = null
    editText.value = ''
}

function saveEdit(id) {
    userStore.updateComment(id, editText.value)
    cancelEdit()
}

function deleteComment(id) {
    if (confirm('確定要刪除這則評論嗎？')) {
        userStore.deleteComment(id)
    }
}
</script>

<template>
    <div class="mt-28 max-w-6xl mx-auto px-6 pb-20 font-sans text-black">
        
        <!-- 標題 -->
        <div class="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
            <h1 class="text-5xl font-black uppercase tracking-tighter relative inline-block">
                MY COMMENTS
                <span class="absolute -top-4 -right-8 bg-blue-300 text-black text-xs font-bold px-2 py-1 border-2 border-black rotate-12 shadow-[2px_2px_0px_0px_black]">
                    SPEAK!
                </span>
            </h1>
        </div>

        <!-- 統計卡片 -->
        <div class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] mb-8 p-6">
            <div class="grid grid-cols-3 gap-4">
                <div class="border-2 border-black bg-blue-100 p-4 text-center">
                    <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">評論總數</div>
                    <div class="text-4xl font-black">{{ stats.total }}</div>
                </div>
                <div class="border-2 border-black bg-yellow-100 p-4 text-center">
                    <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">平均評分</div>
                    <div class="text-4xl font-black">{{ stats.avgRating }}</div>
                </div>
                <div class="border-2 border-black bg-green-100 p-4 text-center">
                    <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">總點讚</div>
                    <div class="text-4xl font-black">{{ stats.totalLikes }}</div>
                </div>
            </div>
        </div>

        <!-- 排序區 -->
        <div class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] mb-8 p-6">
            <h3 class="font-black uppercase mb-4 tracking-wide">排序方式</h3>
            <div class="flex flex-wrap gap-2">
                <button
                    v-for="option in [
                        { label: '最新評論', value: 'recent' },
                        { label: '評分排序', value: 'rating' },
                        { label: '點讚排序', value: 'likes' }
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

        <!-- 評論列表 -->
        <div class="space-y-4">
            <div
                v-for="comment in sortedComments"
                :key="comment.id"
                class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all p-6"
            >
                <!-- 評論頭部 -->
                <div class="flex flex-col md:flex-row justify-between items-start gap-4 mb-4 pb-4 border-b-2 border-dashed border-gray-300">
                    <div class="flex-1">
                        <h3 class="font-black text-2xl uppercase mb-2">{{ comment.recipeTitle }}</h3>
                        <div class="text-xs text-gray-600 font-bold">{{ comment.createdAt }}</div>
                    </div>
                    <div class="flex gap-4 items-center">
                        <!-- 評分 -->
                        <div class="text-center">
                            <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">評分</div>
                            <div class="flex gap-0.5">
                                <span 
                                    v-for="n in 5" 
                                    :key="n"
                                    :class="n <= comment.rating ? 'text-amber-400' : 'text-gray-300'"
                                    class="text-2xl"
                                >
                                    ★
                                </span>
                            </div>
                        </div>
                        <!-- 點讚 -->
                        <div class="text-center border-l-2 border-black pl-4">
                            <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">點讚</div>
                            <div class="font-black text-2xl">{{ comment.likes }}</div>
                        </div>
                    </div>
                </div>

                <!-- 評論文本 -->
                <div v-if="editingId === comment.id">
                    <textarea
                        v-model="editText"
                        class="w-full border-2 border-black p-3 font-bold bg-white focus:bg-yellow-50 focus:outline-none focus:shadow-[2px_2px_0px_0px_black] transition-all h-24 resize-none mb-4"
                    />
                </div>
                <p v-else class="text-gray-700 leading-relaxed mb-4 font-bold">
                    {{ comment.text }}
                </p>

                <!-- 動作按鈕 -->
                <div class="flex gap-2">
                    <button 
                        v-if="editingId === comment.id"
                        @click="saveEdit(comment.id)"
                        class="flex-1 bg-green-400 text-black border-2 border-black font-black py-2 px-3 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-sm"
                    >
                        保存
                    </button>
                    <button 
                        v-else
                        @click="startEdit(comment)"
                        class="flex-1 bg-blue-400 text-black border-2 border-black font-black py-2 px-3 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-sm"
                    >
                        編輯
                    </button>
                    <button
                        v-if="editingId === comment.id"
                        @click="cancelEdit"
                        class="flex-1 bg-gray-400 text-black border-2 border-black font-black py-2 px-3 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-sm"
                    >
                        取消
                    </button>
                    <button 
                        @click="deleteComment(comment.id)"
                        class="flex-1 bg-red-300 text-black border-2 border-black font-black py-2 px-3 uppercase tracking-wide shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] active:shadow-none transition-all text-sm"
                    >
                        刪除
                    </button>
                </div>
            </div>
        </div>

        <!-- 空狀態 -->
        <div v-if="userStore.allComments.length === 0" class="border-4 border-black bg-yellow-200 shadow-[4px_4px_0px_0px_black] p-8 text-center mt-8">
            <p class="font-black text-2xl mb-2">還沒有任何評論</p>
            <p class="text-gray-700 font-semibold">快去食譜頁面分享你的想法吧！</p>
        </div>
    </div>
</template>