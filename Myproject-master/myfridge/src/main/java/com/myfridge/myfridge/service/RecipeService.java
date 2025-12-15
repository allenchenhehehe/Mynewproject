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
    
    /**
     * 查詢所有公開食譜（含食材）
     */
    public List<Recipe> getAllRecipes() {
        try {
            return recipeRepository.findAll();  // ✅ 對應 Repository 的 findAll()
        } catch (Exception e) {
            System.err.println("查詢所有食譜失敗: " + e.getMessage());
            throw new RuntimeException("查詢食譜失敗", e);
        }
    }
    
    /**
     * 查詢食譜詳情（含食材）
     */
    public Recipe getRecipeDetail(Integer recipeId) {
        if (recipeId == null) {
            throw new IllegalArgumentException("食譜 ID 不可為空");
        }
        
        try {
            Recipe recipe = recipeRepository.findById(recipeId);  // ✅ 對應 Repository 的 findById()
            
            if (recipe == null) {
                System.out.println("找不到食譜 ID: " + recipeId);
            }
            
            return recipe;
        } catch (Exception e) {
            System.err.println("查詢食譜詳情失敗 (ID: " + recipeId + "): " + e.getMessage());
            throw new RuntimeException("查詢食譜詳情失敗", e);
        }
    }
    
    /**
     * 關鍵字搜尋食譜（含食材）
     */
    public List<Recipe> searchRecipes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRecipes();
        }
        
        try {
            return recipeRepository.findByKeyword(keyword.trim());  // ✅ 對應 Repository 的 findByKeyword()
        } catch (Exception e) {
            System.err.println("搜尋食譜失敗 (關鍵字: " + keyword + "): " + e.getMessage());
            throw new RuntimeException("搜尋食譜失敗", e);
        }
    }
    
    /**
     * 動態過濾食譜（難度、烹飪時間）
     */
    public List<Recipe> filterRecipes(Integer difficulty, Integer minCookingTime, Integer maxCookingTime) {
        // 參數驗證
        if (difficulty != null && (difficulty < 1 || difficulty > 5)) {
            throw new IllegalArgumentException("難度等級必須在 1-5 之間");
        }
        if (minCookingTime != null && minCookingTime < 0) {
            throw new IllegalArgumentException("最小烹飪時間不可為負數");
        }
        if (maxCookingTime != null && maxCookingTime < 0) {
            throw new IllegalArgumentException("最大烹飪時間不可為負數");
        }
        if (minCookingTime != null && maxCookingTime != null && minCookingTime > maxCookingTime) {
            throw new IllegalArgumentException("最小烹飪時間不可大於最大烹飪時間");
        }
        
        try {
            // 如果沒有任何篩選條件，回傳所有食譜
            if (difficulty == null && minCookingTime == null && maxCookingTime == null) {
                return getAllRecipes();
            }
            
            return recipeRepository.filter(difficulty, minCookingTime, maxCookingTime);  // ✅ 對應 Repository 的 filter()
        } catch (Exception e) {
            System.err.println("篩選食譜失敗: " + e.getMessage());
            throw new RuntimeException("篩選食譜失敗", e);
        }
    }
    
    /**
     * 根據難度查詢食譜
     */
    public List<Recipe> getRecipesByDifficulty(Integer difficulty) {
        return filterRecipes(difficulty, null, null);
    }
    
    /**
     * 查詢快速食譜（烹飪時間 <= maxMinutes）
     */
    public List<Recipe> getQuickRecipes(Integer maxMinutes) {
        return filterRecipes(null, null, maxMinutes);
    }
    
    /**
     * 查詢慢煮食譜（烹飪時間 >= minMinutes）
     */
    public List<Recipe> getSlowRecipes(Integer minMinutes) {
        return filterRecipes(null, minMinutes, null);
    }
}
