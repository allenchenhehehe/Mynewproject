<script setup>
import { ref, onMounted, computed } from 'vue'
import Login from './components/Users/Login.vue'
import SignUp from './components/Users/SignUp.vue'
import Navbar from './components/Users/Navbar.vue'
import ForgetPassword from './components/Users/ForgetPassword.vue'
import Home from './components/ApplicationSection/Home.vue'
import MyFridge from './components/ApplicationSection/MyFridge.vue'
import Recipes from './components/ApplicationSection/Recipes.vue'
import RecipeDetail from './components/ApplicationSection/RecipeDetail.vue'
import ShoppingList from './components/ApplicationSection/ShoppingList.vue'
import { STATUS_LOGIN, STATUS_SIGNUP, STATUS_APP, STATUS_FORGET_PASSWORD } from './libs/constants'

const status = ref(STATUS_LOGIN)
const currentPage = ref('Home')
const selectedRecipe = ref(null)
const shoppingList = ref([])
const mockFridgeData = [
    {
        id: 1,
        ingredient_id: 1,
        name: '洋蔥',
        category: 'vegetable',
        quantity: 5,
        unit: '個',
        purchased_date: '2025-11-03',
        expired_date: '2025-11-12',
    },
    {
        id: 2,
        ingredient_id: 2,
        name: '雞肉',
        category: 'meat',
        quantity: 200,
        unit: '克',
        purchased_date: '2025-11-05',
        expired_date: '2025-11-30',
    },
    {
        id: 3,
        ingredient_id: 3,
        name: '雞蛋',
        category: 'egg',
        quantity: 5,
        unit: '顆',
        purchased_date: '2025-11-08',
        expired_date: '2025-11-30',
    },
    {
        id: 4,
        ingredient_id: 4,
        name: '番茄',
        category: 'vegetable',
        quantity: 5,
        unit: '個',
        purchased_date: '2025-11-03',
        expired_date: '2025-11-30',
    },
    {
        id: 5,
        ingredient_id: 4,
        name: '番茄',
        category: 'vegetable',
        quantity: 3,
        unit: '個',
        purchased_date: '2025-11-30',
        expired_date: '2025-12-5',
    },
    {
        id: 6,
        ingredient_id: 101,
        name: '蔥花',
        category: 'vegetable',
        quantity: 3,
        unit: '匙',
        purchased_date: '2025-11-30',
        expired_date: '2025-12-5',
    },
]

const fridgeItems = ref(mockFridgeData)

const gotoSignup = () => (status.value = STATUS_SIGNUP)
const gotoLogin = () => (status.value = STATUS_LOGIN)
const gotoApp = () => (status.value = STATUS_APP)
const gotoForget = () => (status.value = STATUS_FORGET_PASSWORD)

const handlePageChange = (pageName) => {
    currentPage.value = pageName
}

const handledetail = (recipe) => {
    selectedRecipe.value = recipe
    currentPage.value = 'RecipeDetail'
}

const handleGoBackToRecipes = () => {
    currentPage.value = 'Recipes'
}

const addToShoppingList = (items, recipeName = null, recipeId = null) => {
    if (Array.isArray(items)) {
        shoppingList.value.push({
            recipeId: recipeId,
            recipeName: recipeName || '未命名食譜',
            items: items,
        })
    } else {
        let manualGroup = shoppingList.value.find((g) => g.recipeName === '手動新增')
        if (!manualGroup) {
            manualGroup = {
                recipeId: null,
                recipeName: '手動新增',
                items: [],
            }
            shoppingList.value.push(manualGroup)
        }
        manualGroup.items.push(items)
    }
}
</script>

<template>
    <Login @signup="gotoSignup" @navbar="gotoApp" @forgetpassword="gotoForget" v-if="status == STATUS_LOGIN" />
    <SignUp @login="gotoLogin" v-if="status == STATUS_SIGNUP" />
    <ForgetPassword @login="gotoLogin" v-if="status == STATUS_FORGET_PASSWORD" />

    <div v-if="status == STATUS_APP" class="min-h-screen bg-[#fefae0] text-gray-800 overflow-x-hidden">
        <Navbar :currentPage="currentPage" @change-page="handlePageChange" />

        <Home v-show="currentPage === 'Home'" />
        <MyFridge 
            v-show="currentPage === 'My Fridge'"
            :fridgeItems="fridgeItems"
            @updateFridge="(updatedItems) => (fridgeItems.value = updatedItems)"
        />
        <Recipes 
            v-show="currentPage === 'Recipes'"
            :fridgeItems="fridgeItems"
            @gotorecipedetail="handledetail"
        />
        <RecipeDetail 
            v-show="currentPage === 'RecipeDetail'"
            :recipe="selectedRecipe"
            :fridgeItems="fridgeItems"
            @handlegoback="handleGoBackToRecipes"
            @addToShopping="(items, recipeName, recipeId) => addToShoppingList(items, recipeName, recipeId)"
        />
        <ShoppingList 
            v-show="currentPage === 'Shopping List'"
            :items="shoppingList"
            @add-item="addToShoppingList"
        />
    </div>
</template>