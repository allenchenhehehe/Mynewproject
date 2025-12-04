package test;

import dao.IngredientDAO;
import model.Ingredient;

public class TestIngredientDAO {
    public static void main(String[] args) {
        IngredientDAO dao = new IngredientDAO();
        
        System.out.println("=== 測試 findByName ===");
        
        // 測試 1: 查詢存在的食材
        Ingredient ing1 = dao.findByName("番茄");
        if (ing1 != null) {
            System.out.println("找到食材:");
            System.out.println("ID: " + ing1.getId());
            System.out.println("名稱: " + ing1.getIngredientName());
            System.out.println("分類: " + ing1.getCategory());
            System.out.println("保存天數: " + ing1.getShelfLifeDays());
        } else {
            System.out.println("找不到「番茄」");
        }
        
        System.out.println();
        
        // 測試 2: 查詢不存在的食材
        Ingredient ing2 = dao.findByName("不存在的食材");
        if (ing2 == null) {
            System.out.println("正確回傳 null (不存在的食材)");
        } else {
            System.out.println("不應該找到資料!");
        }
    }
}