package service;

import java.time.LocalDate;
import java.util.List;

import dao.FridgeItemDAO;
import dao.IngredientDAO;
import model.FridgeItem;
import model.Ingredient;

public class FridgeItemService {
	private IngredientDAO ingredientDAO;
	private FridgeItemDAO fridgeItemDAO;
	private Integer getDefaultShelfLife(String category) {
		switch(category.toLowerCase()) {
			case "vegetable": return 7;
			case "fruit": return 7;
			case "meat": return 3;
			case "bean": return 180;
			case "egg": return 30;
			case "dairy": return 7;
			case "seafood": return 2;
			case "seasoning": return 365;
			case "oil": return 365;
			case "other": return 7;
			default: return 7;
		}
	}
	public FridgeItemService() {
        this.ingredientDAO = new IngredientDAO();
        this.fridgeItemDAO = new FridgeItemDAO();
    }
	public FridgeItemService(IngredientDAO ingredientDAO, FridgeItemDAO fridgeItemDAO) {
		this.ingredientDAO = ingredientDAO;
		this.fridgeItemDAO = fridgeItemDAO;
	}
	public List<FridgeItem> getAllItems(Integer userId){
		return fridgeItemDAO.findByUserId(userId);
	}
	public FridgeItem addToFridge(Integer userId,String ingredientName, String category
			,Integer amount,String unit, LocalDate purchasedDate, LocalDate expiredDate) {
		Ingredient ingredient = ingredientDAO.findByName(ingredientName);
		if(ingredient == null) {
			ingredient = new Ingredient();
			ingredient.setIngredientName(ingredientName);
			ingredient.setCategory(category);
			ingredient.setShelfLifeDays(getDefaultShelfLife(category));
			ingredient = ingredientDAO.insert(ingredient);
			System.out.println("新食材已加入系統: " + ingredientName);
		}
		FridgeItem fridgeItem = new FridgeItem();
		fridgeItem.setUserId(userId);
		fridgeItem.setIngredientId(ingredient.getId());
		fridgeItem.setAmount(amount);
		fridgeItem.setUnit(unit);
		fridgeItem.setPurchasedDate(purchasedDate);
		fridgeItem.setExpiredDate(expiredDate);
		FridgeItem saved = fridgeItemDAO.insert(fridgeItem);
		saved.setIngredientName(ingredientName);
		saved.setCategory(category);
		return saved;
	}
	public FridgeItem updateItem(Integer id, Integer amount, String unit, LocalDate purchasedDate, LocalDate expiredDate) {
		FridgeItem item = new FridgeItem();
		item.setId(id);
		item.setAmount(amount);
		item.setUnit(unit);
		item.setPurchasedDate(purchasedDate);
		item.setExpiredDate(expiredDate);
		fridgeItemDAO.updateItem(item);
		return fridgeItemDAO.findById(id);
	}
	public boolean deleteItem(Integer id) {
		return fridgeItemDAO.deleteItem(id);
	}
}
