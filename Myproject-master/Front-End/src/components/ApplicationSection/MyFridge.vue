<script setup>
import { ref, defineProps, defineEmits, watchEffect} from 'vue'

const props = defineProps({ fridgeItems: Array })
const emit = defineEmits(['updateFridge'])

const categories = [
    { key: 'expiring', name: '即將過期' },
    { key: 'vegetable', name: '蔬菜' },
    { key: 'fruit', name: '水果' },
    { key: 'meat', name: '肉類' },
    { key: 'egg', name: '蛋類' },
    { key: 'seasoning', name: '調味料' },
    { key: 'oil', name: '油類' },
    { key: 'seafood', name: '海鮮' },
]

const ingredients = ref(categorizeIngredients(props.fridgeItems))

// ✅ 監聽 props 變化
watchEffect(() => {
    console.log('Recipes watchEffect 觸發，fridgeItems:', props.fridgeItems)
})


const expandedCategories = ref({
    expiring: false,
    vegetable: false,
    fruit: false,
    meat: false,
    egg: false,
    seasoning: false,
    oil: false,
    seafood: false
})

const isAddModalOpen = ref(false)
const newIngredient = ref({
    name: '',
    category: 'vegetable',
    quantity: 1,
    unit: '個',
    purchased_date: new Date().toISOString().split('T')[0],
    expired_date: '',
})

const isEditModalOpen = ref(false)
const editIngredient = ref(null)

function categorizeIngredients(data) {
    const categorized = {}
    data.forEach((item) => {
        const category = isExpired(item.expired_date) ? 'expiring' : item.category
        if (categorized[category] === undefined) {
            categorized[category] = []
        }
        categorized[category].push(item)
    })
    return categorized
}

function toggleCategory(key) {
    expandedCategories.value[key] = !expandedCategories.value[key]
}

function deleteIngredient(ingredientId) {
    if (confirm('確定要刪除這個食材嗎?')) {
        Object.keys(ingredients.value).forEach((category) => {
            ingredients.value[category] = ingredients.value[category].filter((item) => item.id !== ingredientId)
        })
        // ✅ 新增這行
        emit('updateFridge', getUpdatedFridgeItems())
    }
}

function openEditModal(ingredient) {
    editIngredient.value = { ...ingredient }
    isEditModalOpen.value = true
}

function openAddModal() {
    isAddModalOpen.value = true
    newIngredient.value = {
        name: '',
        category: 'vegetable',
        quantity: 1,
        unit: '個',
        purchased_date: new Date().toISOString().split('T')[0],
        expired_date: '',
    }
}

function closeEditModal() {
    editIngredient.value = null
    isEditModalOpen.value = false
}

function closeAddModal() {
    isAddModalOpen.value = false
}

function saveEdit(updatedData) {
    if (updatedData.purchased_date > updatedData.expired_date) {
        alert('購買日期不能晚於過期日期')
        return
    }
    if (updatedData.quantity <= 0) { 
        alert('數量必須大於 0!')
        return
    }
    
    // 找到要更新的食材，並更新它
    Object.keys(ingredients.value).forEach((category) => {
        ingredients.value[category] = ingredients.value[category].filter((item) => item.id !== editIngredient.value.id)
    })

    const category = isExpired(updatedData.expired_date) ? 'expiring' : updatedData.category
    if (!ingredients.value[category]) {
        ingredients.value[category] = []
    }
    ingredients.value[category].push(updatedData)

    closeEditModal()
    // ✅ 新增這行
    emit('updateFridge', getUpdatedFridgeItems())
}

function addIngredient() {
    if (!newIngredient.value.name) {
        alert('請輸入食材名稱!')
        return
    }
    if (newIngredient.value.quantity <= 0) {
        alert('數量必須大於 0!')
        return
    }
    if (!newIngredient.value.expired_date) {
        alert('請輸入過期日期!')
        return
    }
    if (newIngredient.value.purchased_date > newIngredient.value.expired_date) {
        alert('購買日期不得晚於過期日期!')
        return
    }

    // ✅ 完整新增邏輯
    const ingredient = {
        name: newIngredient.value.name,
        category: newIngredient.value.category,
        quantity: newIngredient.value.quantity,
        unit: newIngredient.value.unit,
        purchased_date: newIngredient.value.purchased_date,
        expired_date: newIngredient.value.expired_date,
    }

    const category = isExpired(ingredient.expired_date) ? 'expiring' : ingredient.category
    if (!ingredients.value[category]) {
        ingredients.value[category] = []
    }
    ingredients.value[category].push(ingredient)

    emit('updateFridge', getUpdatedFridgeItems())
    closeAddModal()
}

// ✅ 新增這個函數
function getUpdatedFridgeItems() {
    const result = []
    Object.values(ingredients.value).forEach((categoryItems) => {
        if (Array.isArray(categoryItems)) {
            result.push(...categoryItems)
        }
    })
    console.log('getUpdatedFridgeItems 返回:', result)
    return result
}

function groupByName(data) {
    const grouped = {}
    data.forEach((item) => {
        if (!grouped[item.name]) {
            grouped[item.name] = []
        }
        grouped[item.name].push(item)
    })
    return grouped
}

function calculateDaysLeft(expiredDate) {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const expired = new Date(expiredDate)
    expired.setHours(0, 0, 0, 0)
    const diffTime = expired - today
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
}

function isExpired(expiredDate) {
    return calculateDaysLeft(expiredDate) < 0
}
</script>

<template>
    <div class="mt-28 max-w-4xl mx-auto px-4">
        <!-- ✅ 改成 flex 排列，h1 靠左，按鈕靠右 -->
        <div class="flex justify-center items-center relative mb-8">
            <h1 class="text-4xl font-bold text-gray-800">我的冰箱</h1>
            <button 
                @click="openAddModal"
                class="bg-amber-500 text-white px-4 py-2 rounded-lg font-semibold cursor-pointer absolute right-0"
            >
                新增食材
            </button>
        </div>

        <div class="space-y-4">
            <div v-for="category in categories" :key="category.key">
                <div class="bg-white rounded-lg shadow-md p-4 cursor-pointer" @click="toggleCategory(category.key)">
                    <div class="flex justify-between items-center p-2">
                        <span>
                            {{ category.name }}
                        </span>
                        <span>
                            {{ ingredients[category.key]?.length || 0 }}
                        </span>
                    </div>
                </div>
                <div v-if="expandedCategories[category.key]" class="border-t border-gray-500 space-y-4 p-4">
                    <div
                        v-for="(group, ingredientName) in groupByName(ingredients[category.key] || [])"
                        :key="ingredientName"
                        class="border-l-4 border-blue-500 pl-4"
                    >
                        <h4 class="font-bold mb-2">{{ ingredientName }}</h4>
                        <div v-for="ingredient in group" :key="ingredient.id" class="bg-gray-50 rounded p-3 flex justify-between items-center">
                            <div class="p-2 space-y-1 text-sm">
                                <div>購買日期: {{ ingredient.purchased_date }}</div>
                                <div>{{ ingredient.quantity }}{{ ingredient.unit }}</div>
                                <div class="text-red-500" v-if="isExpired(ingredient.expired_date)">已過期</div>
                                <div v-else>還有 {{ calculateDaysLeft(ingredient.expired_date) }} 天</div>
                            </div>
                            <div class="flex gap-2">
                                <button @click="openEditModal(ingredient)" class="bg-blue-500 text-white px-3 py-1 rounded text-sm">編輯</button>
                                <button @click="deleteIngredient(ingredient.id)" class="bg-red-500 text-white px-3 py-1 rounded text-sm">刪除</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 新增食材 Modal -->
        <div
            v-if="isAddModalOpen"
            class="fixed inset-0 bg-opacity-30 flex items-center justify-center z-50"
            style="background-color: rgba(0, 0, 0, 0.2)"
        >
            <div class="bg-white rounded-lg p-6 w-96">
                <h2 class="text-2xl font-bold mb-4">新增食材</h2>

                <div class="space-y-4">
                    <div>
                        <label class="block font-bold mb-2">食材名稱 *</label>
                        <input 
                            v-model="newIngredient.name" 
                            type="text" 
                            placeholder="例如：番茄、雞蛋"
                            class="w-full border p-2 rounded"
                        />
                    </div>

                    <div>
                        <label class="block font-bold mb-2">分類 *</label>
                        <select v-model="newIngredient.category" class="w-full border p-2 rounded">
                            <option value="vegetable">蔬菜</option>
                            <option value="fruit">水果</option>
                            <option value="meat">肉類</option>
                            <option value="egg">蛋類</option>
                            <option value="seasoning">調味料</option>
                            <option value="oil">油類</option>
                            <option value="seafood">海鮮</option>
                        </select>
                    </div>

                    <div class="flex gap-4">
                        <div class="flex-1">
                            <label class="block font-bold mb-2">數量 *</label>
                            <input 
                                v-model.number="newIngredient.quantity" 
                                type="number" 
                                min="1"
                                class="w-full border p-2 rounded"
                            />
                        </div>
                        <div class="flex-1">
                            <label class="block font-bold mb-2">單位 *</label>
                            <select v-model="newIngredient.unit" class="w-full border p-2 rounded">
                                <option value="個">個</option>
                                <option value="克">克</option>
                                <option value="毫升">毫升</option>
                                <option value="瓶">瓶</option>
                                <option value="盒">盒</option>
                                <option value="匙">匙</option>
                                <option value="斤">斤</option>
                            </select>
                        </div>
                    </div>

                    <div>
                        <label class="block font-bold mb-2">購買日期 *</label>
                        <input 
                            v-model="newIngredient.purchased_date" 
                            type="date" 
                            class="w-full border p-2 rounded"
                        />
                    </div>

                    <div>
                        <label class="block font-bold mb-2">過期日期 *</label>
                        <input 
                            v-model="newIngredient.expired_date" 
                            type="date" 
                            class="w-full border p-2 rounded"
                        />
                    </div>
                </div>

                <div class="flex gap-2 mt-6">
                    <button 
                        @click="addIngredient" 
                        class="flex-1 bg-amber-500 text-white px-4 py-2 rounded font-semibold hover:bg-amber-600"
                    >
                        新增
                    </button>
                    <button 
                        @click="closeAddModal" 
                        class="flex-1 bg-gray-500 text-white px-4 py-2 rounded font-semibold hover:bg-gray-600"
                    >
                        取消
                    </button>
                </div>
            </div>
        </div>

        <!-- 編輯 Modal -->
        <div
            v-if="isEditModalOpen"
            class="fixed inset-0 bg-opacity-30 flex items-center justify-center z-50"
            style="background-color: rgba(0, 0, 0, 0.2)"
        >
            <div class="bg-white rounded-lg p-6 w-96">
                <h2 class="text-2xl font-bold mb-4">編輯食材</h2>

                <div v-if="editIngredient" class="space-y-4">
                    <div>
                        <label class="block font-bold mb-2">食材名稱</label>
                        <input type="text" v-model="editIngredient.name"  class="w-full border p-2 bg-gray-100" />
                    </div>

                    <div>
                        <label class="block font-bold mb-2">數量</label>
                        <input type="number" v-model.number = "editIngredient.quantity" min="1" class="w-full border p-2" />
                    </div>

                    <div>
                        <label class="block font-bold mb-2">購買日期</label>
                        <input type="date" v-model="editIngredient.purchased_date" class="w-full border p-2" />
                    </div>

                    <div>
                        <label class="block font-bold mb-2">過期日期</label>
                        <input type="date" v-model="editIngredient.expired_date" class="w-full border p-2" />
                    </div>
                </div>

                <div class="flex gap-2 mt-6">
                    <button @click="saveEdit(editIngredient)" class="flex-1 bg-blue-500 text-white px-4 py-2 rounded">確認</button>
                    <button @click="closeEditModal()" class="flex-1 bg-gray-500 text-white px-4 py-2 rounded">取消</button>
                </div>
            </div>
        </div>
    </div>
</template>