package com.myfridge.myfridge.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.myfridge.myfridge.entity.Ingredient;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {
    
    // ✅ 根據名稱查詢（對應你的 findByName）
    // Spring Data JDBC 會自動生成：SELECT * FROM ingredient WHERE ingredientName = ?
    Optional<Ingredient> findByIngredientName(String ingredientName);
    
    // ✅ 自動繼承的方法（對應你的其他方法）：
    // - findAll() → 對應你的 findAll()
    // - findById(Integer id) → 對應你的 findById()
    // - save(Ingredient) → 對應你的 insert()
}