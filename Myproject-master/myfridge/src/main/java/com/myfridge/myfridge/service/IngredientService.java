package com.myfridge.myfridge.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myfridge.myfridge.entity.Ingredient;
import com.myfridge.myfridge.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientService {
    
    private final IngredientRepository ingredientRepository;
    
    // ✅ 對應你的 getAllIngre()
    public List<Ingredient> getAllIngre() {
        return StreamSupport.stream(ingredientRepository.findAll().spliterator(), false)
                .toList();
    }
    
    // ✅ 對應你的 getIngreById()
    public Ingredient getIngreById(Integer id) {
        return ingredientRepository.findById(id).orElse(null);
    }
    
    // ✅ 對應你的 getIngreByName()
    public Ingredient getIngreByName(String name) {
        return ingredientRepository.findByIngredientName(name).orElse(null);
    }
    
    // ✅ 對應你的 addIngre()
    @Transactional
    public Ingredient addIngre(Ingredient ingre) {
        return ingredientRepository.save(ingre);
    }
}