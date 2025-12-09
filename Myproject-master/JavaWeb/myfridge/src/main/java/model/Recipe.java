package model;

import java.util.List;

public class Recipe {
	private Integer id;
	private Integer userId;
	private String title;
	private String description;
	private String imageUrl;
	private Integer cookingTime;
	private Integer difficulty;
	private String step;
	private Boolean isPublic;  
	private List<RecipeIngredient> ingredients;

	
	public Recipe() {}
	public Recipe(Integer id, Integer userId, String title, String description, String imageUrl, Integer cookingTime,
			Integer difficulty, String step, Boolean isPublic) {
		super();
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
		this.cookingTime = cookingTime;
		this.difficulty = difficulty;
		this.step = step;
		this.isPublic = isPublic;
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
	public void setImageUrl(String imageURL) {
		this.imageUrl = imageURL;
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
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public Boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}
	public List<RecipeIngredient> getIngredients() {
		return ingredients;
	}
	public void setIngredients(List<RecipeIngredient> ingredients) {
		this.ingredients = ingredients;
	}
	
}
