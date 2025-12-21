package com.myfridge.myfridge.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RecipeAIService {

    private static final String API_KEY = System.getenv("OPENROUTER_API_KEY");
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "openai/gpt-4o-mini";
    // private static final String MODEL = "google/gemini-2.0-flash-exp:free";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    
    // ✅ 預設的菜系列表（當沒有指定時隨機選）
    private static final List<String> CUISINE_OPTIONS = Arrays.asList(
        "台灣小吃",
        "台式熱炒",
        "日式料理",
        "韓式料理",
        "泰式料理",
        "義大利麵",
        "中式家常菜",
        "粵菜",
        "川菜",
        "湘菜",
        "東南亞料理",
        "法式料理",
        "美式料理",
        "墨西哥料理",
        "印度料理",
        "越南料理",
        "地中海料理",
        "素食料理"
    );
    
    public Map<String, Object> generateRecipe(String cuisine) {
        try {
            String prompt = buildPrompt(cuisine);
            String requestBody = buildRequest(prompt);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("HTTP-Referer", "http://localhost:8080");
            headers.set("X-Title", "Stock & Stove");
            
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                API_URL, entity, String.class
            );
            
            return parseResponse(response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("AI 生成失敗: " + e.getMessage(), e);
        }
    }
    
    private String buildPrompt(String cuisine) {
        //如果沒有傳入 cuisine，隨機選一個
        String cuisineText;
        if (cuisine != null && !cuisine.isEmpty()) {
            cuisineText = cuisine;
        } else {
            cuisineText = CUISINE_OPTIONS.get(random.nextInt(CUISINE_OPTIONS.size()));
        }
        
        //加上隨機數字，讓每次都不同
        int randomNumber = random.nextInt(10000);
            
        return String.format("""
            請生成一個「%s」的食譜，並用以下 JSON 格式回傳：
            
            {
              "title": "具體的菜名（非料理種類名稱）",
              "description": "口味特色描述",
              "imageUrl": "https://images.unsplash.com/photo-1546069901-ba9599a7e63c",
              "cookingTime": 數字,
              "difficulty": 1~5,
              "step": "1. 第一步\\n2. 第二步\\n3. 第三步",
              "ingredients": [
                {
                  "name": "番茄",
                  "category": "vegetable",
                  "amount": 2.0,
                  "unit": "顆"
                },
                {
                  "name": "雞蛋",
                  "category": "egg",
                  "amount": 3.0,
                  "unit": "顆"
                }
              ]
            }
            
            規則：
            1. 只回傳 JSON，不要任何 Markdown 或額外文字
            2. title 要吸引人，10 字內
            3. description 包含口味、特色
            4. cookingTime 是分鐘數（5-120）
            5. difficulty 難度等級（1-5），根據以下標準判斷：
           - 1（入門）：
             * 烹飪時間 ≤ 20 分鐘
             * 食材 ≤ 5 種
             * 只需要基本烹飪技巧（切、炒、煮）
             * 不需要特殊調味或醬料
             * 例如：炒蛋、水煮青菜、泡麵
           
           - 2（基礎）：
             * 烹飪時間 20-30 分鐘
             * 食材 5-7 種
             * 需要基本調味（鹽、醬油、糖等）
             * 簡單的烹飪技巧組合（炒 + 調味）
             * 例如：番茄炒蛋、青椒炒肉絲、蒜蓉空心菜
           
           - 3（進階）：
             * 烹飪時間 30-45 分鐘
             * 食材 7-10 種
             * 需要多種調味料
             * 需要掌握火候、時間控制
             * 可能需要醃製、預處理
             * 例如：宮保雞丁、糖醋排骨、三杯雞
           
           - 4（功夫）：
             * 烹飪時間 45-90 分鐘
             * 食材 > 10 種
             * 需要複雜的烹飪技巧（煎、炸、燉、滷）
             * 需要特殊醬料或高湯
             * 需要精確的時間和溫度控制
             * 例如：紅燒獅子頭、佛跳牆、東坡肉
           
           - 5（專業）：
             * 烹飪時間 > 90 分鐘
             * 食材 > 12 種
             * 需要專業技巧（雕花、拉麵、刀工）
             * 需要多階段烹飪（醃製 → 滷製 → 燉煮）
             * 需要特殊器具或專業知識
             * 例如：北京烤鴨、佛跳牆、手工拉麵、法式舒芙蕾
            6. step 用 \\n 分隔，3-8 個步驟，第一步要包含食材準備
            7. ingredients 必須 5-10 個
            8. ⚠️ category 必須是以下英文之一（非常重要）：
               - vegetable（蔬菜）
               - fruit（水果）
               - meat（肉類）
               - seafood（海鮮）
               - egg（蛋類）
               - dairy（乳製品）
               - seasoning（調味料）
               - other（其他）
            9. name 用中文
            10. amount 用小數（2.0, 0.5 等）
            11. unit 用中文常見單位：顆、克、毫升、大匙、小匙、片、條、根
            12.請生成完全不同、有創意的食譜，避免重複（編號：%d）
            13.菜名必須具體，不要只說「%s料理」，要有具體的菜名
            14.必須嚴格符合菜系的傳統風味與調味邏輯。
            """, cuisineText, randomNumber, cuisineText);
    }
    
    private String buildRequest(String prompt) throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("model", MODEL);
        
        // ✅ 提高 temperature 到 1.0（最大變化）
        request.put("temperature", 0.5);
        request.put("max_tokens", 1000);
        
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一位專精於全球各國道地美食的主廚。" + 
        "你的強項在於保持各國料理的純正血統。你拒絕任何不合理的混搭（Fusion），" +
        "當被要求法式時，你只會用法式香料與技法；當被要求義式時，你絕對不會用到亞洲調味料。");
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        
        request.put("messages", Arrays.asList(systemMsg, userMsg));
        
        return objectMapper.writeValueAsString(request);
    }
    
    private Map<String, Object> parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        String content = root.get("choices").get(0)
            .get("message").get("content").asText();
        
        // 清理 Markdown
        content = content
            .replaceAll("```json\\n?", "")
            .replaceAll("```\\n?", "")
            .trim();
        
        // 解析 JSON
        JsonNode recipeJson = objectMapper.readTree(content);
        
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("title", recipeJson.get("title").asText());
        recipe.put("description", recipeJson.get("description").asText());
        recipe.put("imageUrl", recipeJson.get("imageUrl").asText());
        recipe.put("cookingTime", recipeJson.get("cookingTime").asInt());
        recipe.put("difficulty", recipeJson.get("difficulty").asInt());
        recipe.put("step", recipeJson.get("step").asText());
        
        // 解析食材陣列
        List<Map<String, Object>> ingredients = new ArrayList<>();
        JsonNode ingredientsNode = recipeJson.get("ingredients");
        for (JsonNode ing : ingredientsNode) {
            Map<String, Object> ingredient = new HashMap<>();
            ingredient.put("name", ing.get("name").asText());
            
            String category = ing.get("category").asText().toLowerCase();
            ingredient.put("category", validateCategory(category));
            
            ingredient.put("amount", ing.get("amount").asDouble());
            ingredient.put("unit", ing.get("unit").asText());
            ingredients.add(ingredient);
        }
        recipe.put("ingredients", ingredients);
        
        return recipe;
    }
    
    /**
     * 驗證並標準化 category
     */
    private String validateCategory(String category) {
        category = category.trim().toLowerCase();
        
        return switch (category) {
            case "vegetable", "vegetables", "veggie", "veggies", "蔬菜" -> "vegetable";
            case "fruit", "fruits", "水果" -> "fruit";
            case "meat", "meats", "肉類" -> "meat";
            case "seafood", "seafoods", "fish", "海鮮" -> "seafood";
            case "egg", "eggs", "蛋類" -> "egg";
            case "dairy", "milk", "乳製品" -> "dairy";
            case "seasoning", "seasonings", "spice", "spices", "condiment", "調味料" -> "seasoning";
            default -> "other";
        };
    }
}