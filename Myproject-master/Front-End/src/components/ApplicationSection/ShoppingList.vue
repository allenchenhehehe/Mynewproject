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
function deleteItem(itemId) {
    // 遍歷每個分組
    props.items.forEach((group) => {
        // 在分組中找食材
        const index = group.items.findIndex((item) => item.id === itemId)
        if (index > -1) {
            // 找到了，就刪除
            group.items.splice(index, 1)
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
    //把輸入框清空
    itemName.value = ''
    itemQuantity.value = 1
    itemUnit.value = '個'
}
</script>
<template>
    <div class="mt-28 mx-auto max-w-4xl px-4">
        <div class="flex justify-center items-center"><h1 class="text-4xl font-bold mb-8">購物清單</h1></div>
        <div class="bg-white rounded-lg shadow-md p-6 mb-6">
            <div class="flex justify-center items-center"><h2 class="text-xl font-bold mb-4">新增食材</h2></div>
            <div class="space-y-3">
                <input v-model="itemName" type="text" placeholder="食材名稱" class="w-full border p-2 rounded" />
                <div class="flex gap-3">
                    <input v-model.number="itemQuantity" type="number" placeholder="數量" class="flex-1 border p-2 rounded" />
                    <select v-model="itemUnit" class="flex-1 border p-2 rounded">
                        <option>個</option>
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
                    </select>
                    <button @click="addItem" class="bg-amber-500 text-white px-6 py-2 rounded cursor-pointer">新增</button>
                </div>
            </div>
        </div>

        <div class="bg-white p-4 rounded-lg shadow-md">
            <div v-for="group in filteredItems" :key="group.recipeId || 'manual'" class="mb-4">
                <h3 class="text-lg font-bold mb-4 pb-2 border-b">{{ group.recipeName }}</h3>
                <div v-for="item in group.items" :key="item.id" class="flex items-center py-3 gap-2 border-b last:border-b-0">
                    <input type="checkbox" @change="deleteItem(item.id)" class="w-5 h-5" />
                    <span class="flex-1">{{ item.ingredient_name }}</span>
                    <span class="text-gray-600">{{ item.quantity }}{{ item.unit }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

