<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/adminStore'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

const adminStore = useAdminStore()

const selectedCuisine = ref('')
const cuisines = [
  "台灣小吃",
  "台式熱炒",
  "日式料理",
  "韓式料理",
  "泰式料理",
  "義大利麵",
  "中式家常菜",
  "粵菜",
  "川菜",
  "湘菜",
  "東南亞料理",
  "法式料理",
  "美式料理",
  "墨西哥料理",
  "印度料理",
  "越南料理",
  "地中海料理",
  "素食料理"
]

const activeTab = ref('list')
const searchKeyword = ref('')

const newRecipe = ref({
  title: '',
  description: '',
  imageUrl: '',
  cookingTime: null,
  difficulty: 1
})

const generatedRecipe = ref(null)
const isGenerating = ref(false)

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

function getDifficultyLabel(difficulty) {
  const labels = { 1: '入門', 2: '基礎', 3: '進階', 4: '功夫', 5: '專業' }
  return labels[difficulty] || '未知'
}

async function deleteRecipe(recipe) {
  if (!confirm(`確定要刪除食譜「${recipe.title}」嗎?`)) return

  const result = await adminStore.deleteRecipe(recipe.id)
  
  if (result.success) {
    toast.success(result.message, { autoClose: 1000 })
  } else {
    toast.error(result.error || '刪除失敗', { autoClose: 1000 })
  }
}

async function generateRandomRecipe() {
  isGenerating.value = true
  
  try {
    const result = await adminStore.generateRandomRecipe(selectedCuisine.value)
    if (result.success) {
      generatedRecipe.value = result.data
      toast.success('食譜生成成功！', { autoClose: 1000 })
    } else {
      toast.error(result.error || '生成失敗', { autoClose: 2000 })
    }
  } catch (error) {
    toast.error('生成失敗', { autoClose: 2000 })
  } finally {
    isGenerating.value = false
  }
}

//修改：直接使用 AI 生成的食譜（包含 step 和 ingredients）
async function useGeneratedRecipe() {
  if (!generatedRecipe.value) return
  
  try {
    //直接送完整的 generatedRecipe（包含 step 和 ingredients）
    const result = await adminStore.createRecipe(generatedRecipe.value)
    
    if (result.success) {
      toast.success('食譜新增成功！', { autoClose: 1000 })
      
      // 清空生成的食譜
      generatedRecipe.value = null
      
      // 切換回列表
      activeTab.value = 'list'
      
      // 重新載入
      await adminStore.fetchRecipes()
    } else {
      toast.error(result.error || '新增失敗', { autoClose: 2000 })
    }
  } catch (error) {
    console.error('使用生成食譜失敗:', error)
    toast.error('新增失敗', { autoClose: 2000 })
  }
}

//保留：手動新增食譜（不包含 step 和 ingredients）
async function createRecipe() {
  if (!newRecipe.value.title || !newRecipe.value.title.trim()) {
    toast.error('請填寫食譜名稱', { autoClose: 2000 })
    return
  }
  
  if (!newRecipe.value.description || !newRecipe.value.description.trim()) {
    toast.error('請填寫食譜描述', { autoClose: 2000 })
    return
  }

  try {
    const result = await adminStore.createRecipe(newRecipe.value)
    if (result.success) {
      toast.success('食譜新增成功！', { autoClose: 1000 })
      
      newRecipe.value = {
        title: '',
        description: '',
        imageUrl: '',
        cookingTime: null,
        difficulty: 1
      }
      
      activeTab.value = 'list'
      await adminStore.fetchRecipes()
    } else {
      toast.error(result.error || '新增失敗', { autoClose: 2000 })
    }
  } catch (error) {
    toast.error('新增失敗', { autoClose: 2000 })
  }
}
</script>

<template>
  <div class="bg-white border-4 border-black shadow-[8px_8px_0px_0px_black] p-6">
    <h2 class="text-2xl font-black uppercase mb-6 pb-4 border-b-2 border-black">
      食譜管理 RECIPE MANAGEMENT
    </h2>

    <!-- ==================== Tab 切換 ==================== -->
    <div class="flex gap-2 mb-6 flex-wrap">
      <button
        @click="activeTab = 'list'"
        :class="activeTab === 'list' ? 'bg-black text-white' : 'bg-white text-black'"
        class="border-2 border-black px-6 py-3 font-bold shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] transition-all"
      >
        食譜列表
      </button>
      <button
        @click="activeTab = 'create'"
        :class="activeTab === 'create' ? 'bg-black text-white' : 'bg-white text-black'"
        class="border-2 border-black px-6 py-3 font-bold shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] transition-all"
      >
        新增食譜
      </button>
    </div>

    <!-- ==================== 食譜列表 ==================== -->
    <div v-if="activeTab === 'list'">
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
      <div v-if="adminStore.loading" class="text-center py-12">
        <p class="font-bold text-xl">LOADING...</p>
      </div>

      <!-- 食譜列表 -->
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

      <!-- 無資料 -->
      <div
        v-else
        class="border-4 border-black bg-yellow-200 p-8 text-center"
      >
        <p class="font-black text-xl">NO RECIPES FOUND</p>
      </div>
    </div>

    <!-- ==================== 新增食譜 ==================== -->
    <div v-else-if="activeTab === 'create'">
      
      <!-- 隨機生成區塊 -->
      <div class="mb-6 p-6 bg-purple-100 border-4 border-black">
        <h3 class="font-black text-xl mb-2 uppercase">AI 隨機生成</h3>
        <!-- <p class="text-sm mb-4">使用 Spoonacular API 隨機生成英文食譜</p> -->

        <!-- 新增：菜系選擇 -->
    <div class="mb-4">
      <label class="block font-bold mb-2">選擇菜系（可選）</label>
      <select 
        v-model="selectedCuisine"
        class="w-full border-2 border-black px-4 py-3 font-bold bg-white"
      >
        <option value="">隨機菜系</option>
        <option v-for="c in cuisines" :key="c" :value="c">{{ c }}</option>
      </select>
    </div>
        
        <button
          @click="generateRandomRecipe"
          :disabled="isGenerating"
          class="bg-purple-400 border-2 border-black px-6 py-3 font-bold shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ isGenerating ? 'GENERATING...' : 'GENERATE RANDOM RECIPE' }}
        </button>

        <!-- 生成的食譜預覽 -->
        <div v-if="generatedRecipe" class="mt-6 p-4 bg-white border-2 border-black">
          <div class="relative h-48 mb-4 border-2 border-black overflow-hidden">
            <img
              v-if="generatedRecipe.imageUrl"
              :src="generatedRecipe.imageUrl"
              class="w-full h-full object-cover"
            />
          </div>
          <h4 class="text-xl font-black mb-2">{{ generatedRecipe.title }}</h4>
          <p class="text-sm mb-2 line-clamp-3">{{ generatedRecipe.description }}</p>
          <div class="flex gap-4 text-xs mb-4">
            <span class="font-bold">{{ generatedRecipe.cookingTime }} 分鐘</span>
            <span class="font-bold">{{ getDifficultyLabel(generatedRecipe.difficulty) }}</span>
          </div>
          <button
            @click="useGeneratedRecipe"
            class="w-full bg-green-400 border-2 border-black py-2 font-bold hover:bg-green-500 hover:shadow-[2px_2px_0px_0px_black] transition-all"
          >
            USE THIS RECIPE
          </button>
        </div>
      </div>

      <!-- 手動新增表單 -->
      <div class="space-y-4">
        <div>
          <label class="block font-black mb-2 uppercase">食譜名稱 *</label>
          <input
            v-model="newRecipe.title"
            type="text"
            placeholder="例如：番茄炒蛋"
            class="w-full border-2 border-black px-4 py-3 font-bold bg-yellow-50 focus:bg-yellow-100 focus:shadow-[4px_4px_0px_0px_black] outline-none"
          />
        </div>

        <div>
          <label class="block font-black mb-2 uppercase">食譜描述 *</label>
          <textarea
            v-model="newRecipe.description"
            rows="4"
            placeholder="描述這道菜..."
            class="w-full border-2 border-black px-4 py-3 font-bold bg-yellow-50 focus:bg-yellow-100 focus:shadow-[4px_4px_0px_0px_black] outline-none resize-none"
          ></textarea>
        </div>

        <div>
          <label class="block font-black mb-2 uppercase">圖片網址</label>
          <input
            v-model="newRecipe.imageUrl"
            type="text"
            placeholder="https://example.com/image.jpg"
            class="w-full border-2 border-black px-4 py-3 font-bold bg-yellow-50 focus:bg-yellow-100 focus:shadow-[4px_4px_0px_0px_black] outline-none"
          />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block font-black mb-2 uppercase">烹飪時間（分鐘）</label>
            <input
              v-model.number="newRecipe.cookingTime"
              type="number"
              min="1"
              placeholder="30"
              class="w-full border-2 border-black px-4 py-3 font-bold bg-yellow-50 focus:bg-yellow-100 focus:shadow-[4px_4px_0px_0px_black] outline-none"
            />
          </div>

          <div>
            <label class="block font-black mb-2 uppercase">難度</label>
            <select
              v-model.number="newRecipe.difficulty"
              class="w-full border-2 border-black px-4 py-3 font-bold bg-yellow-50 focus:bg-yellow-100 focus:shadow-[4px_4px_0px_0px_black] outline-none"
            >
              <option :value="1">入門</option>
              <option :value="2">基礎</option>
              <option :value="3">進階</option>
              <option :value="4">功夫</option>
              <option :value="5">專業</option>
            </select>
          </div>
        </div>

        <div class="flex gap-4 pt-4">
          <button
            @click="createRecipe"
            :disabled="adminStore.loading"
            class="flex-1 bg-green-400 border-2 border-black px-6 py-3 font-bold shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
          >
            ✅ ADD RECIPE
          </button>
          <button
            @click="activeTab = 'list'"
            class="flex-1 bg-gray-200 border-2 border-black px-6 py-3 font-bold shadow-[2px_2px_0px_0px_black] hover:shadow-[4px_4px_0px_0px_black] transition-all"
          >
            CANCEL
          </button>
        </div>
      </div>
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

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>