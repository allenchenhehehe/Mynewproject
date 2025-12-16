package com.myfridge.myfridge.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient { 
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id; 
    private String ingredientName;   
    private String category;
    private Integer shelfLifeDays;
}
