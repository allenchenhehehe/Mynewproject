package service;

import java.util.*;

import dao.IngredientDAO;
import model.Ingredient;

public class IngredientService {
	private IngredientDAO ingredientDAO;
	
	public IngredientService() {
		this.ingredientDAO = new IngredientDAO();
	}
	public List<Ingredient> getAllIngre(){
		return ingredientDAO.findAll();
	}
	public Ingredient getIngreById(Integer id) {
		return ingredientDAO.findById(id);
	}
	public Ingredient getIngreByName(String name) {
		return ingredientDAO.findByName(name);
	}
	public Ingredient addIngre(Ingredient ingre) {
		return ingredientDAO.insert(ingre);
	}
}
