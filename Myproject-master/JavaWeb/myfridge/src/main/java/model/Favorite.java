package model;

import java.sql.Timestamp;
import java.time.LocalDate;

public class Favorite {
	//DB基本欄位
	private Integer id;
	private Integer userId;
	private Integer recipeId;
	private Timestamp savedAt;
	//前端顯示要用
	private String title;
	private String description;
	private String imageUrl;
	private Integer cookingTime;
	private Integer difficulty;
	
	public Favorite() {}
	
	public Favorite(Integer id, Integer userId, Integer recipeId, Timestamp savedAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.recipeId = recipeId;
		this.savedAt = savedAt;
	}
	
	public Favorite(Integer id, Integer userId, Integer recipeId, Timestamp savedAt, String title, String description,
			String imageUrl, Integer cookingTime, Integer difficulty) {
		super();
		this.id = id;
		this.userId = userId;
		this.recipeId = recipeId;
		this.savedAt = savedAt;
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
		this.cookingTime = cookingTime;
		this.difficulty = difficulty;
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

	public Timestamp getSavedAt() {
		return savedAt;
	}

	public void setSavedAt(Timestamp savedAt) {
		this.savedAt = savedAt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getCookingTime() {
		return cookingTime;
	}

	public void setCookingTime(Integer cookingTime) {
		this.cookingTime = cookingTime;
	}

	public Integer getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Integer difficulty) {
		this.difficulty = difficulty;
	}
	
}
