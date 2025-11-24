<script setup>
import { ref, defineEmits, computed } from 'vue'
const props = defineProps({ items: Array })
const emit = defineEmits(['add-item'])
const itemName = ref('')
const itemQuantity = ref(1)
const itemUnit = ref('個')

const filteredItems = computed(() => {
    return props.items.filter((group) => group.items.length > 0)
})

const stats = computed(() => {
    let total = 0
    let purchased = 0
    props.items.forEach((group) => {
        group.items.forEach((item) => {
            total++
            if (item.is_purchased) purchased++
        })
    })
    return {
        total,
        purchased,
        remaining: total - purchased,
        percentage: total > 0 ? Math.round((purchased / total) * 100) : 0
    }
})

function deleteItem(itemId) {
    props.items.forEach((group) => {
        const index = group.items.findIndex((item) => item.id === itemId)
        if (index > -1) {
            group.items.splice(index, 1)
        }
    })
}

function togglePurchased(itemId) {
    props.items.forEach((group) => {
        const item = group.items.find((item) => item.id === itemId)
        if (item) {
            item.is_purchased = !item.is_purchased
        }
    })
}

function addItem() {
    if (itemName.value.trim() === '') {
        alert('食材輸入不可為空白!')
        return
    }
    const newItem = {
        ingredient_name: itemName.value,
        quantity: itemQuantity.value,
        unit: itemUnit.value,
        is_purchased: false,
    }
    emit('add-item', newItem)
    itemName.value = ''
    itemQuantity.value = 1
    itemUnit.value = '個'
}

function clearPurchased() {
    props.items.forEach((group) => {
        group.items = group.items.filter((item) => !item.is_purchased)
    })
}
</script>

<template>
    <div class="mt-28 max-w-6xl mx-auto px-6 pb-20 font-sans text-black">
        
        <!-- 標題 -->
        <div class="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
            <h1 class="text-5xl font-black uppercase tracking-tighter relative inline-block">
                SHOPPING LIST
                <span class="absolute -top-4 -right-8 bg-green-300 text-black text-xs font-bold px-2 py-1 border-2 border-black rotate-12 shadow-[2px_2px_0px_0px_black]">
                    BUY IT!
                </span>
            </h1>
        </div>

        <!-- 進度卡片 -->
        <div class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] mb-8 p-6">
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div class="border-2 border-black bg-blue-100 p-4 text-center">
                    <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">總項目</div>
                    <div class="text-4xl font-black">{{ stats.total }}</div>
                </div>
                <div class="border-2 border-black bg-green-100 p-4 text-center">
                    <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">已購買</div>
                    <div class="text-4xl font-black">{{ stats.purchased }}</div>
                </div>
                <div class="border-2 border-black bg-orange-100 p-4 text-center">
                    <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">待購買</div>
                    <div class="text-4xl font-black">{{ stats.remaining }}</div>
                </div>
                <div class="border-2 border-black bg-yellow-100 p-4 text-center">
                    <div class="text-xs font-black uppercase tracking-wide text-gray-600 mb-2">進度</div>
                    <div class="text-4xl font-black">{{ stats.percentage }}%</div>
                </div>
            </div>

            <!-- 進度條 -->
            <div class="mt-6 space-y-2">
                <div class="text-xs font-black uppercase tracking-wide">購物進度</div>
                <div class="border-2 border-black bg-gray-200 h-8 overflow-hidden">
                    <div 
                        :style="{ width: stats.percentage + '%' }"
                        class="bg-green-400 h-full transition-all border-r-2 border-black flex items-center justify-center"
                    >
                        <span v-if="stats.percentage > 20" class="font-black text-xs text-black">{{ stats.percentage }}%</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 新增食材區塊 -->
        <div class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black] mb-8 p-6">
            <h2 class="text-2xl font-black uppercase tracking-wide mb-6">新增食材</h2>
            <div class="space-y-4">
                <input 
                    v-model="itemName" 
                    type="text" 
                    placeholder="食材名稱..."
                    class="w-full border-2 border-black px-4 py-3 font-bold bg-yellow-50 focus:bg-yellow-100 focus:outline-none focus:shadow-[2px_2px_0px_0px_black] transition-all"
                />
                <div class="grid grid-cols-3 gap-3">
                    <input 
                        v-model.number="itemQuantity" 
                        type="number" 
                        placeholder="數量"
                        class="border-2 border-black px-4 py-3 font-bold bg-white focus:bg-yellow-50 focus:outline-none focus:shadow-[2px_2px_0px_0px_black] transition-all"
                    />
                    <select 
                        v-model="itemUnit"
                        class="border-2 border-black px-4 py-3 font-bold bg-white focus:bg-yellow-50 focus:outline-none focus:shadow-[2px_2px_0px_0px_black] transition-all appearance-none cursor-pointer"
                    >
                        <option>個</option>
                        <option>顆</option>
                        <option>克</option>
                        <option>公斤</option>
                        <option>毫升</option>
                        <option>公升</option>
                        <option>盒</option>
                        <option>包</option>
                        <option>瓶</option>
                        <option>罐</option>
                        <option>袋</option>
                        <option>片</option>
                        <option>匙</option>
                    </select>
                    <button 
                        @click="addItem"
                        class="border-2 border-black bg-green-400 text-black font-black py-3 px-4 uppercase tracking-wide shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all"
                    >
                        新增
                    </button>
                </div>
            </div>
        </div>

        <!-- 購物清單區塊 -->
        <div class="space-y-6">
            <div v-for="group in filteredItems" :key="group.recipeId || 'manual'" class="border-2 border-black bg-white shadow-[4px_4px_0px_0px_black]">
                
                <!-- 食譜標題區 -->
                <div class="border-b-2 border-black bg-gray-100 p-4">
                    <h3 class="text-2xl font-black uppercase tracking-wide">{{ group.recipeName }}</h3>
                </div>

                <!-- 食材列表 -->
                <div class="p-4 space-y-2">
                    <div 
                        v-for="item in group.items" 
                        :key="item.id"
                        :class="item.is_purchased ? 'bg-gray-100' : 'bg-white'"
                        class="border-2 border-black p-4 flex items-center gap-4 hover:shadow-[2px_2px_0px_0px_black] transition-all group"
                    >
                        <!-- 勾選框 -->
                        <input 
                            type="checkbox"
                            :checked="item.is_purchased"
                            @change="togglePurchased(item.id)"
                            class="w-6 h-6 border-2 border-black cursor-pointer"
                        />

                        <!-- 食材名稱 -->
                        <span 
                            :class="item.is_purchased ? 'line-through text-gray-400' : 'text-black'"
                            class="flex-1 font-bold text-lg"
                        >
                            {{ item.ingredient_name }}
                        </span>

                        <!-- 數量 -->
                        <span class="bg-black text-white px-3 py-1 font-black text-sm whitespace-nowrap">
                            {{ item.quantity }}{{ item.unit }}
                        </span>

                        <!-- 刪除按鈕 -->
                        <button
                            @click="deleteItem(item.id)"
                            class="border-2 border-red-400 bg-red-100 text-red-600 font-black px-3 py-1 text-sm hover:bg-red-200 transition-all opacity-0 group-hover:opacity-100"
                        >
                            刪除
                        </button>
                    </div>
                </div>
            </div>

            <!-- 空清單提示 -->
            <div v-if="stats.total === 0" class="border-4 border-dashed border-black bg-yellow-100 shadow-[4px_4px_0px_0px_black] p-8 text-center">
                <p class="font-black text-2xl mb-2">購物清單為空</p>
                <p class="text-gray-700 font-bold">去食譜頁面新增食材吧！</p>
            </div>
        </div>

        <!-- 清除已購買按鈕 -->
        <div v-if="stats.purchased > 0" class="mt-8">
            <button
                @click="clearPurchased"
                class="w-full border-2 border-black bg-orange-400 text-black font-black py-4 px-6 uppercase tracking-wide shadow-[4px_4px_0px_0px_black] hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-[2px_2px_0px_0px_black] active:translate-x-1 active:translate-y-1 active:shadow-none transition-all text-lg"
            >
                清除已購買 ({{ stats.purchased }})
            </button>
        </div>

    </div>
</template>

<style scoped>
/* select 自訂樣式 */
select {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%23000000' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
    background-position: right 0.5rem center;
    background-repeat: no-repeat;
    background-size: 1.5em 1.5em;
    padding-right: 2.5rem;
}
</style>