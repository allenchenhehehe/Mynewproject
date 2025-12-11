package service;


import java.time.LocalDate;
import java.util.List;

import dao.FridgeItemDAO;
import dao.IngredientDAO;
import dao.ShoppingListItemDAO;
import model.FridgeItem;
import model.ShoppingListItem;
import model.Ingredient;

public class ShoppingListItemService {
	
	private ShoppingListItemDAO shoppingListItemDao;
	private FridgeItemDAO fridgeItemDAO;
	private FridgeItemService fridgeItemService;
    private IngredientDAO ingredientDAO;
    private IngredientService ingredientService;
    
	public  ShoppingListItemService() {
		this.shoppingListItemDao = new ShoppingListItemDAO();
		this.fridgeItemDAO = new FridgeItemDAO();
		this.fridgeItemService = new FridgeItemService();
        this.ingredientDAO = new IngredientDAO();
        this.ingredientService = new IngredientService();
	}
	
	public List<ShoppingListItem> getShoppingList(Integer userId){
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		try {
			List<ShoppingListItem> list = shoppingListItemDao.findByUserId(userId);
			return list;
		}catch(Exception e) {
			System.err.println("查詢購物清單失敗: " + e.getMessage());
            throw new RuntimeException("查詢購物清單失敗", e);
		}	
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
        
        if(item.getIngredientId()==null) {
        	Ingredient exist = ingredientService.getIngreByName(item.getIngredientName());
        	if(exist != null) {
        		item.setIngredientId(exist.getId());
        	}else {
        		Ingredient newIngredient = new Ingredient();
        		newIngredient.setIngredientName(item.getIngredientName());
        		newIngredient.setCategory(item.getCategory());
        		newIngredient.setShelfLifeDays(fridgeItemService.getDefaultShelfLife(item.getCategory()));
        		Ingredient saved = ingredientService.addIngre(newIngredient);
        		item.setIngredientId(saved.getId());
        	}
        }
        
        try {
            // 設定使用者 ID
            item.setUserId(userId);
            
            // 如果沒有設定 isPurchased,預設為 false
            if (item.getIsPurchased() == null) {
                item.setIsPurchased(false);
            }
            
            return shoppingListItemDao.insertItem(item);
            
        } catch (Exception e) {
            System.err.println("新增購物項目失敗: " + e.getMessage());
            throw new RuntimeException("新增購物項目失敗", e);
        }
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
        
        try {
            // 設定使用者 ID
        	ShoppingListItem update = shoppingListItemDao.updateItem(userId, id, updateItem);
        	if (update == null) {
                throw new RuntimeException("找不到此購物項目或無權修改");
            }   
            return update;        
        } catch (Exception e) {
            System.err.println("更新購物項目失敗: " + e.getMessage());
            throw new RuntimeException("更新購物項目失敗", e);
        }
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
              
        try {
            // 設定使用者 ID
        	ShoppingListItem update = shoppingListItemDao.updatePurchasedItem(userId, id, isPurchased);
        	if (update == null) {
                throw new RuntimeException("找不到此購物項目或無權修改");
            }   
            return update;        
        } catch (Exception e) {
            System.err.println("更新購買狀態失敗: " + e.getMessage());
            throw new RuntimeException("更新購買狀態失敗", e);
        }
    }
	
	public void deleteItem(Integer userId, Integer id) {
        if (userId == null) {
            throw new IllegalArgumentException("使用者 ID 不可為空");
        }
        
        if (id == null) {
            throw new IllegalArgumentException("項目 ID 不可為空");
        }
        
        try {
            int rows = shoppingListItemDao.deleteItem(userId, id);       
            if (rows == 0) {
                throw new RuntimeException("找不到此購物項目或無權刪除");
            }
            
        } catch (Exception e) {
            System.err.println("刪除購物項目失敗: " + e.getMessage());
            throw new RuntimeException("刪除購物項目失敗", e);
        }
    }
	
	public int clearPurchasedAndAddToFridge(Integer userId) {
	    if (userId == null) {
	        throw new IllegalArgumentException("使用者 ID 不可為空");
	    }
	    
	    try {
	        // 1. 查詢已購買的項目
	        List<ShoppingListItem> purchasedItems = shoppingListItemDao.findPurchasedItem(userId);
	        
	        if (purchasedItems.isEmpty()) {
	            return 0;
	        }
	        
	        int addedCount = 0;
	        
	        // 2. 逐一加入冰箱
	        for (ShoppingListItem item : purchasedItems) {
	            try {
	                Integer ingredientId = item.getIngredientId();
	                
	                if (ingredientId == null) {
	                    // 應該都要有 ingredient_id
	                	ingredientId = ingredientDAO.findOrCreate(
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
	                Integer shelfLifeDays = fridgeItemService.getDefaultShelfLife(item.getCategory());  //呼叫fridgeItem的方法
	                LocalDate expirationDate = purchaseDate.plusDays(shelfLifeDays);
	                
	                fridgeItem.setPurchasedDate(purchaseDate);
	                fridgeItem.setExpiredDate(expirationDate);
	                
	                // 過期日期由 FridgeItemDAO.insert() 自動設定
	                // 之後會用 FridgeItemService.getDefaultShelfLife() 計算
	                
	                // 加入冰箱
	                fridgeItemDAO.insert(fridgeItem);
	                addedCount++;
	                
	            } catch (Exception e) {
	                System.err.println("加入冰箱失敗 (項目: " + item.getIngredientName() + "): " + e.getMessage());
	                // 繼續處理其他項目
	            }
	        }
	        
	        // 3. 刪除已購買的購物項目
	        int deletedCount = shoppingListItemDao.deletePurchasedItem(userId);
	        
	        System.out.println("成功加入 " + addedCount + " 個項目到冰箱,刪除 " + deletedCount + " 個購物項目");
	        
	        return addedCount;
	        
	    } catch (Exception e) {
	        System.err.println("清除已購買項目失敗: " + e.getMessage());
	        throw new RuntimeException("清除已購買項目失敗", e);
	    }
	}
	
}
