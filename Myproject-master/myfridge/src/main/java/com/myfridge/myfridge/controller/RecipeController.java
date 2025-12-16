package com.myfridge.myfridge.controller;

import java.util.List;

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
    
    //查詢所有食譜，這個方法會回傳一個包含食譜列表的 HTTP 回應
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(){
        List<Recipe> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Integer id){

        Recipe recipe = recipeService.getRecipeDetail(id);

        if (recipe == null) {
            return ResponseEntity.status(404).body(new ErrorResponse("找不到食譜 ID: " + id));
        }
    
        return ResponseEntity.ok(recipe);
    }

    // required = false的意思：這個參數可以不傳
    @GetMapping ("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam(required = false) String keyword){
        List<Recipe> recipes = recipeService.searchRecipes(keyword);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterRecipes( @RequestParam(required = false) Integer difficulty,
        @RequestParam(required = false) Integer minCookingTime,
        @RequestParam(required = false) Integer maxCookingTime){
        
        try{
            List<Recipe> recipes = recipeService.filterRecipes(difficulty, minCookingTime, maxCookingTime);
            return ResponseEntity.ok(recipes);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }       
    }

    record ErrorResponse(String error) {}
}
