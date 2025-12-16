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
    private static final String SELECT_ALL = 
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

    private static final String DELETE_INGREDIENTS = "DELETE FROM RecipeIngredient WHERE recipeId = ?";
    
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
    private final RowMapper<RecipeIngredient> ingredientRowMapper = (rs, rowNum) -> {
        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setId(rs.getInt("id"));
        ingredient.setRecipeId(rs.getInt("recipeId"));
        ingredient.setIngredientId(rs.getInt("ingredientId"));
        ingredient.setAmount(rs.getDouble("amount"));
        ingredient.setUnit(rs.getString("unit"));
        ingredient.setIngredientName(rs.getString("ingredientName"));
        ingredient.setCategory(rs.getString("category"));
        return ingredient;
    };
    
    //載入食材
    private List<RecipeIngredient> getIngredientsByRecipeId(Integer recipeId) {
        return jdbcTemplate.query(SELECT_INGREDIENTS_BY_RECIPE_ID, ingredientRowMapper, recipeId);
    }
    
    //查詢所有公開食譜（含食材）
    public List<Recipe> findAll() {
        List<Recipe> recipes = jdbcTemplate.query(SELECT_ALL, recipeRowMapper);
        //為每個食譜加入所需食材
        recipes.forEach(recipe -> recipe.setIngredients(getIngredientsByRecipeId(recipe.getId())));
        return recipes;
    }
    
    //根據 ID 查詢（含食材）
    public Recipe findById(Integer id) {
        List<Recipe> recipes = jdbcTemplate.query(SELECT_BY_ID, recipeRowMapper, id);
        if (recipes.isEmpty()) {
            return null;
        }
        //從List抓第一個食譜
        Recipe recipe = recipes.get(0);
        //為單個食譜加入所需食材
        recipe.setIngredients(getIngredientsByRecipeId(id));
        return recipe;
    }
    
    //關鍵字搜尋（含食材）
    public List<Recipe> findByKeyword(String keyword) {

        //建立模糊搜尋的模式，% 是SQL的萬用字元（wildcard），%番茄%代表「前後可以有任意字元」
        String pattern = "%" + keyword + "%";

        //執行 SQL 查詢，因為QueryString有兩個?，所以傳兩個pattern
        List<Recipe> recipes = jdbcTemplate.query(SEARCH_BY_KEYWORD, recipeRowMapper, pattern, pattern);

        //為每個食譜載入食材
        recipes.forEach(recipe -> recipe.setIngredients(getIngredientsByRecipeId(recipe.getId())));
        return recipes;
    }
    
    //動態過濾查詢（含食材）
    public List<Recipe> filter(Integer difficulty, Integer minCookingTime, Integer maxCookingTime) {

        //建立基礎 SQL（用 StringBuilder 動態組合）
        StringBuilder sql = new StringBuilder(
            "SELECT id, userId, title, description, imageUrl, cookingTime, " +
            "difficulty, step, isPublic " +
            "FROM recipe " +
            "WHERE isPublic = true "
        );

        //建立參數列表，存放所有的?
        List<Object> params = new ArrayList<>();
        
        //根據有無傳入參數，動態新增 SQL 條件
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
        
        //加上排序
        sql.append("ORDER BY id");
        
        //執行查詢，把StringBuilder轉成String，把List<Object>轉成Object[]
        //因為query(String sql, RowMapper<T> rowMapper, Object... args)最後是Object，params是List，轉成Obj陣列
        List<Recipe> recipes = jdbcTemplate.query(sql.toString(), recipeRowMapper, params.toArray());
        recipes.forEach(recipe -> recipe.setIngredients(getIngredientsByRecipeId(recipe.getId())));
        return recipes;
    }
    
    //刪除食譜（資料庫會級聯刪除 RecipeIngredient）
    @Transactional
    public int delete(Integer recipeId) {
        jdbcTemplate.update(DELETE_INGREDIENTS, recipeId);
        return jdbcTemplate.update(DELETE_RECIPE, recipeId);
    }
}