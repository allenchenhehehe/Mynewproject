package com.myfridge.myfridge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.Ingredient;
import com.myfridge.myfridge.repository.IngredientRepository;



@RestController
@RequestMapping("/api/ingredients")

public class IngredientController {
    @Autowired
    private IngredientRepository ingredientRepository;
    @GetMapping
    public List<Ingredient> getAllIngredients(){
        return ingredientRepository.findAll();
    }
     @GetMapping("/{id}")
    public Ingredient getIngredientBId(@PathVariable Integer id){
        return ingredientRepository.findById(id).orElse(null);
    }
}
