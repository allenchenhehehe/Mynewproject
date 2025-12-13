<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

const adminStore = useAdminStore()

const selectedRecipe = ref('')

onMounted(async () => {
  await adminStore.fetchComments()
})

const uniqueRecipes = computed(() => {
  const recipes = adminStore.comments.map(c => c.recipeTitle)
  return [...new Set(recipes)].filter(Boolean)
})

const filteredComments = computed(() => {
  if (!selectedRecipe.value) return adminStore.comments
  return adminStore.comments.filter(c => c.recipeTitle === selectedRecipe.value)
})

async function deleteComment(comment) {
  if (!confirm('確定要刪除這則評論嗎?')) return

  const result = await adminStore.deleteComment(comment.id)
  
  if (result.success) {
    toast.success(result.message, { autoClose: 1000 })
  } else {
    toast.error(result.error || '刪除失敗', { autoClose: 1000 })
  }
}

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>

<template>
  <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-6">
    <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
      評論管理
    </h2>

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

    <div v-if="adminStore.loading" class="text-center py-12">
      <p class="font-bold text-xl">LOADING...</p>
    </div>

    <div v-else-if="filteredComments.length > 0" class="space-y-4">
      <div
        v-for="comment in filteredComments"
        :key="comment.id"
        class="border-2 border-black p-4 bg-gray-50"
      >
        <div class="flex justify-between items-start mb-2">
          <div>
            <span class="font-black">{{ comment.recipeTitle }}</span>
            <span class="text-sm text-gray-600 ml-2">by {{ comment.userName }}</span>
          </div>
          <div class="text-sm">
            <span v-for="n in comment.rating" :key="n">★</span>
          </div>
        </div>
        
        <p class="mb-3 text-sm">{{ comment.text }}</p>
        
        <div class="flex justify-between items-center pt-3 border-t-2 border-dashed">
          <span class="text-xs text-gray-500">{{ formatDate(comment.createdAt) }}</span>
          <button
            @click="deleteComment(comment)"
            :disabled="adminStore.loading"
            class="bg-red-200 border-2 border-black px-4 py-1 text-sm font-bold hover:shadow-[2px_2px_0px_0px_black] disabled:opacity-50"
          >
            DELETE
          </button>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-12">
      <p class="font-bold text-gray-500">NO COMMENTS</p>
    </div>
  </div>
</template>