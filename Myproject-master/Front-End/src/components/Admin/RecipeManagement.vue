<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

const adminStore = useAdminStore()

const searchKeyword = ref('')

onMounted(async () => {
  await adminStore.fetchRecipes()
})

const filteredRecipes = computed(() => {
  if (!searchKeyword.value) return adminStore.recipes
  
  const keyword = searchKeyword.value.toLowerCase()
  return adminStore.recipes.filter(recipe =>
    recipe.title.toLowerCase().includes(keyword)
  )
})

async function deleteRecipe(recipe) {
  if (!confirm(`確定要刪除食譜「${recipe.title}」嗎?`)) return

  const result = await adminStore.deleteRecipe(recipe.id)
  
  if (result.success) {
    toast.success(result.message, { autoClose: 1000 })
  } else {
    toast.error(result.error || '刪除失敗', { autoClose: 1000 })
  }
}
</script>

<template>
  <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-6">
    <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
      食譜管理
    </h2>

    <div class="mb-6">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="搜尋食譜名稱..."
        class="w-full max-w-md border-2 border-black px-4 py-3 font-bold bg-yellow-50 focus:bg-yellow-100 focus:shadow-[4px_4px_0px_0px_black] outline-none"
      />
    </div>

    <div v-if="adminStore.loading" class="text-center py-12">
      <p class="font-bold text-xl">LOADING...</p>
    </div>

    <div v-else-if="filteredRecipes.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div
        v-for="recipe in filteredRecipes"
        :key="recipe.id"
        class="border-2 border-black bg-white p-0 shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] transition-all"
      >
        <div class="relative h-48 border-b-2 border-black overflow-hidden">
          <img
            :src="recipe.imageUrl"
            :alt="recipe.title"
            class="w-full h-full object-cover"
          />
          <div class="absolute top-2 right-2 bg-black text-white border-2 border-white px-2 py-1 text-xs font-bold">
            {{ recipe.cookingTime }} MIN
          </div>
        </div>

        <div class="p-4">
          <h3 class="font-black text-lg mb-2">{{ recipe.title }}</h3>
          <p class="text-sm text-gray-600 mb-3 line-clamp-2">
            {{ recipe.description }}
          </p>

          <button
            @click="deleteRecipe(recipe)"
            :disabled="adminStore.loading"
            class="w-full bg-red-200 border-2 border-black py-2 font-bold hover:bg-red-300 hover:shadow-[2px_2px_0px_0px_black] transition-all disabled:opacity-50"
          >
            DELETE
          </button>
        </div>
      </div>
    </div>

    <div
      v-else
      class="border-4 border-black bg-yellow-200 p-8 text-center"
    >
      <p class="font-black text-xl">NO RECIPES FOUND</p>
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