package com.myfridge.myfridge.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private String imageUrl;
    private Integer cookingTime;  
    private Integer difficulty;
    private String step;
    private Boolean isPublic;
    private List<RecipeIngredient> ingredients;
    
}