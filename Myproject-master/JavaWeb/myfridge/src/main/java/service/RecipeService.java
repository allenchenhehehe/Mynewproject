package service;

import java.util.List;

import dao.RecipeDAO;
import model.FridgeItem;
import model.Recipe;

public class RecipeService {
	private RecipeDAO recipeDao;
	public RecipeService() {
		this.recipeDao = new RecipeDAO();
	}
	//vue OnMounted掛載
	public List<Recipe> getAllRecipes(){
		try {
            return recipeDao.findAll();
        } catch (Exception e) {
            System.err.println("查詢所有食譜失敗: " + e.getMessage());
            throw new RuntimeException("查詢食譜失敗", e);
        }
	}
	//vue OnMounted掛載
	public Recipe getRecipeDetail(Integer recipeId){
		if (recipeId == null) {
            throw new IllegalArgumentException("食譜 ID 不可為空");
        }
        
        try {
            Recipe recipe = recipeDao.findById(recipeId);
            
            if (recipe == null) {
                System.out.println("找不到食譜 ID: " + recipeId);
            }
            
            return recipe;
        } catch (Exception e) {
            System.err.println("查詢食譜詳情失敗 (ID: " + recipeId + "): " + e.getMessage());
            throw new RuntimeException("查詢食譜詳情失敗", e);
        }
	}
	
	public List<Recipe> searchRecipes(String keyword){
		if(keyword == null || keyword.trim().isEmpty()) {
			return recipeDao.findAll(); 
		}
		try {
            return recipeDao.findByKeyword(keyword.trim());
        } catch (Exception e) {
            System.err.println("搜尋食譜失敗 (關鍵字: " + keyword + "): " + e.getMessage());
            throw new RuntimeException("搜尋食譜失敗", e);
        }
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
		try {
            // 如果沒有任何篩選條件，回傳所有食譜
            if (difficulty == null && minCookingTime == null && maxCookingTime == null) {
                return getAllRecipes();
            }
            
            return recipeDao.filter(difficulty, minCookingTime, maxCookingTime);
        } catch (Exception e) {
            System.err.println("篩選食譜失敗: " + e.getMessage());
            throw new RuntimeException("篩選食譜失敗", e);
        }
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
	
	
}
