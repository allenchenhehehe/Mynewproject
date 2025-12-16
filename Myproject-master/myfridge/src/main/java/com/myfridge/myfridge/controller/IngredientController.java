package com.myfridge.myfridge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.Ingredient;
import com.myfridge.myfridge.service.IngredientService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    
    private final IngredientService ingredientService;
    
    // 對應你的 doGet (pathInfo == null 或 "/")
    // GET /api/ingredients
    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> list = ingredientService.getAllIngre();
        return ResponseEntity.ok(list);
    }
    
    // 對應你的 doGet (pathInfo 有值)
    // GET /api/ingredients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable Integer id) {
        Ingredient ingre = ingredientService.getIngreById(id);
        if (ingre != null) {
            return ResponseEntity.ok(ingre);
        } else {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("找不到此食材"));
        }
    }
    
    // 錯誤回應物件
    record ErrorResponse(String error) {}
}