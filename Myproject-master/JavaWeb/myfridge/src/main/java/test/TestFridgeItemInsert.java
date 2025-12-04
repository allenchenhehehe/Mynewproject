package test;

import java.time.LocalDate;
import dao.FridgeItemDAO;
import dao.IngredientDAO;
import model.FridgeItem;
import model.Ingredient;

public class TestFridgeItemInsert {
    public static void main(String[] args) {
        IngredientDAO ingredientDAO = new IngredientDAO();
        FridgeItemDAO fridgeDAO = new FridgeItemDAO();
        
        System.out.println("=== 測試新增食材到冰箱 ===");
        
        // 1. 先查詢食材 ID
        Ingredient ing = ingredientDAO.findByName("番茄");
        if (ing == null) {
            System.out.println("找不到「番茄」,無法測試");
            return;
        }
        
        System.out.println("找到食材: " + ing.getIngredientName() + " (ID: " + ing.getId() + ")");
        
        // 2. 建立 FridgeItem
        FridgeItem item = new FridgeItem();
        item.setUserId(1);
        item.setIngredientId(ing.getId());
        item.setAmount(5);
        item.setUnit("個");
        item.setPurchasedDate(LocalDate.now());
        item.setExpiredDate(LocalDate.now().plusDays(7));
        
        // 3. 新增到資料庫
        FridgeItem saved = fridgeDAO.insert(item);
        
        System.out.println("新增成功!");
        System.out.println("新的 ID: " + saved.getId());
        System.out.println("使用者 ID: " + saved.getUserId());
        System.out.println("食材 ID: " + saved.getIngredientId());
        System.out.println("數量: " + saved.getAmount() + " " + saved.getUnit());
        System.out.println("購買日期: " + saved.getPurchasedDate());
        System.out.println("過期日期: " + saved.getExpiredDate());
        
        // 4. 驗證:查詢看是否真的加入了
        System.out.println("\n=== 驗證:查詢 user_id=1 的冰箱 ===");
        var items = fridgeDAO.findByUserId(1);
        System.out.println("冰箱共有 " + items.size() + " 個食材");
        
        // 找到剛剛新增的
        boolean found = items.stream()
            .anyMatch(i -> i.getId().equals(saved.getId()));
        
        if (found) {
            System.out.println("驗證成功!剛剛新增的食材在冰箱裡!");
        } else {
            System.out.println("驗證失敗!找不到剛剛新增的食材!");
        }
    }
}