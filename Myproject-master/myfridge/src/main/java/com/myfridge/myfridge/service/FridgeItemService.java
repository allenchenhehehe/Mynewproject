package com.myfridge.myfridge.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myfridge.myfridge.entity.FridgeItem;
import com.myfridge.myfridge.repository.FridgeItemRepository;
import com.myfridge.myfridge.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FridgeItemService {

    private final FridgeItemRepository fridgeItemRepository;
    private final IngredientRepository ingredientRepository;

    public Integer getDefaultShelfLife(String category) {
        return switch (category.toLowerCase()) {
            case "vegetable" -> 7;
            case "fruit" -> 7;
            case "meat" -> 3;
            case "bean" -> 180;
            case "egg" -> 30;
            case "dairy" -> 7;
            case "seafood" -> 2;
            case "seasoning" -> 365;
            case "oil" -> 365;
            case "other" -> 7;
            default -> 7;
        };
	}

    public List<FridgeItem> getAllItems(Integer userId){
		return fridgeItemRepository.findByUserId(userId);
	}

    public FridgeItem addToFridge(Integer userId,String ingredientName, String category
			,Double amount,String unit, LocalDate purchasedDate, LocalDate expiredDate) {

		Integer ingredientId = ingredientRepository.findOrCreate(ingredientName, category);

		FridgeItem fridgeItem = new FridgeItem();
		fridgeItem.setUserId(userId);
		fridgeItem.setIngredientId(ingredientId);
		fridgeItem.setAmount(amount);
		fridgeItem.setUnit(unit);
		fridgeItem.setPurchasedDate(purchasedDate);
		fridgeItem.setExpiredDate(expiredDate);
		FridgeItem saved = fridgeItemRepository.insert(fridgeItem);
		saved.setIngredientName(ingredientName);
		saved.setCategory(category);
		return saved;
	}

    public FridgeItem updateItem(Integer userId,Integer id, Double amount, String unit, LocalDate purchasedDate, LocalDate expiredDate) {

		FridgeItem exist = fridgeItemRepository.findById(id);

		if(exist==null) {
			throw new RuntimeException("找不到此食材");
		}

		if(!exist.getUserId().equals(userId)) {
			 throw new RuntimeException("無權修改此食材");
		}

		FridgeItem item = new FridgeItem();
		item.setUserId(userId);
		item.setId(id);
		item.setAmount(amount);
		item.setUnit(unit);
		item.setPurchasedDate(purchasedDate);
		item.setExpiredDate(expiredDate);
		fridgeItemRepository.update(item);
		return fridgeItemRepository.findById(id);
	}

    public boolean deleteItem(Integer userId, Integer itemid) {

		FridgeItem fridgeItem = fridgeItemRepository.findById(itemid);

		if(fridgeItem==null) {
			 throw new RuntimeException("找不到此食材");
		}

		if(!fridgeItem.getUserId().equals(userId)) {
			 throw new RuntimeException("無權刪除此食材");
		}
        
		return fridgeItemRepository.delete(userId,itemid);
	}

}
