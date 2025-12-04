package test;

import java.util.List;
import dao.FridgeItemDAO;
import model.FridgeItem;

public class TestFridgeItemDAO {
    
    public static void main(String[] args) {
        // 1. 建立 DAO 物件
        FridgeItemDAO dao = new FridgeItemDAO();
        
        // 2. 測試查詢
        System.out.println("=== 測試查詢 user_id=1 的冰箱 ===");
        List<FridgeItem> items = dao.findByUserId(1);
        
        // 3. 印出結果
        System.out.println("查詢到 " + items.size() + " 個食材");
        
        for (FridgeItem item : items) {
            System.out.println("---");
            System.out.println("ID: " + item.getId());
            System.out.println("食材名稱: " + item.getIngredientName());
            System.out.println("分類: " + item.getCategory());
            System.out.println("數量: " + item.getAmount() + " " + item.getUnit());
            System.out.println("購買日期: " + item.getPurchasedDate());
            System.out.println("過期日期: " + item.getExpiredDate());
        }
    }
}