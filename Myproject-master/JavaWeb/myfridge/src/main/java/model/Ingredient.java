package model;

public class Ingredient {
	private Integer id;
	private String ingredientName;
	private String category;
	private Integer shelfLifeDays;
	
	public Ingredient() {}
	public Ingredient(Integer id, String ingredientName, String category, Integer shelfLifeDays) {
		this.id = id;
		this.ingredientName = ingredientName;
		this.category = category;
		this.shelfLifeDays = shelfLifeDays;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String ingredientname) {
		this.ingredientName = ingredientname;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Integer getShelfLifeDays() {
		return shelfLifeDays;
	}
	public void setShelfLifeDays(Integer shelflifedays) {
		this.shelfLifeDays = shelflifedays;
	}
 
	
}
