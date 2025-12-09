package test;

import java.util.List;

import dao.ShoppingListItemDAO;
import model.ShoppingListItem;

public class ShoppingListDAOTest {
    
    public static void main(String[] args) {
        ShoppingListItemDAO dao = new ShoppingListItemDAO();
        Integer testUserId = 1;  // 假設測試使用者 ID 是 1
        
        System.out.println("========== 測試 1: insertItem() - 新增購物項目 ==========");
        try {
            // 測試 1: 新增有 recipe_id 的項目
            ShoppingListItem item1 = new ShoppingListItem();
            item1.setUserId(testUserId);
            item1.setRecipeId(1);  // 來自食譜「番茄炒蛋」
            item1.setRecipeName("番茄炒蛋");
            item1.setIngredientId(1);
            item1.setIngredientName("番茄");
            item1.setAmount(2.0);
            item1.setUnit("個");
            item1.setCategory("vegetable");
            item1.setIsPurchased(false);
            
            ShoppingListItem inserted1 = dao.insertItem(item1);
            System.out.println("✅ 新增成功 ID: " + inserted1.getId() + " - " + inserted1.getIngredientName());
            
            // 測試 2: 新增手動加入的項目 (無 recipe_id)
            ShoppingListItem item2 = new ShoppingListItem();
            item2.setUserId(testUserId);
            item2.setRecipeId(null);  // 手動加入
            item2.setRecipeName(null);
            item2.setIngredientId(null);  // 自訂食材
            item2.setIngredientName("牛排");
            item2.setAmount(1.0);
            item2.setUnit("片");
            item2.setCategory("meat");
            item2.setIsPurchased(false);
            
            ShoppingListItem inserted2 = dao.insertItem(item2);
            System.out.println("✅ 新增成功 ID: " + inserted2.getId() + " - " + inserted2.getIngredientName());
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 2: findByUserId() - 查詢購物清單 ==========");
        try {
            List<ShoppingListItem> items = dao.findByUserId(testUserId);
            System.out.println("✅ 查詢到 " + items.size() + " 個項目:");
            
            for (ShoppingListItem item : items) {
                System.out.println("  - ID:" + item.getId() + 
                                 " | " + item.getIngredientName() + 
                                 " " + item.getAmount() + item.getUnit() +
                                 " | 分類:" + item.getCategory() +
                                 " | 已購買:" + item.getIsPurchased() +
                                 (item.getRecipeName() != null ? " | 來自:" + item.getRecipeName() : ""));
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 3: findById() - 查詢單一項目 ==========");
        try {
            ShoppingListItem item = dao.findById(1, testUserId);
            
            if (item != null) {
                System.out.println("✅ 查詢成功:");
                System.out.println("  ID: " + item.getId());
                System.out.println("  食材: " + item.getIngredientName());
                System.out.println("  數量: " + item.getAmount() + " " + item.getUnit());
                System.out.println("  分類: " + item.getCategory());
                System.out.println("  已購買: " + item.getIsPurchased());
                System.out.println("  來自食譜: " + item.getRecipeName());
            } else {
                System.out.println("❌ 找不到項目");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 4: updatePurchasedStatus() - 標記已購買 ==========");
        try {
            ShoppingListItem updated = dao.updatePurchasedItem(testUserId, 1, true);
            
            if (updated != null) {
                System.out.println("✅ 更新成功: " + updated.getIngredientName() + 
                                 " 已購買=" + updated.getIsPurchased());
            } else {
                System.out.println("❌ 更新失敗");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 5: updateItem() - 更新項目 ==========");
        try {
            ShoppingListItem updateData = new ShoppingListItem();
            updateData.setIngredientName("大番茄");  // 改名字
            updateData.setAmount(3.0);  // 改數量
            updateData.setUnit("顆");
            updateData.setCategory("vegetable");
            updateData.setIsPurchased(false);
            
            ShoppingListItem updated = dao.updateItem(testUserId, 1, updateData);
            
            if (updated != null) {
                System.out.println("✅ 更新成功: " + updated.getIngredientName() + 
                                 " " + updated.getAmount() + updated.getUnit());
            } else {
                System.out.println("❌ 更新失敗");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 6: findPurchasedItems() - 查詢已購買項目 ==========");
        try {
            // 先標記幾個為已購買
            dao.updatePurchasedItem(testUserId, 1, true);
            dao.updatePurchasedItem(testUserId, 2, true);
            
            List<ShoppingListItem> purchased = dao.findPurchasedItem(testUserId);
            System.out.println("✅ 已購買項目有 " + purchased.size() + " 個:");
            
            for (ShoppingListItem item : purchased) {
                System.out.println("  - " + item.getIngredientName() + 
                                 " " + item.getAmount() + item.getUnit());
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 7: deleteItem() - 刪除單一項目 ==========");
        try {
            int rows = dao.deleteItem(testUserId, 2);
            
            if (rows > 0) {
                System.out.println("✅ 刪除成功,影響 " + rows + " 筆");
            } else {
                System.out.println("❌ 刪除失敗");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 8: deletePurchasedItems() - 清除已購買項目 ==========");
        try {
            int rows = dao.deletePurchasedItem(testUserId);
            System.out.println("✅ 刪除 " + rows + " 個已購買項目");
            
            // 檢查是否清空
            List<ShoppingListItem> remaining = dao.findByUserId(testUserId);
            System.out.println("剩餘 " + remaining.size() + " 個未購買項目");
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        System.out.println("\n========== 測試 9: 權限檢查 - 無法查看他人項目 ==========");
        try {
            ShoppingListItem item = dao.findById(1, 999);  // 錯誤的 user_id
            
            if (item == null) {
                System.out.println("✅ 測試通過: 無法查看他人的購物項目");
            } else {
                System.out.println("❌ 測試失敗: 不應該能查看他人項目");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
        }
        
        System.out.println("\n========================================");
        System.out.println("所有 ShoppingListDAO 測試完成!");
        System.out.println("========================================");
    }
}