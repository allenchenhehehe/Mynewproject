package test;

import model.FridgeItem;
import java.time.LocalDate;
import java.util.List;

import dao.FridgeItemDAO;

public class FridgeItemDAOTest {

    // 💡 設定測試用的常數
    private static final Integer TEST_USER_ID = 1; // 確保這個用戶ID存在或可以在DB中插入
    private static final Integer TEST_INGREDIENT_ID = 1; // 確保這個食材ID存在於 ingredients 表中

    // 創建一個基礎的 FridgeItem 物件用於測試插入和更新
    private static FridgeItem createTestItem() {
        // 注意：這裡我們只提供必要的欄位，id 會由 insert 生成
        FridgeItem item = new FridgeItem();
        item.setUserId(TEST_USER_ID);
        item.setIngredientId(TEST_INGREDIENT_ID);
        item.setAmount(5);
        item.setUnit("個");
        item.setPurchasedDate(LocalDate.now());
        item.setExpiredDate(LocalDate.now().plusDays(7));
        // ingredientName 和 category 會在 findByUserId 時被 DAO 填充
        return item;
    }

    public static void main(String[] args) {
        FridgeItemDAO dao = new FridgeItemDAO();

        System.out.println("--- 開始 FridgeItemDAO 測試 ---");
        
        FridgeItem insertedItem = null;
        try {
            // 1. 測試 INSERT (新增)
            insertedItem = testInsert(dao);

            // 2. 測試 SELECT (查詢)
            testSelect(dao, insertedItem);

            // 3. 測試 UPDATE (更新)
            testUpdate(dao, insertedItem);

        } catch (Exception e) {
            System.err.println("❌ 測試過程中發生錯誤: " + e.getMessage());
        } finally {
            // 4. 測試 DELETE (刪除) & 清理
            if (insertedItem != null && insertedItem.getId() != null) {
                testDelete(dao, insertedItem.getId());
            }
        }

        System.out.println("--- 結束 FridgeItemDAO 測試 ---");
    }

    // --- 1. INSERT 測試 ---
    private static FridgeItem testInsert(FridgeItemDAO dao) {
        System.out.println("\n[1] 測試新增食材 (INSERT)...");
        FridgeItem newItem = createTestItem();
        FridgeItem inserted = dao.insert(newItem);

        if (inserted != null && inserted.getId() != null) {
            System.out.println("✅ 新增成功。新 ID: " + inserted.getId());
            return inserted;
        } else {
            System.err.println("❌ 新增失敗：未取得自動生成的 ID。");
            return null;
        }
    }

    // --- 2. SELECT 測試 ---
    private static void testSelect(FridgeItemDAO dao, FridgeItem insertedItem) {
        System.out.println("\n[2] 測試查詢食材 (SELECT)...");
        if (insertedItem == null) return;

        List<FridgeItem> list = dao.findByUserId(TEST_USER_ID);

        boolean found = list.stream().anyMatch(item -> 
            item.getId().equals(insertedItem.getId()) && 
            item.getAmount().equals(insertedItem.getAmount()) &&
            item.getIngredientName() != null // 檢查是否有JOIN到資料
        );

        if (found) {
            System.out.println("✅ 查詢成功。共找到 " + list.size() + " 個食材，包含剛新增的項目。");
        } else {
            System.err.println("❌ 查詢失敗：找不到剛新增或JOIN欄位不正確。");
        }
    }
    
    // --- 3. UPDATE 測試 ---
    private static void testUpdate(FridgeItemDAO dao, FridgeItem insertedItem) {
        System.out.println("\n[3] 測試更新食材 (UPDATE)...");
        if (insertedItem == null) return;
        
        // 變更一些值
        insertedItem.setAmount(100);
        insertedItem.setUnit("克");
        insertedItem.setExpiredDate(LocalDate.now().plusDays(30)); 

        FridgeItem updated = dao.updateItem(insertedItem);

        // 重新查詢資料庫來確認更新是否真的生效
        List<FridgeItem> list = dao.findByUserId(TEST_USER_ID);
        boolean updatedConfirmed = list.stream().anyMatch(item -> 
            item.getId().equals(insertedItem.getId()) && 
            item.getAmount().equals(100) // 檢查 amount 是否已變為 100
        );

        if (updatedConfirmed) {
            System.out.println("✅ 更新成功：Amount 已變為 100。");
        } else {
            System.err.println("❌ 更新失敗：資料庫中的值未正確改變。");
        }
    }

//     --- 4. DELETE 測試 ---
    private static void testDelete(FridgeItemDAO dao, Integer itemId) {
        System.out.println("\n[4] 測試刪除食材 (DELETE) 及清理...");
        try {
            dao.deleteItem(itemId);
            
            // 確認是否真的刪除了
            List<FridgeItem> list = dao.findByUserId(TEST_USER_ID);
            boolean deleted = list.stream().noneMatch(item -> item.getId().equals(itemId));
            
            if (deleted) {
                 System.out.println("✅ 刪除成功，已清理測試數據。");
            } else {
                 System.err.println("❌ 刪除失敗：食材 ID " + itemId + " 仍存在。");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 刪除測試失敗: " + e.getMessage());
        }
    }
}