package com.myfridge.myfridge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.myfridge.myfridge.entity.Ingredient;
import com.myfridge.myfridge.repository.IngredientRepository;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;
    
    public List<Ingredient> findAll(){
        return ingredientRepository.findAll();
    }

    public Ingredient findById(@PathVariable Integer id){
        return ingredientRepository.findById(id).orElse(null);
    }
}
