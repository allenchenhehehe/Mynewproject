package com.myfridge.myfridge.service;

import com.myfridge.myfridge.entity.Ingredient;
import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.entity.RecipeIngredient;
import com.myfridge.myfridge.repository.IngredientRepository;
import com.myfridge.myfridge.repository.RecipeRepository;
import com.myfridge.myfridge.repository.RecipeIngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecipeImportService {
    
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    
    /**
     * 從 AI 生成的資料匯入食譜（包含食材關聯）
     */
    @Transactional
    public Recipe importRecipeFromAI(Map<String, Object> aiData) {
        try {
            // 1. 建立 Recipe
            Recipe recipe = new Recipe();
            recipe.setTitle((String) aiData.get("title"));
            recipe.setDescription((String) aiData.get("description"));
            recipe.setImageUrl((String) aiData.get("imageUrl"));
            recipe.setCookingTime((Integer) aiData.get("cookingTime"));
            recipe.setDifficulty((Integer) aiData.get("difficulty"));
            recipe.setStep((String) aiData.get("step"));
            
            // 插入 Recipe 表
            Recipe savedRecipe = recipeRepository.insert(recipe);
            Integer recipeId = savedRecipe.getId();
            
            // 2. 處理食材
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> ingredients = 
                (List<Map<String, Object>>) aiData.get("ingredients");
            
            if (ingredients != null && !ingredients.isEmpty()) {
                for (Map<String, Object> ingData : ingredients) {
                    processIngredient(recipeId, ingData);
                }
            }
            
            return savedRecipe;
            
        } catch (Exception e) {
            throw new RuntimeException("匯入食譜失敗: " + e.getMessage(), e);
        }
    }
    
    /**
     * 處理單個食材
     */
    private void processIngredient(Integer recipeId, Map<String, Object> ingData) {
        String name = (String) ingData.get("name");
        String category = (String) ingData.get("category");
        Double amount = ((Number) ingData.get("amount")).doubleValue();
        String unit = (String) ingData.get("unit");
        
        // 1. 檢查 Ingredient 是否存在
        Ingredient ingredient = ingredientRepository.findByName(name);
        Integer ingredientId;
        
        if (ingredient == null) {
            // 2. 不存在，新增到 Ingredient 表
            Ingredient newIngredient = new Ingredient();
            newIngredient.setIngredientName(name);
            newIngredient.setCategory(category);
            newIngredient.setShelfLifeDays(guessShelfLife(category));
            
            Ingredient created = ingredientRepository.insert(newIngredient);
            ingredientId = created.getId();
        } else {
            ingredientId = ingredient.getId();
        }
        
        // 3. 建立 RecipeIngredient 關聯
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setRecipeId(recipeId);
        recipeIngredient.setIngredientId(ingredientId);
        recipeIngredient.setAmount(amount);
        recipeIngredient.setUnit(unit);
        
        recipeIngredientRepository.insert(recipeIngredient);
    }
    
    /**
     * 根據分類推測保存天數
     */
    private Integer guessShelfLife(String category) {
        return switch (category) {
            case "蔬菜" -> 7;
            case "水果" -> 5;
            case "肉類" -> 3;
            case "海鮮" -> 2;
            case "蛋類" -> 14;
            case "乳製品" -> 7;
            case "調味料" -> 365;
            default -> 7;
        };
    }
}