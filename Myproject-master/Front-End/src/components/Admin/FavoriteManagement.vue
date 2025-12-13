<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/adminStore'

const adminStore = useAdminStore()

const searchKeyword = ref('')
const statusFilter = ref('')

onMounted(async () => {
  await adminStore.fetchFavorites()
})

const filteredFavorites = computed(() => {
  let result = adminStore.favorites

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(fav =>
      (fav.userName && fav.userName.toLowerCase().includes(keyword)) ||
      (fav.recipeTitle && fav.recipeTitle.toLowerCase().includes(keyword))
    )
  }

  return result
})

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
      收藏記錄 (VIEW ONLY)
    </h2>

    <div class="bg-yellow-100 border-2 border-black px-4 py-3 mb-6 font-bold text-sm">
      此頁面僅供查看，不提供操作功能
    </div>

    <div class="mb-6">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="搜尋使用者或食譜..."
        class="w-full border-2 border-black px-4 py-3 font-bold outline-none"
      />
    </div>

    <div v-if="adminStore.loading" class="text-center py-12">
      <p class="font-bold text-xl">LOADING...</p>
    </div>

    <div v-else-if="filteredFavorites.length > 0" class="space-y-2">
      <div
        v-for="fav in filteredFavorites"
        :key="fav.id"
        class="border-2 border-black p-3 flex justify-between items-center"
      >
        <div>
          <span class="font-bold">{{ fav.userName }}</span>
          <span class="mx-2">→</span>
          <span>{{ fav.recipeTitle }}</span>
        </div>
        <span class="text-xs text-gray-500">{{ formatDate(fav.savedAt) }}</span>
      </div>
    </div>

    <div v-else class="text-center py-12">
      <p class="font-bold text-gray-500">NO DATA</p>
    </div>
  </div>
</template>
