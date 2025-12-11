package model;

import java.sql.Timestamp;

public class Comment {
	private Integer id;
	private Integer userId;
	private Integer recipeId;
	private Integer rating;
	private String text;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private String userName;
	private String recipeTitle;
	
	public Comment() {}
	
	
	public Comment(Integer id, Integer userId, Integer recipeId, Integer rating, String text, Timestamp createdAt,
			Timestamp updatedAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.recipeId = recipeId;
		this.rating = rating;
		this.text = text;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Comment(Integer id, Integer userId, Integer recipeId, Integer rating, String text, Timestamp createdAt,
			Timestamp updatedAt, String userName, String recipeTitle) {
		super();
		this.id = id;
		this.userId = userId;
		this.recipeId = recipeId;
		this.rating = rating;
		this.text = text;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.userName = userName;
		this.recipeTitle = recipeTitle;
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
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRecipeTitle() {
		return recipeTitle;
	}
	public void setRecipeTitle(String recipeTitle) {
		this.recipeTitle = recipeTitle;
	}
	
}
