import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/myfridge/api'

export const useRecipeStore = defineStore('recipe', () => {
  // State
  const recipes = ref([])
  const loading = ref(false)
  const error = ref(null)

  // Getters
  const recipeCount = computed(() => recipes.value.length)
  const allRecipes = computed(() => recipes.value)

  // Actions

  //取得所有食譜
  async function fetchRecipes() {

    loading.value = true
    error.value = null

    try {

      const response = await axios.get(
        `${API_BASE_URL}/recipes`,
        { withCredentials: true }
      )

      // 轉換後端資料格式為前端格式
      recipes.value = response.data.map(recipe => ({
        id: recipe.id,
        title: recipe.title,
        description: recipe.description,
        difficulty: recipe.difficulty,
        cooking_time: recipe.cookingTime,
        step: recipe.step,
        creator_id: recipe.creatorId || recipe.userId || 'system',
        is_public: recipe.isPublic,
        image_url: recipe.imageUrl,
        ingredients: recipe.ingredients.map(ing => ({
          ingredient_id: ing.ingredientId,
          ingredient_name: ing.ingredientName,
          quantity: ing.amount,  // 後端是 amount，前端用 quantity
          unit: ing.unit,
          category: ing.category
        }))
      }))

      return { success: true }

    } catch (err) {
      console.error('載入失敗:', err)
      error.value = err.response?.data?.error || '載入失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  //取得單一食譜
  async function fetchRecipeById(id) {
    loading.value = true
    error.value = null

    try {

      const response = await axios.get(
        `${API_BASE_URL}/recipes/${id}`,
        { withCredentials: true }
      )

      // 轉換格式
      const recipe = {
        id: response.data.id,
        title: response.data.title,
        description: response.data.description,
        difficulty: response.data.difficulty,
        cooking_time: response.data.cookingTime,
        step: response.data.step,
        creator_id: response.data.creatorId || response.data.userId || 'system',
        is_public: response.data.isPublic,
        image_url: response.data.imageUrl,
        ingredients: response.data.ingredients.map(ing => ({
          ingredient_id: ing.ingredientId,
          ingredient_name: ing.ingredientName,
          quantity: ing.amount,
          unit: ing.unit,
          category: ing.category
        }))
      }

      return { success: true, recipe }

    } catch (err) {
      console.error('載入失敗:', err)
      error.value = err.response?.data?.error || '載入失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  //搜尋食譜
  async function searchRecipes(keyword) {
    if (!keyword || !keyword.trim()) {
      // 空搜尋，回傳所有食譜
      return await fetchRecipes()
    }

    loading.value = true
    error.value = null

    try {

      const response = await axios.get(
        `${API_BASE_URL}/recipes/search`,
        {
          params: { keyword },
          withCredentials: true
        }
      )

      // 轉換格式
      recipes.value = response.data.map(recipe => ({
        id: recipe.id,
        title: recipe.title,
        description: recipe.description,
        difficulty: recipe.difficulty,
        cooking_time: recipe.cookingTime,
        step: recipe.step,
        creator_id: recipe.creatorId || recipe.userId || 'system',
        is_public: recipe.isPublic,
        image_url: recipe.imageUrl,
        ingredients: recipe.ingredients.map(ing => ({
          ingredient_id: ing.ingredientId,
          ingredient_name: ing.ingredientName,
          quantity: ing.amount,
          unit: ing.unit,
          category: ing.category
        }))
      }))

      return { success: true }

    } catch (err) {
      console.error('搜尋失敗:', err)
      error.value = err.response?.data?.error || '搜尋失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  //篩選食譜
  async function filterRecipes(filters) {
    loading.value = true
    error.value = null

    try {

      const params = {}
      
      if (filters.difficulty) {
        params.difficulty = filters.difficulty
      }
      
      if (filters.minCookingTime !== undefined) {
        params.minCookingTime = filters.minCookingTime
      }
      
      if (filters.maxCookingTime !== undefined) {
        params.maxCookingTime = filters.maxCookingTime
      }

      const response = await axios.get(
        `${API_BASE_URL}/recipes/filter`,
        {
          params,
          withCredentials: true
        }
      )

      // 轉換格式
      recipes.value = response.data.map(recipe => ({
        id: recipe.id,
        title: recipe.title,
        description: recipe.description,
        difficulty: recipe.difficulty,
        cooking_time: recipe.cookingTime,
        step: recipe.step,
        creator_id: recipe.creatorId || recipe.userId || 'system',
        is_public: recipe.isPublic,
        image_url: recipe.imageUrl,
        ingredients: recipe.ingredients.map(ing => ({
          ingredient_id: ing.ingredientId,
          ingredient_name: ing.ingredientName,
          quantity: ing.amount,
          unit: ing.unit,
          category: ing.category
        }))
      }))

      return { success: true }

    } catch (err) {
      console.error('篩選失敗:', err)
      error.value = err.response?.data?.error || '篩選失敗'
      return { success: false, error: error.value }

    } finally {
      loading.value = false
    }
  }

  //從載入的recipes取得單一食譜
  function getRecipeById(id) {
    return recipes.value.find((recipe) => recipe.id === id)
  }

  //從載入的recipes中篩選
  function getRecipesByFilters(filters) {
    let filtered = [...recipes.value]

    if (filters.title) {
      filtered = filtered.filter((recipe) =>
        recipe.title.toLowerCase().includes(filters.title.toLowerCase())
      )
    }

    if (filters.difficulty) {
      filtered = filtered.filter((recipe) => recipe.difficulty === filters.difficulty)
    }

    if (filters.maxCookingTime) {
      filtered = filtered.filter((recipe) => recipe.cooking_time <= filters.maxCookingTime)
    }

    if (filters.category) {
      filtered = filtered.filter((recipe) => recipe.category === filters.category)
    }

    return filtered
  }

  //依難度篩選
  function getRecipesByDifficulty(difficulty) {
    return recipes.value.filter((recipe) => recipe.difficulty === difficulty)
  }

  //依烹飪時間篩選
  function getRecipesByCookingTime(maxMinutes) {
    return recipes.value.filter((recipe) => recipe.cooking_time <= maxMinutes)
  }

  return {
    // State
    recipes,
    loading,
    error,
    // Getters
    recipeCount,
    allRecipes,
    // Actions
    fetchRecipes,
    fetchRecipeById,
    searchRecipes,
    filterRecipes,
    getRecipeById,
    getRecipesByFilters,
    getRecipesByDifficulty,
    getRecipesByCookingTime
  }
})