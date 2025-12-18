package com.myfridge.myfridge.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myfridge.myfridge.entity.FridgeItem;
import com.myfridge.myfridge.entity.ShoppingListItem;
import com.myfridge.myfridge.repository.FridgeItemRepository;
import com.myfridge.myfridge.repository.IngredientRepository;
import com.myfridge.myfridge.repository.ShoppingListItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShoppingListItemService {

    private final ShoppingListItemRepository shoppingListItemRepository;
    private final FridgeItemRepository fridgeItemRepository;
    private final IngredientRepository ingredientRepository;
    private final FridgeItemService fridgeItemService;

    public List<ShoppingListItem> getShoppingList(Integer userId){

        if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}

        return shoppingListItemRepository.findByUserId(userId);
    }

    public ShoppingListItem addItem(Integer userId, ShoppingListItem item) {

        if (userId == null) {
            throw new IllegalArgumentException("使用者 ID 不可為空");
        }
        
        if (item == null) {
            throw new IllegalArgumentException("購物項目不可為空");
        }
        
        // 驗證必要欄位
        if (item.getIngredientName() == null || item.getIngredientName().trim().isEmpty()) {
            throw new IllegalArgumentException("食材名稱不可為空");
        }
        
        if (item.getAmount() == null || item.getAmount() <= 0) {
            throw new IllegalArgumentException("數量必須大於 0");
        }
        
        if (item.getUnit() == null || item.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("單位不可為空");
        }

        // 如果沒有 ingredientId，查詢或建立，ingredientRepository.findOrCreate會回傳IngredientId，再set進去
        if (item.getIngredientId() == null) {
            Integer ingredientId = ingredientRepository.findOrCreate(
                item.getIngredientName(),
                item.getCategory()
            );
            item.setIngredientId(ingredientId);
        }
        
        //把傳入的userId也set進去
        item.setUserId(userId);
        
        if (item.getIsPurchased() == null) {
            item.setIsPurchased(false);
        }
        
        return shoppingListItemRepository.insert(item);
    }

    public ShoppingListItem updateItem(Integer userId, Integer id, ShoppingListItem updateItem) {

        if (userId == null) {
            throw new IllegalArgumentException("使用者 ID 不可為空");
        }
        
        if (id == null) {
            throw new IllegalArgumentException("項目 ID 不可為空");
        }
        
        if (updateItem == null) {
            throw new IllegalArgumentException("新增項目不可為空");
        }
        
        // 驗證必要欄位
        if (updateItem.getIngredientName() == null || updateItem.getIngredientName().trim().isEmpty()) {
            throw new IllegalArgumentException("新增食材名稱不可為空");
        }
        
        if (updateItem.getAmount() == null || updateItem.getAmount() <= 0) {
            throw new IllegalArgumentException("新增數量必須大於 0");
        }
        
        if (updateItem.getUnit() == null || updateItem.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("新增單位不可為空");
        }

        int rows = shoppingListItemRepository.update(userId, id, updateItem);

        if (rows == 0) {
            throw new RuntimeException("找不到此購物項目或無權修改");
        }   
        
        return shoppingListItemRepository.findById(id, userId); 

    }

    public ShoppingListItem togglePurchase(Integer userId, Integer id, Boolean isPurchased) {

        if (userId == null) {
            throw new IllegalArgumentException("使用者 ID 不可為空");
        }
        
        if (id == null) {
            throw new IllegalArgumentException("項目 ID 不可為空");
        }
        
        if (isPurchased == null) {
            throw new IllegalArgumentException("購買狀態不可為空");
        }

        int rows = shoppingListItemRepository.updatePurchasedItem(userId, id, isPurchased);

        if (rows == 0) {
            throw new RuntimeException("找不到此購物項目或無權修改");
        }   
            
        return shoppingListItemRepository.findById(id, userId);
    }

    public void deleteItem(Integer userId, Integer id) {

        if (userId == null) {
            throw new IllegalArgumentException("使用者 ID 不可為空");
        }
        
        if (id == null) {
            throw new IllegalArgumentException("項目 ID 不可為空");
        }

        int rows = shoppingListItemRepository.delete(userId, id);       
        
        if (rows == 0) {
            throw new RuntimeException("找不到此購物項目或無權刪除");
        }
    }

    public int clearPurchasedAndAddToFridge(Integer userId) {

        if (userId == null) {
	        throw new IllegalArgumentException("使用者 ID 不可為空");
	    }

        List<ShoppingListItem> purchasedItems = shoppingListItemRepository.findPurchasedItem(userId);
        if (purchasedItems.isEmpty()) {
	        return 0;
	    }
	        
	    int addedCount = 0;

        for (ShoppingListItem item : purchasedItems) {

            try {
                Integer ingredientId = item.getIngredientId();
                
                if (ingredientId == null) {
                    ingredientId = ingredientRepository.findOrCreate(
                        item.getIngredientName(),
                        item.getCategory()
                    );
                }
                
                // 建立 FridgeItem
                FridgeItem fridgeItem = new FridgeItem();
                fridgeItem.setUserId(userId);
                fridgeItem.setIngredientId(ingredientId);
                fridgeItem.setIngredientName(item.getIngredientName());
                fridgeItem.setAmount(item.getAmount());
                fridgeItem.setUnit(item.getUnit());
                fridgeItem.setCategory(item.getCategory());
                
                LocalDate purchaseDate = LocalDate.now();
                Integer shelfLifeDays = fridgeItemService.getDefaultShelfLife(item.getCategory());
                LocalDate expirationDate = purchaseDate.plusDays(shelfLifeDays);
                
                fridgeItem.setPurchasedDate(purchaseDate);
                fridgeItem.setExpiredDate(expirationDate);
                
                // 加入冰箱
                fridgeItemRepository.insert(fridgeItem);
                addedCount++;
                
            } catch (Exception e) {
                System.err.println("加入冰箱失敗 (項目: " + item.getIngredientName() + "): " + e.getMessage());
                // 繼續處理其他項目
            }   
        }

        int deletedCount = shoppingListItemRepository.deletePurchasedItem(userId);
        
        System.out.println("成功加入 " + addedCount + " 個項目到冰箱，刪除 " + deletedCount + " 個購物項目");
        
        return addedCount;

    }
}
