package com.myfridge.myfridge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "ingredients")
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer id;
    @Column(name = "ingredient_name",nullable = false, unique = true, length = 20)
    private String ingredientName;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;
    public enum Category{
        vegetable,
        fruit, 
        meat,     
        dairy,      
        seasoning,   
        oil,       
        seafood,  
        egg,      
		bean,         
        other
    };
}
