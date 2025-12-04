package model;

public class Ingredient {
	private Integer id;
	private String ingredientname;
	private String category;
	private Integer shelflifedays;
	
	public Ingredient() {}
	public Ingredient(Integer id, String ingredientname, String category, Integer shelflifedays) {
		this.id = id;
		this.ingredientname = ingredientname;
		this.category = category;
		this.shelflifedays = shelflifedays;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIngredientName() {
		return ingredientname;
	}
	public void setIngredientName(String ingredientname) {
		this.ingredientname = ingredientname;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Integer getShelfLifeDays() {
		return shelflifedays;
	}
	public void setShelfLifeDays(Integer shelflifedays) {
		this.shelflifedays = shelflifedays;
	}
 
	
}
