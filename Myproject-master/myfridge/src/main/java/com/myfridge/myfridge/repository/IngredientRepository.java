package com.myfridge.myfridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myfridge.myfridge.entity.Ingredient;

@Repository
public interface  IngredientRepository extends JpaRepository<Ingredient, Integer> {
    
}
