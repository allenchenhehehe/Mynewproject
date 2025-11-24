<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'

const props = defineProps({ fridgeItems: Array })
const emit = defineEmits(['updateFridge'])

const categories = [
    { key: 'expiring', name: '即將過期', color: 'bg-red-100' }, // 增加顏色配置
    { key: 'vegetable', name: '蔬菜', color: 'bg-green-100' },
    { key: 'fruit', name: '水果', color: 'bg-pink-100' },
    { key: 'meat', name: '肉類', color: 'bg-red-200' },
    { key: 'egg', name: '蛋類', color: 'bg-yellow-100' },
    { key: 'seasoning', name: '調味料', color: 'bg-gray-100' },
    { key: 'oil', name: '油類', color: 'bg-orange-100' },
    { key: 'seafood', name: '海鮮', color: 'bg-blue-100' },
]

const ingredients = ref(categorizeIngredients(props.fridgeItems || []))

// ✅ 改進：監聽 props 變化並實際更新本地數據
watch(() => props.fridgeItems, (newItems) => {
    console.log('MyFridge 數據更新:', newItems)
    ingredients.value = categorizeIngredients(newItems || [])
}, { deep: true })

const expandedCategories = ref({
    expiring: true, // 預設展開即將過期
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
        let hasChanged = false
        Object.keys(ingredients.value).forEach((category) => {
            const originalLength = ingredients.value[category].length
            ingredients.value[category] = ingredients.value[category].filter((item) => item.id !== ingredientId)
            if (ingredients.value[category].length !== originalLength) hasChanged = true
        })
        if (hasChanged) emit('updateFridge', getUpdatedFridgeItems())
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
    
    // 簡單的更新邏輯：先移除舊的，再重新分類加入新的
    const currentList = getUpdatedFridgeItems().filter(item => item.id !== editIngredient.value.id)
    currentList.push(updatedData)
    
    // 重新分類並更新本地視圖
    ingredients.value = categorizeIngredients(currentList)
    
    closeEditModal()
    emit('updateFridge', currentList)
}

function addIngredient() {
    if (!newIngredient.value.name) return alert('請輸入食材名稱!')
    if (newIngredient.value.quantity <= 0) return alert('數量必須大於 0!')
    if (!newIngredient.value.expired_date) return alert('請輸入過期日期!')
    if (newIngredient.value.purchased_date > newIngredient.value.expired_date) return alert('購買日期不得晚於過期日期!')

    const ingredient = {
        id: Date.now(), // 暫時給個 ID
        ...newIngredient.value
    }

    const category = isExpired(ingredient.expired_date) ? 'expiring' : ingredient.category
    if (!ingredients.value[category]) {
        ingredients.value[category] = []
    }
    ingredients.value[category].push(ingredient)

    emit('updateFridge', getUpdatedFridgeItems())
    closeAddModal()
}

function getUpdatedFridgeItems() {
    const result = []
    Object.values(ingredients.value).forEach((categoryItems) => {
        if (Array.isArray(categoryItems)) {
            result.push(...categoryItems)
        }
    })
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
    <div class="mt-28 max-w-5xl mx-auto px-6 pb-20 font-sans text-black">
        
        <div class="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
            <h1 class="text-5xl font-black uppercase tracking-tighter text-stroke-black relative inline-block">
                MY FRIDGE
                <span class="absolute -top-4 -right-8 bg-yellow-300 text-black text-xs font-bold px-2 py-1 border-2 border-black rotate-12 shadow-[2px_2px_0px_0px_black]">
                    STOCK IT UP!
                </span>
            </h1>
            <button 
                @click="openAddModal"
                class="bg-[#a7f3d0] text-black border-2 border-black px-6 py-3 font-bold shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all flex items-center gap-2"
            >
                <span class="text-xl leading-none font-black">+</span> ADD ITEM
            </button>
        </div>

        <div class="grid grid-cols-1 gap-6">
            <div v-for="category in categories" :key="category.key">
                
                <div 
                    @click="toggleCategory(category.key)"
                    class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] transition-all cursor-pointer relative overflow-hidden group"
                    :class="{'mb-4': !expandedCategories[category.key]}"
                >
                    <div class="flex justify-between items-center p-4 relative z-10" 
                         :class="expandedCategories[category.key] ? 'bg-black text-white' : 'bg-white text-black'">
                        <div class="flex items-center gap-3">
                            <span class="font-black text-xl uppercase tracking-wide">{{ category.name }}</span>
                        </div>
                        <div class="flex items-center gap-3">
                            <span class="font-bold border-2 px-2 py-0.5 rounded-full"
                                  :class="expandedCategories[category.key] ? 'border-white bg-black' : 'border-black bg-gray-100'">
                                {{ ingredients[category.key]?.length || 0 }}
                            </span>
                            <span class="text-2xl font-black transform transition-transform" 
                                  :class="expandedCategories[category.key] ? 'rotate-180 text-white' : 'rotate-0'">
                                ▼
                            </span>
                        </div>
                    </div>
                </div>

                <div v-if="expandedCategories[category.key]" 
                     class="border-x-2 border-b-2 border-black bg-white p-6 shadow-[4px_4px_0px_0px_black] mb-6 -mt-2 relative">
                    
                    <div class="absolute inset-0 opacity-5 pointer-events-none" style="background-image: radial-gradient(circle, #000 1px, transparent 1px); background-size: 10px 10px;"></div>

                    <div v-if="!ingredients[category.key] || ingredients[category.key].length === 0" class="text-center py-8 text-gray-400 font-bold italic">
                        EMPTY / 這裡空空如也
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 relative z-10">
                        <div
                            v-for="(group, ingredientName) in groupByName(ingredients[category.key] || [])"
                            :key="ingredientName"
                            class="border-2 border-black bg-white p-0"
                        >
                            <div class="bg-[#fefae0] border-b-2 border-black p-2 font-black text-lg flex justify-between items-center">
                                <span>{{ ingredientName }}</span>
                                <span class="text-xs font-normal border border-black px-1 bg-white">x{{ group.length }}</span>
                            </div>

                            <div class="p-3 space-y-3">
                                <div v-for="ingredient in group" :key="ingredient.id" class="flex flex-col gap-2">
                                    <div class="flex justify-between items-start">
                                        <div class="text-sm font-bold font-mono text-gray-600">
                                            <div class="mb-1">
                                                <span class="bg-gray-200 px-1 border border-black text-xs text-black">QTY</span> 
                                                <span class="text-black text-lg ml-1">{{ ingredient.quantity }} {{ ingredient.unit }}</span>
                                            </div>
                                            <div class="text-xs">
                                                PUR: {{ ingredient.purchased_date }}
                                            </div>
                                        </div>
                                        
                                        <div v-if="isExpired(ingredient.expired_date)" class="bg-red-500 text-white border-2 border-black px-2 py-1 text-xs font-black animate-pulse">
                                            EXPIRED
                                        </div>
                                        <div v-else class="bg-yellow-300 text-black border-2 border-black px-2 py-1 text-xs font-black">
                                            {{ calculateDaysLeft(ingredient.expired_date) }} DAYS LEFT
                                        </div>
                                    </div>

                                    <div class="flex gap-2 mt-1 pt-2 border-t-2 border-dashed border-gray-300">
                                        <button @click="openEditModal(ingredient)" class="flex-1 bg-[#bfdbfe] border-2 border-black py-1 text-xs font-bold hover:bg-blue-300 hover:shadow-[2px_2px_0px_0px_black] transition-all">
                                            EDIT
                                        </button>
                                        <button @click="deleteIngredient(ingredient.id)" class="flex-1 bg-[#fecaca] border-2 border-black py-1 text-xs font-bold hover:bg-red-300 hover:shadow-[2px_2px_0px_0px_black] transition-all">
                                            DELETE
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div v-if="isAddModalOpen || isEditModalOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
            <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="closeAddModal(); closeEditModal()"></div>
            
            <div class="relative bg-white border-4 border-black shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] w-full max-w-md p-6 md:p-8 animate-in zoom-in-95 duration-200">
                <div class="flex justify-between items-center mb-6">
                    <h2 class="text-3xl font-black uppercase italic bg-yellow-300 inline-block px-2 border-2 border-black transform -rotate-2">
                        {{ isEditModalOpen ? 'EDIT ITEM' : 'NEW ITEM' }}
                    </h2>
                    <button @click="isEditModalOpen ? closeEditModal() : closeAddModal()" class="font-black text-2xl hover:text-red-500 hover:rotate-90 transition-transform">X</button>
                </div>

                <div class="space-y-4 font-bold">
                    <div v-if="isEditModalOpen ? editIngredient : newIngredient" class="space-y-4">
                        <div>
                            <label class="block mb-1 text-sm uppercase tracking-wider">Name</label>
                            <input type="text" v-model="(isEditModalOpen ? editIngredient : newIngredient).name" 
                                class="w-full border-2 border-black p-3 bg-gray-50 focus:bg-white focus:ring-0 focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none"
                                placeholder="e.g., Tomato"
                            />
                        </div>

                        <div v-if="!isEditModalOpen"> <label class="block mb-1 text-sm uppercase tracking-wider">Category</label>
                            <select v-model="newIngredient.category" class="w-full border-2 border-black p-3 bg-gray-50 focus:ring-0 focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none appearance-none cursor-pointer">
                                <option v-for="cat in categories.filter(c=>c.key!=='expiring')" :key="cat.key" :value="cat.key">{{ cat.name }}</option>
                            </select>
                        </div>

                        <div class="flex gap-4">
                            <div class="flex-1">
                                <label class="block mb-1 text-sm uppercase tracking-wider">Qty</label>
                                <input type="number" v-model.number="(isEditModalOpen ? editIngredient : newIngredient).quantity" min="1"
                                    class="w-full border-2 border-black p-3 bg-gray-50 focus:ring-0 focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none"
                                />
                            </div>
                            <div class="flex-1" v-if="!isEditModalOpen || (isEditModalOpen && editIngredient)">
                                <label class="block mb-1 text-sm uppercase tracking-wider">Unit</label>
                                <select v-model="(isEditModalOpen ? editIngredient : newIngredient).unit" 
                                        class="w-full border-2 border-black p-3 bg-gray-50 focus:ring-0 focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none"
                                        :disabled="isEditModalOpen" 
                                        :class="{'opacity-50 cursor-not-allowed': isEditModalOpen}">
                                    <option>個</option><option>顆</option><option>克</option><option>公斤</option>
                                    <option>毫升</option><option>公升</option><option>盒</option><option>包</option>
                                    <option>瓶</option><option>罐</option><option>袋</option><option>片</option><option>匙</option>
                                </select>
                            </div>
                        </div>

                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <label class="block mb-1 text-sm uppercase tracking-wider">Purchased</label>
                                <input type="date" v-model="(isEditModalOpen ? editIngredient : newIngredient).purchased_date" 
                                    class="w-full border-2 border-black p-3 bg-gray-50 focus:ring-0 focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none"
                                />
                            </div>
                            <div>
                                <label class="block mb-1 text-sm uppercase tracking-wider">Expires</label>
                                <input type="date" v-model="(isEditModalOpen ? editIngredient : newIngredient).expired_date" 
                                    class="w-full border-2 border-black p-3 bg-gray-50 focus:ring-0 focus:shadow-[4px_4px_0px_0px_black] transition-all outline-none"
                                />
                            </div>
                        </div>
                    </div>
                </div>

                <div class="flex gap-4 mt-8">
                    <button 
                        @click="isEditModalOpen ? saveEdit(editIngredient) : addIngredient()" 
                        class="flex-1 bg-[#a7f3d0] border-2 border-black py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all"
                    >
                        {{ isEditModalOpen ? 'Save Changes' : 'Add It' }}
                    </button>
                    <button 
                        @click="isEditModalOpen ? closeEditModal() : closeAddModal()" 
                        class="flex-1 bg-white border-2 border-black py-3 font-black uppercase shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all"
                    >
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 為了讓文字有漫畫般的描邊效果 */
.text-stroke-black {
  -webkit-text-stroke: 1px black;
}
/* 隱藏 select 預設箭頭，因為我們自定義樣式 */
select {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%23000000' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
    background-position: right 0.75rem center;
    background-repeat: no-repeat;
    background-size: 1.5em 1.5em;
    padding-right: 2.5rem;
    appearance: none;
}
</style>