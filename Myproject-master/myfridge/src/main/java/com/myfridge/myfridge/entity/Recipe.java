package com.myfridge.myfridge.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("recipe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    
    @Id
    private Integer id;
    
    @Column("userId")
    private Integer userId;
    
    private String title;
    private String description;
    
    @Column("imageUrl")
    private String imageUrl;
    
    @Column("cookingTime")
    private Integer cookingTime;
    
    private Integer difficulty;
    private String step;
    
    @Column("isPublic")
    private Boolean isPublic;
    
    @MappedCollection(idColumn = "recipeId")
    private List<RecipeIngredient> ingredients;
}