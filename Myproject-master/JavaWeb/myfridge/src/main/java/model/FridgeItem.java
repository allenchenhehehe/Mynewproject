package model;

import java.sql.*;

public class FridgeItem {
	private Integer id;
	private Integer userId;
	private Integer ingredientId;
	private Integer quantity;
	private String unit;
	private Date purchasedDate;      
    private Date expiredDate;
    
    public FridgeItem() {}
    public FridgeItem(Integer id,Integer userId,Integer ingredientId,Integer quantity
    		,String unit,Date purchasedDate,Date expiredDate) {
    	this.id = id;
    	this.userId =  userId;
    	this.ingredientId = ingredientId;
    	this.quantity = quantity;
    	this.unit = unit;
    	this.purchasedDate = purchasedDate;
    	this.expiredDate = expiredDate;
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
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Date getPurchasedDate() {
		return purchasedDate;
	}
	public void setPurchasedDate(Date purchasedDate) {
		this.purchasedDate = purchasedDate;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
}
