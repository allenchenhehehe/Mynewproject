package test;

import java.util.List;

import model.Recipe;
import model.RecipeIngredient;
import service.RecipeService;

public class RecipeServiceTest {
    
    public static void main(String[] args) {
        RecipeService service = new RecipeService();
        
        System.out.println("========== 測試 1: 查詢所有食譜 ==========");
        List<Recipe> allRecipes = service.getAllRecipes();
        System.out.println("共有 " + allRecipes.size() + " 個食譜");
        for (Recipe r : allRecipes) {
            System.out.println(r.getId() + ". " + r.getTitle() + 
                             " (難度:" + r.getDifficulty() + 
                             ", 時間:" + r.getCookingTime() + "分" +
                             ", 食材:" + r.getIngredients().size() + "種)");
        }
        
        System.out.println("\n========== 測試 2: 查詢單一食譜詳情 ==========");
        Recipe recipe = service.getRecipeDetail(1);
        if (recipe != null) {
            System.out.println("食譜: " + recipe.getTitle());
            System.out.println("描述: " + recipe.getDescription());
            System.out.println("難度: " + recipe.getDifficulty() + " 星");
            System.out.println("烹飪時間: " + recipe.getCookingTime() + " 分鐘");
            System.out.println("\n食材清單:");
            for (RecipeIngredient ing : recipe.getIngredients()) {
                System.out.println("  - " + ing.getIngredientName() + 
                                 " " + ing.getAmount() + 
                                 " " + ing.getUnit() +
                                 " (" + ing.getCategory() + ")");
            }
        }
        
        System.out.println("\n========== 測試 3: 搜尋食譜 ==========");
        List<Recipe> searchResults = service.searchRecipes("肉");
        System.out.println("搜尋「肉」, 找到 " + searchResults.size() + " 個食譜:");
        for (Recipe r : searchResults) {
            System.out.println("  - " + r.getTitle());
        }
        
        System.out.println("\n========== 測試 4: 篩選食譜 (難度=3) ==========");
        List<Recipe> filtered1 = service.getRecipesByDifficulty(3);
        System.out.println("難度=3 的食譜 (" + filtered1.size() + " 個):");
        for (Recipe r : filtered1) {
            System.out.println("  - " + r.getTitle() + " (難度:" + r.getDifficulty() + ")");
        }
        
        System.out.println("\n========== 測試 5: 篩選食譜 (時間 ≤ 20分) ==========");
        List<Recipe> filtered2 = service.getQuickRecipes(20);
        System.out.println("烹飪時間 ≤ 20分 的食譜 (" + filtered2.size() + " 個):");
        for (Recipe r : filtered2) {
            System.out.println("  - " + r.getTitle() + " (時間:" + r.getCookingTime() + "分)");
        }
        
        System.out.println("\n========== 測試 6: 篩選食譜 (時間 ≥ 60分) ==========");
        List<Recipe> filtered3 = service.getSlowRecipes(60);
        System.out.println("烹飪時間 ≥ 60分 的食譜 (" + filtered3.size() + " 個):");
        for (Recipe r : filtered3) {
            System.out.println("  - " + r.getTitle() + " (時間:" + r.getCookingTime() + "分)");
        }
        
        System.out.println("\n========== 測試 7: 綜合篩選 (難度=5, 時間 ≤ 30分) ==========");
        List<Recipe> filtered4 = service.filterRecipes(5, null, 30);
        System.out.println("難度=5 且 時間≤30分 的食譜 (" + filtered4.size() + " 個):");
        for (Recipe r : filtered4) {
            System.out.println("  - " + r.getTitle() + 
                             " (難度:" + r.getDifficulty() + 
                             ", 時間:" + r.getCookingTime() + "分)");
        }
        
        System.out.println("\n========== 測試 8: 查詢不存在的食譜 ==========");
        Recipe notFound = service.getRecipeDetail(999);
        System.out.println("食譜 ID 999: " + (notFound == null ? "不存在 ✅" : "找到了 ❌"));
        
        System.out.println("\n========== 測試 9: 空關鍵字搜尋 ==========");
        List<Recipe> emptySearch = service.searchRecipes("");
        System.out.println("空關鍵字搜尋, 回傳 " + emptySearch.size() + " 個食譜 (應該是全部)");
        
        System.out.println("\n========== 所有測試完成! ==========");
    }
}