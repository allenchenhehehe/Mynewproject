package com.myfridge.myfridge.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.service.RecipeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeService recipeService;
    
    /**
     * 1. GET /api/recipes - 查詢所有食譜
     */
    @GetMapping
    public ResponseEntity<?> getAllRecipes() {
        try {
            List<Recipe> recipes = recipeService.getAllRecipes();
            List<Map<String, Object>> jsonArray = recipes.stream()
                    .map(this::recipeToMap)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(jsonArray);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("伺服器錯誤: " + e.getMessage()));
        }
    }
    
    /**
     * 2. GET /api/recipes/search?keyword=xxx - 搜尋
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchRecipes(@RequestParam(required = false) String keyword) {
        try {
            List<Recipe> recipes;
            if (keyword == null || keyword.trim().isEmpty()) {
                recipes = recipeService.getAllRecipes();
            } else {
                recipes = recipeService.searchRecipes(keyword);
            }
            List<Map<String, Object>> jsonArray = recipes.stream()
                    .map(this::recipeToMap)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(jsonArray);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("伺服器錯誤: " + e.getMessage()));
        }
    }
    
    /**
     * 3. GET /api/recipes/filter?difficulty=3&minCookingTime=60 - 篩選
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterRecipes(
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) Integer minCookingTime,
            @RequestParam(required = false) Integer maxCookingTime) {
        try {
            List<Recipe> recipes = recipeService.filterRecipes(difficulty, minCookingTime, maxCookingTime);
            List<Map<String, Object>> jsonArray = recipes.stream()
                    .map(this::recipeToMap)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(jsonArray);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("伺服器錯誤: " + e.getMessage()));
        }
    }
    
    /**
     * 4. GET /api/recipes/{id} - 查詢單一食譜
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Integer id) {
        try {
            Recipe recipe = recipeService.getRecipeDetail(id);
            if (recipe == null) {
                return ResponseEntity.status(404)
                        .body(new ErrorResponse("找不到食譜 ID: " + id));
            }
            Map<String, Object> json = recipeToMap(recipe);
            return ResponseEntity.ok(json);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("伺服器錯誤: " + e.getMessage()));
        }
    }
    
    /**
     * 將 Recipe 轉換成 Map（對應你的 recipeToJson）
     */
    private Map<String, Object> recipeToMap(Recipe recipe) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", recipe.getId());
        map.put("userId", recipe.getUserId());
        map.put("creatorId", recipe.getUserId() != null ? recipe.getUserId() : 0);  // 前端用 creator_id
        map.put("title", recipe.getTitle());
        map.put("description", recipe.getDescription());
        map.put("imageUrl", recipe.getImageUrl());
        map.put("cookingTime", recipe.getCookingTime());
        map.put("difficulty", recipe.getDifficulty());
        map.put("step", recipe.getStep());
        map.put("isPublic", recipe.getIsPublic());
        
        // 轉換 ingredients
        if (recipe.getIngredients() != null) {
            List<Map<String, Object>> ingredientsList = recipe.getIngredients().stream()
                    .map(ing -> {
                        Map<String, Object> ingMap = new HashMap<>();
                        ingMap.put("ingredientId", ing.getIngredientId());
                        ingMap.put("ingredientName", ing.getIngredientName());
                        ingMap.put("amount", ing.getAmount());  // amount → quantity
                        ingMap.put("unit", ing.getUnit());
                        ingMap.put("category", ing.getCategory());
                        return ingMap;
                    })
                    .collect(Collectors.toList());
            map.put("ingredients", ingredientsList);
        } else {
            map.put("ingredients", List.of());
        }
        
        return map;
    }
    
    // 錯誤回應物件
    record ErrorResponse(String error) {}
}