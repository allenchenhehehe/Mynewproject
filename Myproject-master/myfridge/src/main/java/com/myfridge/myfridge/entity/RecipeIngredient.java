package com.myfridge.myfridge.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    private Integer recipeId;
    private Integer ingredientId;
    private String ingredientName;
    private Double amount;
    private String unit;
    private String category;
    
    // 如果需要自訂建構子，Lombok 無法自動生成多個建構子，需要手動加上你的 5 參數建構子
    public RecipeIngredient(Integer id, Integer recipeId, Integer ingredientId,
                            Double amount, String unit, String string, String string2) {
        this.id = id;
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.amount = amount;
        this.unit = unit;
    }
}