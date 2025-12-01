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
}
