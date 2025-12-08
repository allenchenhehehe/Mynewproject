<script setup>
import { ref, computed, onMounted } from 'vue'

const loading = ref(true)
const comments = ref([])
const selectedRecipe = ref('')

// 模擬資料
onMounted(async () => {
  loading.value = true
  
  // TODO: 呼叫 API
  // const response = await fetch('http://localhost:8080/myfridge/api/comments', {
  //   credentials: 'include'
  // })
  // comments.value = await response.json()
  
  // 模擬資料
  comments.value = [
    {
      id: 1,
      recipe_name: '番茄炒蛋',
      user_name: '使用者A',
      content: '很好吃!',
      rating: 5,
      created_at: '2025-12-01',
      is_deleted: false
    }
  ]
  
  loading.value = false
})

const uniqueRecipes = computed(() => {
  return [...new Set(comments.value.map(c => c.recipe_name))]
})

const filteredComments = computed(() => {
  if (!selectedRecipe.value) return comments.value
  return comments.value.filter(c => c.recipe_name === selectedRecipe.value)
})

async function deleteComment(comment) {
  if (!confirm('確定要刪除這則評論嗎?')) return

  try {
    // TODO: 呼叫 API
    console.log('刪除評論:', comment.id)
    alert('刪除成功! (Demo)')
  } catch (error) {
    alert('刪除失敗: ' + error.message)
  }
}
</script>

<template>
  <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-6">
    <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
      評論管理
    </h2>

    <!-- 篩選 -->
    <div class="mb-6">
      <select
        v-model="selectedRecipe"
        class="border-2 border-black px-4 py-3 font-bold bg-gray-50 focus:shadow-[4px_4px_0px_0px_black] outline-none"
      >
        <option value="">全部食譜</option>
        <option v-for="recipe in uniqueRecipes" :key="recipe" :value="recipe">
          {{ recipe }}
        </option>
      </select>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-12">
      <p class="font-bold">LOADING...</p>
    </div>

    <!-- 評論列表 -->
    <div v-else class="space-y-4">
      <div
        v-for="comment in filteredComments"
        :key="comment.id"
        class="border-2 border-black p-4 bg-gray-50"
      >
        <div class="flex justify-between items-start mb-2">
          <div>
            <span class="font-black">{{ comment.recipe_name }}</span>
            <span class="text-sm text-gray-600 ml-2">by {{ comment.user_name }}</span>
          </div>
          <div class="text-sm">
            {{ '⭐'.repeat(comment.rating) }}
          </div>
        </div>
        
        <p class="mb-3 text-sm">{{ comment.content }}</p>
        
        <div class="flex justify-between items-center pt-3 border-t-2 border-dashed">
          <span class="text-xs text-gray-500">{{ comment.created_at }}</span>
          <button
            @click="deleteComment(comment)"
            class="bg-[#fecaca] border-2 border-black px-4 py-1 text-sm font-bold hover:shadow-[2px_2px_0px_0px_black]"
          >
            DELETE
          </button>
        </div>
      </div>
    </div>

    <!-- 空狀態 -->
    <div v-if="!loading && filteredComments.length === 0" class="text-center py-12">
      <p class="font-bold text-gray-500">NO COMMENTS</p>
    </div>
  </div>
</template>