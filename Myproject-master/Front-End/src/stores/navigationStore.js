import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNavigationStore = defineStore('navigation', () => {
  // State
  const view = localStorage.getItem('lastPage')||'Home'
  const currentPage = ref(view)
  const selectedRecipe = ref(null)

  // Actions
  function goToPage(pageName) {
    currentPage.value = pageName
    localStorage.setItem('lastPage',pageName)
    window.scrollTo(0, 0)
  }

  function goToRecipeDetail(recipe) {
    selectedRecipe.value = recipe
    goToPage('RecipeDetail')
  }

  function goBackToRecipes() {
    goToPage('Recipes')
    selectedRecipe.value = null
  }

  function goHome() {
   goToPage('Home')
  }

  function goToMyFridge() {
    goToPage('MyFridge')
  }

  function goToShoppingList() {
    goToPage('ShoppingList')
  }

  function goToFavorites() {
    goToPage('Favorites')
  }

  function goToMyComments() {
    goToPage('MyComments')
  }

  function goToAdminLogin() {
    goToPage('AdminLogin')
  }

  function goToAdminPanel() {
    goToPage('AdminPanel')
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
    goToMyComments,
    goToAdminLogin,
    goToAdminPanel
  }
})