<script setup>
import { ref, onMounted } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { useAuthStore, STATUS_LOGIN } from '@/stores/authStore'
import RecipeManagement from '@/components/admin/RecipeManagement.vue'
import CommentManagement from '@/components/admin/CommentManagement.vue'
import FavoriteManagement from '@/components/admin/FavoriteManagement.vue'

const adminStore = useAdminStore()
const authStore = useAuthStore()

const activeTab = ref('recipes')

// onMounted(async () => {
//   // 檢查登入狀態
//   const isAuth = await adminStore.checkAuth()
//   if (!isAuth) {
//     authStore.setAuthStatus(STATUS_LOGIN)
//   }
// })

onMounted(() => {
  if (!adminStore.isAuthenticated) {
    // 如果沒有登入，跳回登入頁
    authStore.setAuthStatus(STATUS_ADMIN)
  }
})

function handleLogout() {
  if (confirm('確定要登出嗎?')) {
    adminStore.logout()
    authStore.setAuthStatus(STATUS_LOGIN)
  }
}
</script>

<template>
  <div class="min-h-screen bg-[#fefae0] pb-20">
    <div class="bg-white border-b-4 border-black shadow-[0px_4px_0px_0px_black] sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-4">
            <h1 class="text-3xl font-black uppercase tracking-tighter">
              ADMIN PANEL
            </h1>
            <div class="bg-orange-300 border-2 border-black px-3 py-1 text-xs font-bold">
              {{ adminStore.adminUser?.username || 'ADMIN' }}
            </div>
          </div>
          
          <button
            @click="handleLogout"
            class="bg-red-400 border-2 border-black px-4 py-2 font-bold shadow-[2px_2px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[1px_1px_0px_0px_black] transition-all"
          >
            LOGOUT
          </button>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-6 mt-8">
      <div class="flex gap-2 mb-8 flex-wrap">
        <button
          @click="activeTab = 'recipes'"
          :class="activeTab === 'recipes' ? 'bg-black text-white' : 'bg-white text-black'"
          class="border-2 border-black px-6 py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
        >
          食譜管理
        </button>
        
        <button
          @click="activeTab = 'comments'"
          :class="activeTab === 'comments' ? 'bg-black text-white' : 'bg-white text-black'"
          class="border-2 border-black px-6 py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
        >
          評論管理
        </button>
        
        <button
          @click="activeTab = 'favorites'"
          :class="activeTab === 'favorites' ? 'bg-black text-white' : 'bg-white text-black'"
          class="border-2 border-black px-6 py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
        >
          收藏記錄
        </button>
      </div>

      <RecipeManagement v-if="activeTab === 'recipes'" />
      <CommentManagement v-if="activeTab === 'comments'" />
      <FavoriteManagement v-if="activeTab === 'favorites'" />
    </div>
  </div>
</template>