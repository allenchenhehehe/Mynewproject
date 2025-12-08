<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRecipeStore } from '@/stores/recipeStore'

const recipeStore = useRecipeStore()

const searchKeyword = ref('')
const loading = ref(true)

// 模擬載入
onMounted(async () => {
  loading.value = true
  // TODO: 呼叫 API 載入所有食譜 (包含已刪除)
  await recipeStore.fetchRecipes()
  loading.value = false
})

const filteredRecipes = computed(() => {
  if (!searchKeyword.value) return recipeStore.recipes
  
  const keyword = searchKeyword.value.toLowerCase()
  return recipeStore.recipes.filter(recipe =>
    recipe.title.toLowerCase().includes(keyword)
  )
})

async function deleteRecipe(recipe) {
  if (!confirm(`確定要刪除食譜「${recipe.title}」嗎?`)) return

  try {
    // TODO: 呼叫刪除 API
    // await fetch(`http://localhost:8080/myfridge/api/admin/recipes/${recipe.id}`, {
    //   method: 'DELETE',
    //   credentials: 'include'
    // })
    
    console.log('刪除食譜:', recipe.id)
    alert('刪除成功! (Demo: 實際需要 API)')
    
    // 重新載入
    await recipeStore.fetchRecipes()
  } catch (error) {
    console.error('刪除失敗:', error)
    alert('刪除失敗: ' + error.message)
  }
}
</script>

<template>
  <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-6">
    <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
      食譜管理
    </h2>

    <!-- 搜尋 -->
    <div class="mb-6">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="搜尋食譜名稱..."
        class="w-full max-w-md border-2 border-black px-4 py-3 font-bold bg-yellow-50 focus:bg-yellow-100 focus:shadow-[4px_4px_0px_0px_black] outline-none"
      />
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-12">
      <div class="text-4xl mb-4">⏳</div>
      <p class="font-bold">LOADING...</p>
    </div>

    <!-- 食譜列表 -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div
        v-for="recipe in filteredRecipes"
        :key="recipe.id"
        class="border-2 border-black bg-white p-0 shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] transition-all"
      >
        <!-- 圖片 -->
        <div class="relative h-48 border-b-2 border-black overflow-hidden">
          <img
            :src="recipe.image_url"
            :alt="recipe.title"
            class="w-full h-full object-cover"
          />
          <div class="absolute top-2 right-2 bg-black text-white border-2 border-white px-2 py-1 text-xs font-bold">
            {{ recipe.cooking_time }} MIN
          </div>
        </div>

        <!-- 內容 -->
        <div class="p-4">
          <h3 class="font-black text-lg mb-2">{{ recipe.title }}</h3>
          <p class="text-sm text-gray-600 mb-3 line-clamp-2">
            {{ recipe.description }}
          </p>

          <!-- 刪除按鈕 -->
          <button
            @click="deleteRecipe(recipe)"
            class="w-full bg-[#fecaca] border-2 border-black py-2 font-bold hover:bg-red-300 hover:shadow-[2px_2px_0px_0px_black] transition-all"
          >
            DELETE
          </button>
        </div>
      </div>
    </div>

    <!-- 空狀態 -->
    <div
      v-if="!loading && filteredRecipes.length === 0"
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