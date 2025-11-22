<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import { toast } from 'vue3-toastify'; // 引用
import 'vue3-toastify/dist/index.css'; // 官方

const recipe = ref(null)
const emit = defineEmits(['handlegoback', 'addToShopping'])
const props = defineProps({
    recipe: Object,
    fridgeItems: Array,
})
function handleGoBack() {
    emit('handlegoback')
}
function addAllToShoppingList() {
    const timestamp = Date.now()
    const itemsToAdd = props.recipe.ingredients.map((ingredient, index) => ({
        id: timestamp + index, // 確保每個 id 都不同
        ingredient_id: null,
        ingredient_name: ingredient.ingredient_name,
        quantity: ingredient.quantity,
        unit: ingredient.unit,
        is_purchased: false,
    }))
    emit('addToShopping', itemsToAdd, props.recipe.title, props.recipe.id) // 一次發出整個陣列
    toast.success(`已將 ${itemsToAdd.length} 項食材添加到購物清單！`, {
        autoClose: 1000,
    })
}
function getMissingIngredients() {
    return props.recipe.ingredients.filter((recipeIng) => {
        //如果冰箱中「沒有」這個食材
        return !props.fridgeItems.some((fridgeItem) => fridgeItem.ingredient_id === recipeIng.ingredient_id)
        //回傳所有「冰箱中沒有」的食材。
    })
}
function getMissingIngredientsText() {
    return getMissingIngredients()
        .map((ing) => `${ing.ingredient_name}`)
        .join('、')
}
</script>
<template>
    <div class="mt-28 max-w-7xl mx-auto px-4">
        <div class="flex justify-center items-center relative">
            <h1 class="mb-8 text-4xl font-bold">食譜詳情</h1>
            <button @click="handleGoBack" class="mb-6 px-4 py-2 bg-gray-300 text-gray-700 rounded font-semibold absolute right-0 cursor-pointer">
                返回列表
            </button>
        </div>

        <div v-if="props.recipe" class="bg-white rounded-b-lg p-4 md:p-6 gap-6 flex flex-col md:flex-row shadow-md">
            <img :src="props.recipe.image_url" :alt="props.recipe.title" class="w-full md:w-1/3 object-cover rounded md:rounded-none  h-80 md:h-auto" />
            <div class="flex flex-col flex-1 gap-6">
                <div class="text-gray-500 border-b border-gray-200 pb-4">
                    <h2 class="mb-6 text-4xl font-bold">{{ props.recipe.title }}</h2>
                    <div class="flex justify-between">
                        <span>烹飪時長 : 約{{ props.recipe.coocking_time }}分</span>
                        <span>創作者 : {{ props.recipe.creator_id }}</span>
                    </div>
                </div>

                <div class="border-b border-gray-200 pb-4">
                    <h3 class="text-lg font-bold mb-4">準備食材 (一人份)</h3>
                    <div class="space-y-2 mb-4">
                        <div v-for="ingredient in props.recipe.ingredients" :key="ingredient.id" class="flex justify-between">
                            <span>{{ ingredient.ingredient_name }}</span>
                            <span>{{ ingredient.quantity }}{{ ingredient.unit }}</span>
                        </div>
                    </div>
                    <div class="flex flex-col md:flex-row gap-4 items-center">
                        <div class="flex-1">
                            <p class="text-red-600 wrap-break-word" >
                                缺少食材:&nbsp;
                                <span class="font-semibold">
                                    {{ getMissingIngredientsText() }}
                                </span>
                            </p>
                        </div>
                        <button @click="addAllToShoppingList" class="w-full md:w-1/3 bg-gray-500 text-white py-2 px-3 rounded text-sm md:text-base  whitespace-nowrap cursor-pointer">
                            加入購物清單
                        </button>
                    </div>
                </div>
                <div>
                    <h3 class="text-lg mb-4 font-bold">詳細步驟</h3>
                    <p class="whitespace-pre-line text-gray-700">{{ props.recipe.step }}</p>
                </div>
            </div>
        </div>
    </div>
</template>
