package test;

import java.util.List;

import dao.RecipeDAO;
import model.Recipe;
import model.RecipeIngredient;

public class RecipeDAOTest {
    
    public static void main(String[] args) {
        RecipeDAO dao = new RecipeDAO();
        
        System.out.println("========== 測試 1: findAll() - 查詢所有食譜 ==========");
        try {
            List<Recipe> allRecipes = dao.findAll();
            System.out.println("✅ 成功查詢到 " + allRecipes.size() + " 個食譜");
            
            for (Recipe r : allRecipes) {
                System.out.println("\nID: " + r.getId());
                System.out.println("標題: " + r.getTitle());
                System.out.println("描述: " + r.getDescription());
                System.out.println("難度: " + r.getDifficulty() + " 星");
                System.out.println("烹飪時間: " + r.getCookingTime() + " 分鐘");
                System.out.println("圖片: " + r.getImageUrl());
                System.out.println("食材數量: " + (r.getIngredients() != null ? r.getIngredients().size() : 0) + " 種");
                
                // 檢查每個食譜是否有食材
                if (r.getIngredients() == null || r.getIngredients().isEmpty()) {
                    System.out.println("⚠️ 警告: 食譜「" + r.getTitle() + "」沒有食材!");
                }
            }
            
            // 驗證預期結果
            if (allRecipes.size() == 12) {
                System.out.println("\n✅ 測試通過: 食譜數量正確 (12 個)");
            } else {
                System.out.println("\n❌ 測試失敗: 預期 12 個食譜，實際 " + allRecipes.size() + " 個");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 2: findById(1) - 查詢單一食譜 ==========");
        try {
            Recipe recipe = dao.findById(1);
            
            if (recipe != null) {
                System.out.println("✅ 成功查詢到食譜");
                System.out.println("\nID: " + recipe.getId());
                System.out.println("標題: " + recipe.getTitle());
                System.out.println("描述: " + recipe.getDescription());
                System.out.println("難度: " + recipe.getDifficulty() + " 星");
                System.out.println("烹飪時間: " + recipe.getCookingTime() + " 分鐘");
                System.out.println("是否公開: " + recipe.getIsPublic());
                
                System.out.println("\n食材清單:");
                if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                    for (RecipeIngredient ing : recipe.getIngredients()) {
                        System.out.println("  - ID: " + ing.getId());
                        System.out.println("    食材ID: " + ing.getIngredientId());
                        System.out.println("    名稱: " + ing.getIngredientName());
                        System.out.println("    數量: " + ing.getAmount());
                        System.out.println("    單位: " + ing.getUnit());
                        System.out.println("    分類: " + ing.getCategory());
                        System.out.println();
                    }
                    
                    if (recipe.getIngredients().size() >= 3) {
                        System.out.println("✅ 測試通過: 食譜有食材清單");
                    } else {
                        System.out.println("⚠️ 警告: 食材數量少於預期");
                    }
                } else {
                    System.out.println("❌ 測試失敗: 沒有食材清單");
                }
                
                System.out.println("\n詳細步驟:");
                System.out.println(recipe.getStep());
                
            } else {
                System.out.println("❌ 測試失敗: 找不到食譜 ID 1");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 3: findById(999) - 查詢不存在的食譜 ==========");
        try {
            Recipe notFound = dao.findById(999);
            
            if (notFound == null) {
                System.out.println("✅ 測試通過: 不存在的食譜正確回傳 null");
            } else {
                System.out.println("❌ 測試失敗: 不應該找到食譜 ID 999");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 4: searchByKeyword(\"肉\") - 搜尋食譜 ==========");
        try {
            List<Recipe> searchResults = dao.findByKeyword("肉");
            System.out.println("✅ 搜尋「肉」找到 " + searchResults.size() + " 個食譜:");
            
            for (Recipe r : searchResults) {
                System.out.println("  - " + r.getTitle() + 
                                 " (食材: " + r.getIngredients().size() + " 種)");
            }
            
            // 驗證: 應該包含「紅燒肉」、「宮保雞丁」等
            boolean foundRedBraisedPork = searchResults.stream()
                .anyMatch(r -> r.getTitle().contains("紅燒肉"));
            boolean foundMuxu = searchResults.stream()
                .anyMatch(r -> r.getTitle().contains("木須肉"));
            
            if (foundRedBraisedPork && foundMuxu) {
                System.out.println("✅ 測試通過: 找到預期的食譜");
            } else {
                System.out.println("⚠️ 警告: 可能遺漏某些食譜");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 5: searchByKeyword(\"番茄\") - 搜尋食譜 ==========");
        try {
            List<Recipe> searchResults = dao.findByKeyword("番茄");
            System.out.println("✅ 搜尋「番茄」找到 " + searchResults.size() + " 個食譜:");
            
            for (Recipe r : searchResults) {
                System.out.println("  - " + r.getTitle());
            }
            
            boolean foundTomatoEgg = searchResults.stream()
                .anyMatch(r -> r.getTitle().contains("番茄炒蛋"));
            
            if (foundTomatoEgg) {
                System.out.println("✅ 測試通過: 找到「番茄炒蛋」");
            } else {
                System.out.println("❌ 測試失敗: 應該找到「番茄炒蛋」");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 6: filter(difficulty=3) - 篩選難度 ==========");
        try {
            List<Recipe> filtered = dao.filter(3, null, null);
            System.out.println("✅ 難度=3 的食譜有 " + filtered.size() + " 個:");
            
            for (Recipe r : filtered) {
                System.out.println("  - " + r.getTitle() + 
                                 " (難度: " + r.getDifficulty() + 
                                 ", 時間: " + r.getCookingTime() + "分" +
                                 ", 食材: " + r.getIngredients().size() + "種)");
            }
            
            // 驗證所有食譜難度都是 3
            boolean allCorrectDifficulty = filtered.stream()
                .allMatch(r -> r.getDifficulty() == 3);
            
            if (allCorrectDifficulty) {
                System.out.println("✅ 測試通過: 所有食譜難度都是 3");
            } else {
                System.out.println("❌ 測試失敗: 有食譜難度不是 3");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 7: filter(maxCookingTime=20) - 篩選時間 ≤ 20分 ==========");
        try {
            List<Recipe> filtered = dao.filter(null, null, 20);
            System.out.println("✅ 烹飪時間 ≤ 20分 的食譜有 " + filtered.size() + " 個:");
            
            for (Recipe r : filtered) {
                System.out.println("  - " + r.getTitle() + 
                                 " (時間: " + r.getCookingTime() + "分" +
                                 ", 食材: " + r.getIngredients().size() + "種)");
            }
            
            // 驗證所有食譜時間都 ≤ 20
            boolean allCorrectTime = filtered.stream()
                .allMatch(r -> r.getCookingTime() <= 20);
            
            if (allCorrectTime) {
                System.out.println("✅ 測試通過: 所有食譜時間都 ≤ 20分");
            } else {
                System.out.println("❌ 測試失敗: 有食譜時間 > 20分");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 8: filter(minCookingTime=60) - 篩選時間 ≥ 60分 ==========");
        try {
            List<Recipe> filtered = dao.filter(null, 60, null);
            System.out.println("✅ 烹飪時間 ≥ 60分 的食譜有 " + filtered.size() + " 個:");
            
            for (Recipe r : filtered) {
                System.out.println("  - " + r.getTitle() + 
                                 " (時間: " + r.getCookingTime() + "分" +
                                 ", 食材: " + r.getIngredients().size() + "種)");
            }
            
            // 驗證所有食譜時間都 ≥ 60
            boolean allCorrectTime = filtered.stream()
                .allMatch(r -> r.getCookingTime() >= 60);
            
            if (allCorrectTime) {
                System.out.println("✅ 測試通過: 所有食譜時間都 ≥ 60分");
            } else {
                System.out.println("❌ 測試失敗: 有食譜時間 < 60分");
                for (Recipe r : filtered) {
                    if (r.getCookingTime() < 60) {
                        System.out.println("  錯誤: " + r.getTitle() + " 時間=" + r.getCookingTime());
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 9: filter(difficulty=5, maxCookingTime=30) - 綜合篩選 ==========");
        try {
            List<Recipe> filtered = dao.filter(5, null, 30);
            System.out.println("✅ 難度=5 且 時間≤30分 的食譜有 " + filtered.size() + " 個:");
            
            for (Recipe r : filtered) {
                System.out.println("  - " + r.getTitle() + 
                                 " (難度: " + r.getDifficulty() + 
                                 ", 時間: " + r.getCookingTime() + "分" +
                                 ", 食材: " + r.getIngredients().size() + "種)");
            }
            
            // 驗證條件
            boolean allCorrect = filtered.stream()
                .allMatch(r -> r.getDifficulty() == 5 && r.getCookingTime() <= 30);
            
            if (allCorrect) {
                System.out.println("✅ 測試通過: 所有食譜符合條件");
            } else {
                System.out.println("❌ 測試失敗: 有食譜不符合條件");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 測試 10: 驗證食材資料完整性 ==========");
        try {
            Recipe recipe = dao.findById(1);  // 番茄炒蛋
            
            if (recipe != null && recipe.getIngredients() != null) {
                System.out.println("檢查食譜「" + recipe.getTitle() + "」的食材:");
                
                boolean allFieldsValid = true;
                for (RecipeIngredient ing : recipe.getIngredients()) {
                    System.out.println("\n  食材: " + ing.getIngredientName());
                    
                    // 檢查必要欄位
                    if (ing.getIngredientId() == null) {
                        System.out.println("    ❌ ingredient_id 為 null");
                        allFieldsValid = false;
                    } else {
                        System.out.println("    ✅ ingredient_id: " + ing.getIngredientId());
                    }
                    
                    if (ing.getIngredientName() == null || ing.getIngredientName().isEmpty()) {
                        System.out.println("    ❌ ingredient_name 為空");
                        allFieldsValid = false;
                    } else {
                        System.out.println("    ✅ ingredient_name: " + ing.getIngredientName());
                    }
                    
                    if (ing.getAmount() == null) {
                        System.out.println("    ❌ amount 為 null");
                        allFieldsValid = false;
                    } else {
                        System.out.println("    ✅ amount: " + ing.getAmount());
                    }
                    
                    if (ing.getUnit() == null || ing.getUnit().isEmpty()) {
                        System.out.println("    ❌ unit 為空");
                        allFieldsValid = false;
                    } else {
                        System.out.println("    ✅ unit: " + ing.getUnit());
                    }
                    
                    if (ing.getCategory() == null || ing.getCategory().isEmpty()) {
                        System.out.println("    ❌ category 為空");
                        allFieldsValid = false;
                    } else {
                        System.out.println("    ✅ category: " + ing.getCategory());
                    }
                }
                
                if (allFieldsValid) {
                    System.out.println("\n✅ 測試通過: 所有食材資料完整");
                } else {
                    System.out.println("\n❌ 測試失敗: 有食材資料不完整");
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ 測試失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========================================");
        System.out.println("所有 RecipeDAO 測試完成!");
        System.out.println("========================================");
    }
}