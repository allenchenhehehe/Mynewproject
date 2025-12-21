package com.myfridge.myfridge.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.entity.RecipeIngredient;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    // ==================== SQL 語句 ====================
    
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
    
    // ✅ 新增：查詢所有食譜（包含食材）- 使用 JOIN 一次查詢
    private static final String SELECT_ALL_WITH_INGREDIENTS = 
        "SELECT " +
        "r.id, r.userId, r.title, r.description, r.imageUrl, r.cookingTime, " +
        "r.difficulty, r.step, r.isPublic, " +
        "ri.ingredientId, ri.amount, ri.unit, " +
        "i.ingredientName, i.category " +
        "FROM recipe r " +
        "LEFT JOIN recipeingredient ri ON r.id = ri.recipeId " +
        "LEFT JOIN ingredient i ON ri.ingredientId = i.id " +
        "WHERE r.isPublic = true " +
        "ORDER BY r.id";
    
    // ✅ 新增：查詢單一食譜（包含食材）- 使用 JOIN
    private static final String SELECT_BY_ID_WITH_INGREDIENTS = 
        "SELECT " +
        "r.id, r.userId, r.title, r.description, r.imageUrl, r.cookingTime, " +
        "r.difficulty, r.step, r.isPublic, " +
        "ri.ingredientId, ri.amount, ri.unit, " +
        "i.ingredientName, i.category " +
        "FROM recipe r " +
        "LEFT JOIN recipeingredient ri ON r.id = ri.recipeId " +
        "LEFT JOIN ingredient i ON ri.ingredientId = i.id " +
        "WHERE r.id = ?";
    
    private static final String SEARCH_BY_KEYWORD = 
        "SELECT id, userId, title, description, imageUrl, cookingTime, " +
        "difficulty, step, isPublic " +
        "FROM recipe " +
        "WHERE isPublic = true " +
        "AND (title LIKE ? OR description LIKE ?) " +
        "ORDER BY id";
    
    private static final String DELETE_RECIPE = "DELETE FROM recipe WHERE id = ?";
    private static final String DELETE_INGREDIENTS = "DELETE FROM RecipeIngredient WHERE recipeId = ?";
    
    // ✅ 修改：INSERT 加上 step 欄位
    private static final String INSERT = 
        "INSERT INTO Recipe (title, description, imageUrl, cookingTime, difficulty, step) " +
        "VALUES (?, ?, ?, ?, ?, ?)";
    
    // ==================== RowMapper ====================
    
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
        // ✅ 移除 createdAt
        return recipe;
    };
    
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
    
    // ==================== Helper 方法 ====================
    
    private List<RecipeIngredient> getIngredientsByRecipeId(Integer recipeId) {
        return jdbcTemplate.query(SELECT_INGREDIENTS_BY_RECIPE_ID, ingredientRowMapper, recipeId);
    }
    
    // ==================== 查詢方法 ====================
    
    /**
     * ✅ 查詢所有公開食譜（包含食材）- 使用 JOIN 優化
     */
    public List<Recipe> findAllWithIngredients() {
    return jdbcTemplate.query(SELECT_ALL_WITH_INGREDIENTS, rs -> {
        Map<Integer, Recipe> recipeMap = new LinkedHashMap<>();
        
        try {
            while (rs.next()) {
                Integer recipeId = rs.getInt("id");
                
                // 如果這個食譜還沒加入 Map，建立新的 Recipe
                Recipe recipe = recipeMap.computeIfAbsent(recipeId, id -> {
                    try {
                        Recipe r = new Recipe();
                        r.setId(recipeId);
                        r.setUserId(rs.getInt("userId"));
                        r.setTitle(rs.getString("title"));
                        r.setDescription(rs.getString("description"));
                        r.setImageUrl(rs.getString("imageUrl"));
                        r.setCookingTime(rs.getInt("cookingTime"));
                        r.setDifficulty(rs.getInt("difficulty"));
                        r.setStep(rs.getString("step"));
                        r.setIsPublic(rs.getBoolean("isPublic"));
                        
                        r.setIngredients(new ArrayList<>());
                        return r;
                    } catch (Exception e) {
                        throw new RuntimeException("解析食譜資料失敗", e);
                    }
                });
                
                // 如果有食材，加入到 ingredients 列表
                Integer ingredientId = (Integer) rs.getObject("ingredientId");
                if (ingredientId != null) {
                    RecipeIngredient ing = new RecipeIngredient();
                    ing.setIngredientId(ingredientId);
                    ing.setIngredientName(rs.getString("ingredientName"));
                    ing.setAmount(rs.getDouble("amount"));
                    ing.setUnit(rs.getString("unit"));
                    ing.setCategory(rs.getString("category"));
                    
                    recipe.getIngredients().add(ing);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("查詢食譜失敗", e);
        }
        
        return new ArrayList<>(recipeMap.values());
    });
}
    
    /**
     * ✅ 查詢單一食譜（包含食材）- 使用 JOIN 優化
     */
    public Recipe findByIdWithIngredients(Integer id) {
        List<Recipe> recipes = jdbcTemplate.query(SELECT_BY_ID_WITH_INGREDIENTS, rs -> {
            Recipe recipe = null;
            List<RecipeIngredient> ingredients = new ArrayList<>();
            
            while (rs.next()) {
                if (recipe == null) {
                    recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setUserId(rs.getInt("userId"));
                    recipe.setTitle(rs.getString("title"));
                    recipe.setDescription(rs.getString("description"));
                    recipe.setImageUrl(rs.getString("imageUrl"));
                    recipe.setCookingTime(rs.getInt("cookingTime"));
                    recipe.setDifficulty(rs.getInt("difficulty"));
                    recipe.setStep(rs.getString("step"));
                    recipe.setIsPublic(rs.getBoolean("isPublic"));
                    // ✅ 移除 createdAt
                }
                
                Integer ingredientId = (Integer) rs.getObject("ingredientId");
                if (ingredientId != null) {
                    RecipeIngredient ing = new RecipeIngredient();
                    ing.setIngredientId(ingredientId);
                    ing.setIngredientName(rs.getString("ingredientName"));
                    ing.setAmount(rs.getDouble("amount"));
                    ing.setUnit(rs.getString("unit"));
                    ing.setCategory(rs.getString("category"));
                    
                    ingredients.add(ing);
                }
            }
            
            if (recipe != null) {
                recipe.setIngredients(ingredients);
                return Collections.singletonList(recipe);
            }
            return Collections.emptyList();
        }, id);
        
        return recipes.isEmpty() ? null : recipes.get(0);
    }
    
    // ✅ 保留舊方法（相容性）
    public List<Recipe> findAll() {
        List<Recipe> recipes = jdbcTemplate.query(SELECT_ALL, recipeRowMapper);
        recipes.forEach(recipe -> recipe.setIngredients(getIngredientsByRecipeId(recipe.getId())));
        return recipes;
    }
    
    public Recipe findById(Integer id) {
        List<Recipe> recipes = jdbcTemplate.query(SELECT_BY_ID, recipeRowMapper, id);
        if (recipes.isEmpty()) {
            return null;
        }
        Recipe recipe = recipes.get(0);
        recipe.setIngredients(getIngredientsByRecipeId(id));
        return recipe;
    }
    
    public List<Recipe> findByKeyword(String keyword) {
        String pattern = "%" + keyword + "%";
        List<Recipe> recipes = jdbcTemplate.query(SEARCH_BY_KEYWORD, recipeRowMapper, pattern, pattern);
        recipes.forEach(recipe -> recipe.setIngredients(getIngredientsByRecipeId(recipe.getId())));
        return recipes;
    }
    
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
        recipes.forEach(recipe -> recipe.setIngredients(getIngredientsByRecipeId(recipe.getId())));
        return recipes;
    }
    
    // ==================== 修改/刪除方法 ====================
    
    @Transactional
    public int delete(Integer recipeId) {
        jdbcTemplate.update(DELETE_INGREDIENTS, recipeId);
        return jdbcTemplate.update(DELETE_RECIPE, recipeId);
    }

    /**
     * ✅ 插入食譜（加上 step 欄位）
     */
    public Recipe insert(Recipe recipe) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, recipe.getTitle());
            ps.setString(2, recipe.getDescription());
            ps.setString(3, recipe.getImageUrl());
            ps.setInt(4, recipe.getCookingTime());
            ps.setInt(5, recipe.getDifficulty());
            ps.setString(6, recipe.getStep());  // ✅ 新增 step
            return ps;
        }, keyHolder);
        
        recipe.setId(keyHolder.getKey().intValue());
        return findById(recipe.getId());
    }
}