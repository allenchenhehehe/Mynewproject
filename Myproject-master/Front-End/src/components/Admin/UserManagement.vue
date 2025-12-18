<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/adminStore'

const adminStore = useAdminStore()

const searchKeyword = ref('')
const statusFilter = ref('')
const roleFilter = ref('')

// 確認刪除的狀態
const confirmingDeleteId = ref(null)
const confirmingStatusChange = ref(null)

onMounted(async () => {
  await adminStore.fetchUsers()
})

const filteredUsers = computed(() => {
  let result = adminStore.users

  // 搜尋過濾
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(user =>
      (user.userName && user.userName.toLowerCase().includes(keyword)) ||
      (user.email && user.email.toLowerCase().includes(keyword))
    )
  }

  // 狀態過濾
  if (statusFilter.value) {
    result = result.filter(user => user.status === statusFilter.value)
  }

  // 角色過濾
  if (roleFilter.value) {
    result = result.filter(user => user.role === roleFilter.value)
  }

  return result
})

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function getStatusLabel(status) {
  const statusMap = {
    'active': '正常',
    'inactive': '停用',
    'banned': '封鎖'
  }
  return statusMap[status] || status
}

function getStatusClass(status) {
  const statusClassMap = {
    'active': 'bg-green-200 border-green-600',
    'inactive': 'bg-yellow-200 border-yellow-600',
    'banned': 'bg-red-200 border-red-600'
  }
  return statusClassMap[status] || 'bg-gray-200 border-gray-600'
}

function getRoleLabel(role) {
  return role === 'admin' ? '管理員' : '使用者'
}

// 開始確認狀態變更
function startConfirmStatusChange(userId, newStatus) {
  confirmingStatusChange.value = { userId, newStatus }
}

// 取消狀態變更
function cancelStatusChange() {
  confirmingStatusChange.value = null
}

// 確認狀態變更
async function confirmStatusChange() {
  if (!confirmingStatusChange.value) return
  
  const { userId, newStatus } = confirmingStatusChange.value
  
  const result = await adminStore.updateUserStatus(userId, newStatus)
  
  if (result.success) {
    alert(result.message)
  } else {
    alert('更新失敗: ' + result.error)
  }
  
  confirmingStatusChange.value = null
}

// 開始確認刪除
function startConfirmDelete(userId) {
  confirmingDeleteId.value = userId
}

// 取消刪除
function cancelDelete() {
  confirmingDeleteId.value = null
}

// 確認刪除
async function confirmDelete() {
  if (!confirmingDeleteId.value) return
  
  const result = await adminStore.deleteUser(confirmingDeleteId.value)
  
  if (result.success) {
    alert(result.message)
  } else {
    alert('刪除失敗: ' + result.error)
  }
  
  confirmingDeleteId.value = null
}
</script>

<template>
  <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-6">
    <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
      使用者管理 USER MANAGEMENT
    </h2>

    <!-- 搜尋和篩選 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="搜尋使用者名稱或 Email..."
        class="w-full border-2 border-black px-4 py-3 font-bold outline-none focus:shadow-[4px_4px_0px_0px_black]"
      />
      
      <select
        v-model="statusFilter"
        class="w-full border-2 border-black px-4 py-3 font-bold outline-none focus:shadow-[4px_4px_0px_0px_black] bg-white"
      >
        <option value="">全部狀態</option>
        <option value="active">正常</option>
        <option value="inactive">停用</option>
        <option value="banned">封鎖</option>
      </select>
      
      <select
        v-model="roleFilter"
        class="w-full border-2 border-black px-4 py-3 font-bold outline-none focus:shadow-[4px_4px_0px_0px_black] bg-white"
      >
        <option value="">全部角色</option>
        <option value="user">使用者</option>
        <option value="admin">管理員</option>
      </select>
    </div>

    <!-- 統計資訊 -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      <div class="border-2 border-black p-4 bg-blue-100">
        <div class="text-3xl font-black">{{ adminStore.users.length }}</div>
        <div class="text-sm font-bold">總使用者</div>
      </div>
      <div class="border-2 border-black p-4 bg-green-100">
        <div class="text-3xl font-black">{{ adminStore.users.filter(u => u.status === 'active').length }}</div>
        <div class="text-sm font-bold">正常</div>
      </div>
      <div class="border-2 border-black p-4 bg-yellow-100">
        <div class="text-3xl font-black">{{ adminStore.users.filter(u => u.status === 'inactive').length }}</div>
        <div class="text-sm font-bold">停用</div>
      </div>
      <div class="border-2 border-black p-4 bg-red-100">
        <div class="text-3xl font-black">{{ adminStore.users.filter(u => u.status === 'banned').length }}</div>
        <div class="text-sm font-bold">封鎖</div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="adminStore.loading" class="text-center py-12">
      <p class="font-bold text-xl">LOADING...</p>
    </div>

    <!-- 使用者列表 -->
    <div v-else-if="filteredUsers.length > 0" class="space-y-4">
      <div
        v-for="user in filteredUsers"
        :key="user.id"
        class="border-2 border-black p-4"
      >
        <div class="flex justify-between items-start mb-3">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-2 flex-wrap">
              <h3 class="text-xl font-black">{{ user.userName }}</h3>
              <span 
                class="text-xs font-bold px-2 py-1 border-2 border-black"
                :class="user.role === 'admin' ? 'bg-purple-200' : 'bg-gray-100'"
              >
                {{ getRoleLabel(user.role) }}
              </span>
              <span 
                class="text-xs font-bold px-2 py-1 border-2"
                :class="getStatusClass(user.status)"
              >
                {{ getStatusLabel(user.status) }}
              </span>
            </div>
            <p class="text-sm text-gray-600 mb-1">
              <span class="font-bold">Email:</span> {{ user.email }}
            </p>
            <p class="text-xs text-gray-500">
              <span class="font-bold">ID:</span> {{ user.id }} | 
              <span class="font-bold">註冊:</span> {{ formatDate(user.createdAt) }}
            </p>
          </div>
        </div>

        <!-- 操作按鈕 -->
        <div class="flex gap-2 mt-4 pt-4 border-t-2 border-black flex-wrap">
          <template v-if="user.role !== 'admin' && user.id !== adminStore.adminUser?.id">
            <!-- 狀態變更按鈕 -->
            <button
              v-if="user.status === 'active'"
              @click="startConfirmStatusChange(user.id, 'inactive')"
              class="px-4 py-2 bg-yellow-200 border-2 border-black font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all"
            >
              停用
            </button>
            <button
              v-if="user.status === 'active'"
              @click="startConfirmStatusChange(user.id, 'banned')"
              class="px-4 py-2 bg-red-200 border-2 border-black font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all"
            >
              封鎖
            </button>
            <button
              v-if="user.status === 'inactive'"
              @click="startConfirmStatusChange(user.id, 'active')"
              class="px-4 py-2 bg-green-200 border-2 border-black font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all"
            >
              啟用
            </button>
            <button
              v-if="user.status === 'banned'"
              @click="startConfirmStatusChange(user.id, 'active')"
              class="px-4 py-2 bg-green-200 border-2 border-black font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all"
            >
              解除封鎖
            </button>
            
            <!-- 刪除按鈕 -->
            <button
              @click="startConfirmDelete(user.id)"
              class="px-4 py-2 bg-red-300 border-2 border-black font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all md:ml-auto"
            >
              刪除
            </button>
          </template>
          <div v-else class="text-sm text-gray-500 italic">
            {{ user.role === 'admin' ? '管理員帳號無法操作' : '無法操作自己的帳號' }}
          </div>
        </div>
      </div>
    </div>

    <!-- 無資料 -->
    <div v-else class="text-center py-12">
      <p class="font-bold text-gray-500">NO DATA</p>
    </div>

    <!-- 確認狀態變更 Modal -->
    <div
        v-if="confirmingStatusChange"
        class="fixed inset-0 bg-black/20 backdrop-blur-sm flex items-center justify-center z-50 px-4"
        @click="cancelStatusChange"
    >
      <div
        class="bg-white border-4 border-black shadow-[12px_12px_0px_0px_black] p-8 max-w-md w-full"
        @click.stop
      >
        <h3 class="text-xl font-black mb-4 uppercase">確認變更狀態</h3>
        <p class="mb-6">
          確定要將使用者狀態變更為「<strong>{{ getStatusLabel(confirmingStatusChange.newStatus) }}</strong>」嗎？
        </p>
        <div class="flex gap-4">
          <button
            @click="confirmStatusChange"
            class="flex-1 bg-red-500 text-white border-2 border-black px-6 py-3 font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all"
          >
            確認
          </button>
          <button
            @click="cancelStatusChange"
            class="flex-1 bg-gray-200 border-2 border-black px-6 py-3 font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all"
          >
            取消
          </button>
        </div>
      </div>
    </div>

    <!-- 確認刪除 Modal -->
    <div
        v-if="confirmingDeleteId"
        class="fixed inset-0 bg-black/20 backdrop-blur-sm flex items-center justify-center z-50 px-4"
         @click="cancelDelete"
    >
      <div
        class="bg-white border-4 border-black shadow-[12px_12px_0px_0px_black] p-8 max-w-md w-full"
        @click.stop
      >
        <h3 class="text-xl font-black mb-4 uppercase text-red-600">危險操作！</h3>
        <p class="mb-6">
          確定要<strong class="text-red-600">刪除</strong>此使用者嗎？<br>
          <span class="text-sm text-gray-600">此操作無法復原！</span>
        </p>
        <div class="flex gap-4">
          <button
            @click="confirmDelete"
            class="flex-1 bg-red-500 text-white border-2 border-black px-6 py-3 font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all"
          >
            確認刪除
          </button>
          <button
            @click="cancelDelete"
            class="flex-1 bg-gray-200 border-2 border-black px-6 py-3 font-bold hover:shadow-[4px_4px_0px_0px_black] transition-all"
          >
            取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>