package com.myfridge.myfridge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    // ✅ 查詢所有食譜（已包含食材）
    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }

    // ✅ 查詢單一食譜（已包含食材）
    public Recipe getRecipeById(Integer recipeId){
        if (recipeId == null) {
            throw new IllegalArgumentException("食譜 ID 不可為空");
        }
        
        Recipe recipe = recipeRepository.findById(recipeId);
        if (recipe == null) {
            throw new RuntimeException("找不到該食譜");
        }
        
        return recipe;
    }

    // 原本的 getRecipeDetail（保留相容性）
    public Recipe getRecipeDetail(Integer recipeId){
        return getRecipeById(recipeId);
    }

    public List<Recipe> searchRecipes(String keyword){
        if(keyword == null || keyword.trim().isEmpty()) {
            return recipeRepository.findAll(); 
        }
        return recipeRepository.findByKeyword(keyword.trim());
    }

    public List<Recipe> filterRecipes(Integer difficulty, Integer minCookingTime, Integer maxCookingTime){
        if(difficulty != null && (difficulty<1 || difficulty>5)) {
            throw new IllegalArgumentException("難度等級必須在 1-5 之間");
        }
        if(minCookingTime != null && minCookingTime<0) {
            throw new IllegalArgumentException("最小烹飪時間不可為負數");
        }
        if(maxCookingTime != null && maxCookingTime<0) {
            throw new IllegalArgumentException("最大烹飪時間不可為負數");
        }
        if (minCookingTime != null && maxCookingTime != null && minCookingTime > maxCookingTime) {
            throw new IllegalArgumentException("最小烹飪時間不可大於最大烹飪時間");
        }

        if (difficulty == null && minCookingTime == null && maxCookingTime == null) {
            return getAllRecipes();
        }
        
        return recipeRepository.filter(difficulty, minCookingTime, maxCookingTime);
    }

    public List<Recipe> getRecipesByDifficulty(Integer difficulty) {
        return filterRecipes(difficulty, null, null);
    }
    
    public List<Recipe> getQuickRecipes(Integer maxMinutes) {
        return filterRecipes(null, null, maxMinutes);
    }
    
    public List<Recipe> getSlowRecipes(Integer minMinutes) {
        return filterRecipes(null, minMinutes, null);
    }

    // ✅ 刪除食譜
    public void deleteRecipe(Integer recipeId) {
        if (recipeId == null) {
            throw new IllegalArgumentException("食譜 ID 不可為空");
        }
        
        int rows = recipeRepository.delete(recipeId);
        if (rows == 0) {
            throw new RuntimeException("找不到該食譜");
        }
        
        System.out.println("管理員刪除食譜: " + recipeId);
    }

    // ✅ 新增食譜（簡單版，不包含食材）
    public Recipe createRecipe(Recipe recipe) {
        // 驗證必填欄位
        if (recipe.getTitle() == null || recipe.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("食譜名稱不可為空");
        }
        
        if (recipe.getDescription() == null || recipe.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("食譜描述不可為空");
        }
        
        // 設定預設值
        if (recipe.getDifficulty() == null) {
            recipe.setDifficulty(1);
        }
        
        if (recipe.getCookingTime() == null) {
            recipe.setCookingTime(30);
        }
        
        // 儲存到資料庫
        return recipeRepository.insert(recipe);
    }
}