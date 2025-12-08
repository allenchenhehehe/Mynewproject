<!-- <script setup>
import { ref, onMounted } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { useNavigationStore } from '@/stores/navigationStore'

const adminStore = useAdminStore()
const navStore = useNavigationStore()

const activeTab = ref('favorites')  // 預設顯示收藏記錄

// ✅ 重要!進入頁面就檢查登入
onMounted(() => {
  console.log('AdminPanel mounted')
  console.log('isAuthenticated:', adminStore.isAuthenticated)
  console.log('adminUser:', adminStore.adminUser)
  
  if (!adminStore.isAuthenticated) {
    console.log('未登入,跳轉到 AdminLogin')
    navStore.goToAdminLogin()  // 未登入 → 跳到登入頁
  }
})

function handleLogout() {
  if (confirm('確定要登出嗎?')) {
    adminStore.logout()
    navStore.goHome()  // 登出 → 回首頁
  }
}
</script> -->

<!-- <template>
  
  <div v-if="adminStore.isAuthenticated" class="min-h-screen bg-[#fefae0] pb-20">
    
    <div class="bg-white border-b-4 border-black shadow-[0px_4px_0px_0px_black] sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-4">
            <h1 class="text-3xl font-black uppercase tracking-tighter">
              🛠️ ADMIN PANEL
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
          🍳 食譜管理
        </button>
        
        <button
          @click="activeTab = 'comments'"
          :class="activeTab === 'comments' ? 'bg-black text-white' : 'bg-white text-black'"
          class="border-2 border-black px-6 py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
        >
          💬 評論管理
        </button>
        
        <button
          @click="activeTab = 'favorites'"
          :class="activeTab === 'favorites' ? 'bg-black text-white' : 'bg-white text-black'"
          class="border-2 border-black px-6 py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
        >
          ⭐ 收藏記錄
        </button>
      </div>

      
      <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-8">
        
        <div v-if="activeTab === 'recipes'" class="space-y-4">
          <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
            🍳 食譜管理
          </h2>
          <p class="text-gray-600 font-bold">功能開發中...</p>
        </div>
        
        
        <div v-if="activeTab === 'comments'" class="space-y-4">
          <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
            💬 評論管理
          </h2>
          <p class="text-gray-600 font-bold">功能開發中...</p>
        </div>
        
        
        <div v-if="activeTab === 'favorites'" class="space-y-4">
          <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
            ⭐ 收藏記錄 (VIEW ONLY)
          </h2>
          
          <div class="bg-yellow-100 border-2 border-black px-4 py-3 mb-6 font-bold text-sm">
            ℹ️ 此頁面僅供查看,不提供操作功能
          </div>
          
          <div class="border-2 border-black p-8 text-center">
            <p class="font-bold text-gray-500">暫無收藏記錄</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  
  <div v-else class="min-h-screen flex items-center justify-center">
    <div class="text-2xl font-black">LOADING...</div>
  </div>
</template> -->
<script setup>
import { ref } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { useNavigationStore } from '@/stores/navigationStore'
import { useAuthStore, STATUS_LOGIN } from '@/stores/authStore'

const adminStore = useAdminStore()
const navStore = useNavigationStore()
const authStore = useAuthStore()

const activeTab = ref('favorites')

function handleLogout() {
  if (confirm('確定要登出嗎?')) {
    adminStore.logout()
    authStore.setAuthStatus(STATUS_LOGIN)  // ✅ 回到使用者登入狀態
    navStore.goHome()
  }
}
</script>

<template>
  <div class="min-h-screen bg-[#fefae0] pb-20">
    <!-- 頂部導覽 -->
    <div class="bg-white border-b-4 border-black shadow-[0px_4px_0px_0px_black] sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-4">
            <h1 class="text-3xl font-black uppercase tracking-tighter">
              🛠️ ADMIN PANEL
            </h1>
            <div class="bg-orange-300 border-2 border-black px-3 py-1 text-xs font-bold">
              {{ adminStore.adminUser?.username || 'DEMO' }}
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

    <!-- 標籤導覽 -->
    <div class="max-w-7xl mx-auto px-6 mt-8">
      <div class="flex gap-2 mb-8 flex-wrap">
        <button
          @click="activeTab = 'recipes'"
          :class="activeTab === 'recipes' ? 'bg-black text-white' : 'bg-white text-black'"
          class="border-2 border-black px-6 py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
        >
          🍳 食譜管理
        </button>
        
        <button
          @click="activeTab = 'comments'"
          :class="activeTab === 'comments' ? 'bg-black text-white' : 'bg-white text-black'"
          class="border-2 border-black px-6 py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
        >
          💬 評論管理
        </button>
        
        <button
          @click="activeTab = 'favorites'"
          :class="activeTab === 'favorites' ? 'bg-black text-white' : 'bg-white text-black'"
          class="border-2 border-black px-6 py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all"
        >
          ⭐ 收藏記錄
        </button>
      </div>

      <!-- 內容區域 -->
      <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-8">
        <!-- 食譜管理 -->
        <div v-if="activeTab === 'recipes'" class="space-y-4">
          <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
            🍳 食譜管理
          </h2>
          <p class="text-gray-600 font-bold">功能開發中...</p>
        </div>
        
        <!-- 評論管理 -->
        <div v-if="activeTab === 'comments'" class="space-y-4">
          <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
            💬 評論管理
          </h2>
          <p class="text-gray-600 font-bold">功能開發中...</p>
        </div>
        
        <!-- 收藏記錄 -->
        <div v-if="activeTab === 'favorites'" class="space-y-4">
          <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
            ⭐ 收藏記錄 (VIEW ONLY)
          </h2>
          
          <div class="bg-yellow-100 border-2 border-black px-4 py-3 mb-6 font-bold text-sm">
            ℹ️ 此頁面僅供查看,不提供操作功能
          </div>
          
          <div class="border-2 border-black p-8 text-center">
            <p class="font-bold text-gray-500">暫無收藏記錄</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>