package model;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.google.gson.annotations.SerializedName;

public class ShoppingListItem {
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
    private Timestamp createdAt;
    
    public ShoppingListItem() {}
	public ShoppingListItem(Integer id, Integer userId, Integer recipeId, String recipeName, Integer ingredientId,
			String ingredientName, Double amount, String unit, String category, Boolean isPurchased,
			Timestamp createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.recipeId = recipeId;
		this.recipeName = recipeName;
		this.ingredientId = ingredientId;
		this.ingredientName = ingredientName;
		this.amount = amount;
		this.unit = unit;
		this.category = category;
		this.isPurchased = isPurchased;
		this.createdAt = createdAt;
	}
	public ShoppingListItem(Integer userId, Integer recipeId, String recipeName, Integer ingredientId,
			String ingredientName, Double amount, String unit, String category) {
		super();
		this.userId = userId;
		this.recipeId = recipeId;
		this.recipeName = recipeName;
		this.ingredientId = ingredientId;
		this.ingredientName = ingredientName;
		this.amount = amount;
		this.unit = unit;
		this.category = category;
		this.isPurchased = false;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getRecipeId() {
		return recipeId;
	}
	public void setRecipeId(Integer recipeId) {
		this.recipeId = recipeId;
	}
	public String getRecipeName() {
		return recipeName;
	}
	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
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
	public Boolean getIsPurchased() {
		return isPurchased;
	}
	public void setIsPurchased(Boolean isPurchased) {
		this.isPurchased = isPurchased;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
