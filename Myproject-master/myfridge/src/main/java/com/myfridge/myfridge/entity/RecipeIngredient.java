package com.myfridge.myfridge.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("recipeingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {
    
    @Id
    private Integer id;
    
    @Column("recipeId")
    private Integer recipeId;
    
    @Column("ingredientId")
    private Integer ingredientId;
    
    @Column("ingredientName")
    private String ingredientName;
    
    private Double amount;
    private String unit;
    private String category;
    
    // ✅ 如果需要自訂建構子，Lombok 無法自動生成多個建構子
    // 需要手動加上你的 5 參數建構子
    public RecipeIngredient(Integer id, Integer recipeId, Integer ingredientId,
                            Double amount, String unit, String string, String string2) {
        this.id = id;
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.amount = amount;
        this.unit = unit;
    }
}