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

    public List<Recipe> getAllRecipes(){
            return recipeRepository.findAll();
	}

    public Recipe getRecipeDetail(Integer recipeId){

		if (recipeId == null) {
            throw new IllegalArgumentException("食譜 ID 不可為空");
        }
    
        return recipeRepository.findById(recipeId);  

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

        // 如果沒有任何篩選條件，回傳所有食譜
        if (difficulty == null && minCookingTime == null && maxCookingTime == null) {
            return getAllRecipes();
        }
        
        return recipeRepository.filter(difficulty, minCookingTime, maxCookingTime);
  
	}

    //filterRecipe的輔助方法
    public List<Recipe> getRecipesByDifficulty(Integer difficulty) {
        return filterRecipes(difficulty, null, null);
    }
	public List<Recipe> getQuickRecipes(Integer maxMinutes) {
        return filterRecipes(null, null, maxMinutes);
    }
	public List<Recipe> getSlowRecipes(Integer minMinutes) {
        return filterRecipes(null, minMinutes, null);
	}

    public int deleteRecipe(Integer recipeId) {
        if (recipeId == null) {
            throw new IllegalArgumentException("食譜 ID 不可為空");
        }
        return recipeRepository.delete(recipeId);
    }
}
