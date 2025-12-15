package com.myfridge.myfridge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {
    
    @Id
    private Integer id;
    
    @Column("ingredientName")
    private String ingredientName;
    
    private String category;
    
    @Column("shelfLifeDays")
    private Integer shelfLifeDays;
}
