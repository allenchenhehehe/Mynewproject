import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // State
  const favoriteRecipes = ref([])
  const allComments = ref([])

  // Getters
  const favoriteCount = computed(() => favoriteRecipes.value.length)

  const commentCount = computed(() => allComments.value.length)

  const averageRating = computed(() => {
    if (allComments.value.length === 0) return 0
    const totalRating = allComments.value.reduce((sum, comment) => sum + comment.rating, 0)
    return (totalRating / allComments.value.length).toFixed(1)
  })

  const totalLikes = computed(() => {
    return allComments.value.reduce((sum, comment) => sum + (comment.likes || 0), 0)
  })

  // Actions
  function addFavorite(recipe) {
    // 檢查是否已經收藏
    const exists = favoriteRecipes.value.some((fav) => fav.id === recipe.id)
    if (!exists) {
      favoriteRecipes.value.push({
        ...recipe,
        savedAt: new Date().toLocaleDateString('zh-TW')
      })
      console.log('收藏食譜:', recipe.title)
    }
  }

  function removeFavorite(recipeId) {
    const index = favoriteRecipes.value.findIndex((fav) => fav.id === recipeId)
    if (index > -1) {
      const removedRecipe = favoriteRecipes.value[index]
      favoriteRecipes.value.splice(index, 1)
      console.log('取消收藏:', removedRecipe.title)
    }
  }

  function isFavorited(recipeId) {
    return favoriteRecipes.value.some((fav) => fav.id === recipeId)
  }

  function addComment(comment) {
    const newComment = {
      id: Date.now(),
      createdAt: new Date().toLocaleDateString('zh-TW'),
      ...comment
    }
    allComments.value.push(newComment)
    console.log('新增評論:', comment.recipeTitle)
  }

  function updateComment(commentId, newText) {
    const comment = allComments.value.find((c) => c.id === commentId)
    if (comment) {
      comment.text = newText
      console.log('更新評論')
    }
  }

  function deleteComment(commentId) {
    const index = allComments.value.findIndex((c) => c.id === commentId)
    if (index > -1) {
      allComments.value.splice(index, 1)
      console.log('刪除評論')
    }
  }

  function getCommentsByRecipe(recipeId) {
    return allComments.value.filter((comment) => comment.recipe_id === recipeId)
  }

  function getRecipeAverageRating(recipeId) {
    const comments = getCommentsByRecipe(recipeId)
    if (comments.length === 0) return 0
    const totalRating = comments.reduce((sum, comment) => sum + comment.rating, 0)
    return (totalRating / comments.length).toFixed(1)
  }

  function clearAllData() {
    favoriteRecipes.value = []
    allComments.value = []
    console.log('清空所有使用者資料')
  }

  return {
    // State
    favoriteRecipes,
    allComments,
    // Getters
    favoriteCount,
    commentCount,
    averageRating,
    totalLikes,
    // Actions
    addFavorite,
    removeFavorite,
    isFavorited,
    addComment,
    updateComment,
    deleteComment,
    getCommentsByRecipe,
    getRecipeAverageRating,
    clearAllData
  }
})