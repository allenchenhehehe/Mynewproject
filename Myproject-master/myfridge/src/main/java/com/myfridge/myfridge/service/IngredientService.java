package com.myfridge.myfridge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myfridge.myfridge.entity.Ingredient;
import com.myfridge.myfridge.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientService {
    
    private final IngredientRepository ingredientRepository;
    
    // 對應你的 getAllIngre()
    public List<Ingredient> getAllIngre() {
        return ingredientRepository.findAll();
    }
    
    //對應你的 getIngreById()
    public Ingredient getIngreById(Integer id) {
        return ingredientRepository.findById(id);
    }
    
    //對應你的 getIngreByName()
    public Ingredient getIngreByName(String name) {
        return ingredientRepository.findByName(name);
    }   
    
    //對應你的 addIngre()
    public Ingredient addIngre(Ingredient ingre) {
        return ingredientRepository.insert(ingre);
    }

    public Integer findOrCreate(String ingredientName, String category) {
        return ingredientRepository.findOrCreate(ingredientName, category); 
    }
}