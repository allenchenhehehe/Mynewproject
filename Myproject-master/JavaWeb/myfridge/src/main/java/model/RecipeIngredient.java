package model;

import java.util.List;

public class RecipeIngredient {
	private Integer id;
	private Integer recipeId;
	private Integer ingredientId;
	private String ingredientName;
	private Double amount;
	private String unit;
	private String category;
	
	
	public RecipeIngredient() {}
	public RecipeIngredient(Integer id, Integer recipeId, Integer ingredientId, 
	         Double amount, String unit) {
		this.id = id;
		this.recipeId = recipeId;
		this.ingredientId = ingredientId;
		this.amount = amount;
		this.unit = unit;
	}
	public RecipeIngredient(Integer id, Integer recipeId, Integer ingredientId, Double amount,
			String unit, String ingredientName, String category) {
		this.id = id;
		this.recipeId = recipeId;
		this.ingredientId = ingredientId;
		this.ingredientName = ingredientName;
		this.amount = amount;
		this.unit = unit;
		this.category = category;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Integer recipeId) {
		this.recipeId = recipeId;
	}

	public Integer getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(Integer ingredientId) {
		this.ingredientId = ingredientId;
	}

	public String getIngredientName() {
		return ingredientName;
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
