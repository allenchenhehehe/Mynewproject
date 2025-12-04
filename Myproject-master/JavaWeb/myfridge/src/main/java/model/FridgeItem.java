package model;

import java.sql.*;
import java.time.LocalDate;

public class FridgeItem {
	private Integer id;
	private Integer userId;
	private Integer ingredientId;
	private Integer amount;
	private String unit;
	private LocalDate purchasedDate;      
    private LocalDate expiredDate;
    private String ingredientName;  // 從 ingredients 表 JOIN 來的
    private String category; // 從 ingredients 表 JOIN 來的
    
    public FridgeItem() {}
	public FridgeItem(Integer id, Integer userId, Integer ingredientId, Integer amount, String unit,
			LocalDate purchasedDate, LocalDate expiredDate, String ingredientName, String category) {
		this.id = id;
		this.userId = userId;
		this.ingredientId = ingredientId;
		this.amount = amount;
		this.unit = unit;
		this.purchasedDate = purchasedDate;
		this.expiredDate = expiredDate;
		this.ingredientName = ingredientName;
		this.category = category;
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
	public Integer getIngredientId() {
		return ingredientId;
	}
	public void setIngredientId(Integer ingredientId) {
		this.ingredientId = ingredientId;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public LocalDate getPurchasedDate() {
		return purchasedDate;
	}
	public void setPurchasedDate(LocalDate purchasedDate) {
		this.purchasedDate = purchasedDate;
	}
	public LocalDate getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(LocalDate expiredDate) {
		this.expiredDate = expiredDate;
	}
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
