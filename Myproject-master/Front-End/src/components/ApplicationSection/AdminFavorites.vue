<script setup>
import { ref, computed, onMounted } from 'vue'

const loading = ref(true)
const favorites = ref([])
const searchKeyword = ref('')
const statusFilter = ref('')

onMounted(async () => {
  loading.value = true
  
  // TODO: API
  favorites.value = []
  
  loading.value = false
})

const filteredFavorites = computed(() => {
  let result = favorites.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(fav =>
      fav.user_name.toLowerCase().includes(keyword) ||
      fav.recipe_name.toLowerCase().includes(keyword)
    )
  }

  if (statusFilter.value === 'deleted') {
    result = result.filter(fav => fav.is_recipe_deleted)
  } else if (statusFilter.value === 'active') {
    result = result.filter(fav => !fav.is_recipe_deleted)
  }

  return result
})
</script>

<template>
  <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-6">
    <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
      收藏記錄 (VIEW ONLY)
    </h2>

    <div class="bg-yellow-100 border-2 border-black px-4 py-3 mb-6 font-bold text-sm">
      ℹ️ 此頁面僅供查看,不提供操作功能
    </div>

    <!-- 篩選 -->
    <div class="mb-6 flex gap-4">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="搜尋使用者或食譜..."
        class="flex-1 border-2 border-black px-4 py-3 font-bold outline-none"
      />
      <select
        v-model="statusFilter"
        class="border-2 border-black px-4 py-3 font-bold outline-none"
      >
        <option value="">全部</option>
        <option value="active">食譜正常</option>
        <option value="deleted">食譜已刪除</option>
      </select>
    </div>

    <!-- 收藏列表 -->
    <div v-if="!loading" class="space-y-2">
      <div
        v-for="fav in filteredFavorites"
        :key="fav.id"
        class="border-2 border-black p-3 flex justify-between items-center"
      >
        <div>
          <span class="font-bold">{{ fav.user_name }}</span>
          <span class="mx-2">→</span>
          <span :class="{ 'text-red-600 line-through': fav.is_recipe_deleted }">
            {{ fav.recipe_name }}
          </span>
        </div>
        <span class="text-xs text-gray-500">{{ fav.created_at }}</span>
      </div>
    </div>

    <div v-if="!loading && filteredFavorites.length === 0" class="text-center py-12">
      <p class="font-bold text-gray-500">NO DATA</p>
    </div>
  </div>
</template>