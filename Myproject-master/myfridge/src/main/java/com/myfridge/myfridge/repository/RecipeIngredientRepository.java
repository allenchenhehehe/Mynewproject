package com.myfridge.myfridge.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.myfridge.myfridge.entity.RecipeIngredient;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeIngredientRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    // ==================== SQL 語句 ====================
    
    private static final String INSERT = 
        "INSERT INTO RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES (?, ?, ?, ?)";
    
    private static final String DELETE_BY_RECIPE_ID = 
        "DELETE FROM RecipeIngredient WHERE recipeId = ?";
    
    private static final String DELETE_BY_INGREDIENT_ID = 
        "DELETE FROM RecipeIngredient WHERE ingredientId = ?";
    
    // ==================== CRUD 方法 ====================
    
    /**
     * 插入食譜食材關聯
     */
    public void insert(RecipeIngredient recipeIngredient) {
        jdbcTemplate.update(INSERT,
            recipeIngredient.getRecipeId(),
            recipeIngredient.getIngredientId(),
            recipeIngredient.getAmount(),
            recipeIngredient.getUnit()
        );
    }
    
    /**
     * 刪除某個食譜的所有食材關聯
     * （用於刪除食譜時的級聯刪除）
     */
    public int deleteByRecipeId(Integer recipeId) {
        return jdbcTemplate.update(DELETE_BY_RECIPE_ID, recipeId);
    }
    
    /**
     * 刪除某個食材的所有關聯
     * （用於刪除食材時的級聯刪除）
     */
    public int deleteByIngredientId(Integer ingredientId) {
        return jdbcTemplate.update(DELETE_BY_INGREDIENT_ID, ingredientId);
    }
}