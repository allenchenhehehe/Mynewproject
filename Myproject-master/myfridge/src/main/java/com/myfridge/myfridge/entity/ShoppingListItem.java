package com.myfridge.myfridge.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListItem {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
	private Integer userId;
	private Integer recipeId;
	private String recipeName;
	private Integer ingredientId;
	private String ingredientName;
	private Double amount;
	private String unit;
    private String category; 
    private Boolean isPurchased;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

}
