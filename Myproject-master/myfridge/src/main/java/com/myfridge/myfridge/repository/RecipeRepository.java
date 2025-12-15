package com.myfridge.myfridge.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.entity.RecipeIngredient;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    // SQL 語句
    private static final String SELECT_ALL_PUBLIC = 
        "SELECT id, userId, title, description, imageUrl, cookingTime, " +
        "difficulty, step, isPublic " +
        "FROM recipe " +
        "WHERE isPublic = true " +
        "ORDER BY id";
    
    private static final String SELECT_BY_ID = 
        "SELECT id, userId, title, description, imageUrl, cookingTime, " +
        "difficulty, step, isPublic " +
        "FROM recipe " +
        "WHERE id = ?";
    
    private static final String SELECT_INGREDIENTS_BY_RECIPE_ID = 
        "SELECT ri.id, ri.recipeId, ri.ingredientId, ri.amount, ri.unit, " +
        "i.ingredientName, i.category " +
        "FROM recipeingredient ri " +
        "JOIN ingredient i ON ri.ingredientId = i.id " +
        "WHERE ri.recipeId = ?";
    
    private static final String SEARCH_BY_KEYWORD = 
        "SELECT id, userId, title, description, imageUrl, cookingTime, " +
        "difficulty, step, isPublic " +
        "FROM recipe " +
        "WHERE isPublic = true " +
        "AND (title LIKE ? OR description LIKE ?) " +
        "ORDER BY id";
    
    private static final String DELETE_RECIPE = "DELETE FROM recipe WHERE id = ?";
    
    // RowMapper for Recipe
    private final RowMapper<Recipe> recipeRowMapper = (rs, rowNum) -> {
        Recipe recipe = new Recipe();
        recipe.setId(rs.getInt("id"));
        recipe.setUserId(rs.getInt("userId"));
        recipe.setTitle(rs.getString("title"));
        recipe.setDescription(rs.getString("description"));
        recipe.setImageUrl(rs.getString("imageUrl"));
        recipe.setCookingTime(rs.getInt("cookingTime"));
        recipe.setDifficulty(rs.getInt("difficulty"));
        recipe.setStep(rs.getString("step"));
        recipe.setIsPublic(rs.getBoolean("isPublic"));
        return recipe;
    };
    
    // RowMapper for RecipeIngredient
    private final RowMapper<RecipeIngredient> ingredientRowMapper = (rs, rowNum) -> 
        new RecipeIngredient(
            rs.getInt("id"),
            rs.getInt("recipeId"),
            rs.getInt("ingredientId"),
            rs.getDouble("amount"),
            rs.getString("unit"),
            rs.getString("ingredientName"),
            rs.getString("category")
        );
    
    // ✅ 載入食材
    private List<RecipeIngredient> getIngredients(Integer recipeId) {
        return jdbcTemplate.query(SELECT_INGREDIENTS_BY_RECIPE_ID, ingredientRowMapper, recipeId);
    }
    
    // ✅ 查詢所有公開食譜（含食材）
    public List<Recipe> findAll() {
        List<Recipe> recipes = jdbcTemplate.query(SELECT_ALL_PUBLIC, recipeRowMapper);
        recipes.forEach(recipe -> recipe.setIngredients(getIngredients(recipe.getId())));
        return recipes;
    }
    
    // ✅ 根據 ID 查詢（含食材）
    public Recipe findById(Integer id) {
        List<Recipe> recipes = jdbcTemplate.query(SELECT_BY_ID, recipeRowMapper, id);
        if (recipes.isEmpty()) {
            return null;
        }
        Recipe recipe = recipes.get(0);
        recipe.setIngredients(getIngredients(id));
        return recipe;
    }
    
    // ✅ 關鍵字搜尋（含食材）
    public List<Recipe> findByKeyword(String keyword) {
        String pattern = "%" + keyword + "%";
        List<Recipe> recipes = jdbcTemplate.query(SEARCH_BY_KEYWORD, recipeRowMapper, pattern, pattern);
        recipes.forEach(recipe -> recipe.setIngredients(getIngredients(recipe.getId())));
        return recipes;
    }
    
    // ✅ 動態過濾查詢（含食材）
    public List<Recipe> filter(Integer difficulty, Integer minCookingTime, Integer maxCookingTime) {
        StringBuilder sql = new StringBuilder(
            "SELECT id, userId, title, description, imageUrl, cookingTime, " +
            "difficulty, step, isPublic " +
            "FROM recipe " +
            "WHERE isPublic = true "
        );
        
        List<Object> params = new ArrayList<>();
        
        if (difficulty != null) {
            sql.append("AND difficulty = ? ");
            params.add(difficulty);
        }
        
        if (minCookingTime != null) {
            sql.append("AND cookingTime >= ? ");
            params.add(minCookingTime);
        }
        
        if (maxCookingTime != null) {
            sql.append("AND cookingTime <= ? ");
            params.add(maxCookingTime);
        }
        
        sql.append("ORDER BY id");
        
        List<Recipe> recipes = jdbcTemplate.query(sql.toString(), recipeRowMapper, params.toArray());
        recipes.forEach(recipe -> recipe.setIngredients(getIngredients(recipe.getId())));
        return recipes;
    }
    
    // ✅ 刪除食譜（資料庫會級聯刪除 RecipeIngredient）
    @Transactional
    public int delete(Integer recipeId) {
        return jdbcTemplate.update(DELETE_RECIPE, recipeId);
    }
}