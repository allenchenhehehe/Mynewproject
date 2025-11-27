import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNavigationStore = defineStore('navigation', () => {
  // State
  const currentPage = ref('Home')
  const selectedRecipe = ref(null)

  // Actions
  function goToPage(pageName) {
    currentPage.value = pageName
    window.scrollTo(0, 0)
    console.log('導航到頁面:', pageName)
  }

  function goToRecipeDetail(recipe) {
    selectedRecipe.value = recipe
    currentPage.value = 'RecipeDetail'
    console.log('查看食譜詳情:', recipe.title)
  }

  function goBackToRecipes() {
    currentPage.value = 'Recipes'
    selectedRecipe.value = null
  }

  function goHome() {
    currentPage.value = 'Home'
  }

  function goToMyFridge() {
    currentPage.value = 'MyFridge'
  }

  function goToShoppingList() {
    currentPage.value = 'ShoppingList'
  }

  function goToFavorites() {
    currentPage.value = 'Favorites'
  }

  function goToMyComments() {
    currentPage.value = 'MyComments'
  }

  return {
    // State
    currentPage,
    selectedRecipe,
    // Actions
    goToPage,
    goToRecipeDetail,
    goBackToRecipes,
    goHome,
    goToMyFridge,
    goToShoppingList,
    goToFavorites,
    goToMyComments
  }
})