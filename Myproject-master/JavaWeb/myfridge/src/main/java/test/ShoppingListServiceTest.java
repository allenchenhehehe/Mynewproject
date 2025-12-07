package test;

import java.util.List;

import model.ShoppingItem;
import service.ShoppingListService;

public class ShoppingListServiceTest {
    
    public static void main(String[] args) {
        ShoppingListService service = new ShoppingListService();
        Integer testUserId = 1;
        
        // ✨ 用來儲存新增項目的 ID
        Integer itemId1 = null;
        Integer itemId2 = null;
        Integer itemId3 = null;
        
        System.out.println("========================================");
        System.out.println("開始測試 ShoppingListService");
        System.out.println("========================================\n");
        
        // ========== 測試 1: addItem() ==========
        System.out.println("========== 測試 1: addItem() - 新增購物項目 ==========");
        try {
            // 新增項目 1
            ShoppingItem item1 = new ShoppingItem();
            item1.setRecipeId(1);
            item1.setRecipeName("番茄炒蛋");
            item1.setIngredientId(1);
            item1.setIngredientName("番茄");
            item1.setAmount(2.0);
            item1.setUnit("個");
            item1.setCategory("vegetable");
            
            ShoppingItem added1 = service.addItem(testUserId, item1);
            itemId1 = added1.getId();  // ✨ 儲存 ID
            System.out.println("✅ 新增成功 ID: " + itemId1 + " - " + added1.getIngredientName());
            
            // 新增項目 2
            ShoppingItem item2 = new ShoppingItem();
            item2.setIngredientName("龍眼乾");
            item2.setAmount(200.0);
            item2.setUnit("克");
            item2.setCategory("fruit");
            
            ShoppingItem added2 = service.addItem(testUserId, item2);
            itemId2 = added2.getId();  // ✨ 儲存 ID
            System.out.println("✅ 新增成功 ID: " + itemId2 + " - " + added2.getIngredientName());
            
            // 新增項目 3
            ShoppingItem item3 = new ShoppingItem();
            item3.setIngredientName("牛排");
            item3.setAmount(1.0);
            item3.setUnit("片");
            item3.setCategory("meat");
            
            ShoppingItem added3 = service.addItem(testUserId, item3);
            itemId3 = added3.getId();  // ✨ 儲存 ID
            System.out.println("✅ 新增成功 ID: " + itemId3 + " - " + added3.getIngredientName());
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        // ========== 測試 2: getShoppingList() ==========
        System.out.println("\n========== 測試 2: getShoppingList() ==========");
        try {
            List<ShoppingItem> items = service.getShoppingList(testUserId);
            System.out.println("✅ 查詢到 " + items.size() + " 個項目:\n");
            
            for (ShoppingItem item : items) {
                System.out.println("  - ID:" + item.getId() + " | " + 
                                 item.getIngredientName() + " " + 
                                 item.getAmount() + item.getUnit() + 
                                 " | 已購買:" + item.getIsPurchased());
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        // ========== 測試 3: updateItem() ==========
        System.out.println("\n========== 測試 3: updateItem() - 更新項目 ==========");
        try {
            if (itemId1 == null) {
                System.out.println("⚠️ 跳過測試: 沒有可更新的項目");
            } else {
                ShoppingItem updateData = new ShoppingItem();
                updateData.setIngredientName("大番茄");
                updateData.setAmount(5.0);
                updateData.setUnit("顆");
                updateData.setCategory("vegetable");
                updateData.setIsPurchased(false);
                
                ShoppingItem updated = service.updateItem(testUserId, itemId1, updateData);  // ✅ 使用動態 ID
                System.out.println("✅ 更新成功 ID:" + itemId1);
                System.out.println("   食材: " + updated.getIngredientName());
                System.out.println("   數量: " + updated.getAmount() + " " + updated.getUnit());
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        // ========== 測試 4: togglePurchase() ==========
        System.out.println("\n========== 測試 4: togglePurchase() - 切換購買狀態 ==========");
        try {
            if (itemId1 == null || itemId2 == null || itemId3 == null) {
                System.out.println("⚠️ 跳過測試: 沒有可更新的項目");
            } else {
                ShoppingItem updated1 = service.togglePurchase(testUserId, itemId1, true);  // ✅ 動態 ID
                System.out.println("✅ 標記已購買: ID:" + itemId1 + " " + updated1.getIngredientName());
                
                ShoppingItem updated2 = service.togglePurchase(testUserId, itemId2, true);  // ✅ 動態 ID
                System.out.println("✅ 標記已購買: ID:" + itemId2 + " " + updated2.getIngredientName());
                
                ShoppingItem updated3 = service.togglePurchase(testUserId, itemId3, true);  // ✅ 動態 ID
                System.out.println("✅ 標記已購買: ID:" + itemId3 + " " + updated3.getIngredientName());
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        // ========== 測試 5: clearPurchasedAndAddToFridge() ==========
        System.out.println("\n========== 測試 5: clearPurchasedAndAddToFridge() ✨ ==========");
        try {
            int count = service.clearPurchasedAndAddToFridge(testUserId);
            System.out.println("✅ 成功加入 " + count + " 個項目到冰箱");
            
            // 檢查購物清單
            List<ShoppingItem> remaining = service.getShoppingList(testUserId);
            long purchasedCount = remaining.stream()
                .filter(item -> item.getIsPurchased())
                .count();
            
            System.out.println("剩餘購物項目: " + remaining.size() + " 個");
            System.out.println("已購買項目: " + purchasedCount + " 個 (應該是 0)");
            
            if (purchasedCount == 0) {
                System.out.println("✅ 測試通過: 已購買項目已清空");
            } else {
                System.out.println("❌ 測試失敗: 還有已購買項目");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        // ========== 測試 6: deleteItem() ==========
        System.out.println("\n========== 測試 6: deleteItem() - 刪除項目 ==========");
        try {
            // 先新增一個測試項目
            ShoppingItem testItem = new ShoppingItem();
            testItem.setIngredientName("測試食材");
            testItem.setAmount(1.0);
            testItem.setUnit("個");
            testItem.setCategory("other");
            
            ShoppingItem added = service.addItem(testUserId, testItem);
            Integer testItemId = added.getId();
            System.out.println("✅ 新增測試項目 ID: " + testItemId);
            
            // 刪除
            service.deleteItem(testUserId, testItemId);
            System.out.println("✅ 刪除成功");
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        // ========== 測試 7-11: 參數驗證 ==========
        System.out.println("\n========== 測試 7: 參數驗證 - null userId ==========");
        try {
            service.getShoppingList(null);
            System.out.println("❌ 測試失敗: 應該拋出異常");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ 測試通過: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 8: 參數驗證 - 空食材名稱 ==========");
        try {
            ShoppingItem item = new ShoppingItem();
            item.setIngredientName("");
            item.setAmount(1.0);
            item.setUnit("個");
            
            service.addItem(testUserId, item);
            System.out.println("❌ 測試失敗: 應該拋出異常");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ 測試通過: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 9: 參數驗證 - 數量 <= 0 ==========");
        try {
            ShoppingItem item = new ShoppingItem();
            item.setIngredientName("測試");
            item.setAmount(0.0);
            item.setUnit("個");
            
            service.addItem(testUserId, item);
            System.out.println("❌ 測試失敗: 應該拋出異常");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ 測試通過: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 10: 權限檢查 ==========");
        try {
            ShoppingItem updateData = new ShoppingItem();
            updateData.setIngredientName("測試");
            updateData.setAmount(1.0);
            updateData.setUnit("個");
            updateData.setCategory("other");
            updateData.setIsPurchased(false);
            
            service.updateItem(999, 1, updateData);  // 錯誤的 user_id
            System.out.println("❌ 測試失敗: 應該拋出異常");
        } catch (RuntimeException e) {
            System.out.println("✅ 測試通過: " + e.getMessage());
        }
        
        System.out.println("\n========================================");
        System.out.println("所有 ShoppingListService 測試完成!");
        System.out.println("========================================");
        
        System.out.println("\n⚠️ 請手動檢查資料庫:");
        System.out.println("1. SELECT * FROM fridge_items WHERE user_id = 1;");
        System.out.println("   (應該有 3 筆: 大番茄, 龍眼乾, 牛排)");
        System.out.println("\n2. SELECT * FROM ingredients WHERE ingredient_name IN ('龍眼乾', '牛排');");
        System.out.println("   (應該自動建立這 2 個食材)");
        System.out.println("\n3. SELECT * FROM shopping_list_items WHERE user_id = 1 AND is_purchased = TRUE;");
        System.out.println("   (應該是空的,已購買項目已刪除)");
    }
}