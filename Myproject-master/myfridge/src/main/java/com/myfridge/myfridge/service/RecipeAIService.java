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

    private static final String API_KEY = "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";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "openai/gpt-4o-mini";
    // private static final String MODEL = "google/gemini-2.0-flash-exp:free";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    
    // 預設的菜系列表（當沒有指定時隨機選）
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
    
    public Map<String, Object> generateRecipe(String cuisine, List<String> excludeTitles) {
        try {
            String prompt = buildPrompt(cuisine, excludeTitles);
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

    //舊方法：保持相容性
    public Map<String, Object> generateRecipe(String cuisine) {
        return generateRecipe(cuisine, new ArrayList<>());
    }
    
    private String buildPrompt(String cuisine, List<String> excludeTitles ) {

        String cuisineText;
        if (cuisine != null && !cuisine.isEmpty()) {
            cuisineText = cuisine;
        } else {
            cuisineText = CUISINE_OPTIONS.get(random.nextInt(CUISINE_OPTIONS.size()));
        }
        
        int randomNumber = random.nextInt(10000);
        
        //建立排除清單
        String excludeText = "";
        if (!excludeTitles.isEmpty()) {
            excludeText = "\n 禁止生成以下已出現的菜名：\n" + 
                          String.join("、", excludeTitles) + 
                          "\n你必須生成完全不同的菜色，不可重複上述菜名或類似變體。\n";
        }
            
        return String.format("""
            重要限制：你必須生成一道「%s」的道地料理，絕對不可以使用其他菜系的食材或調味料。
            %s
            
            請生成一個「%s」的食譜，並用以下 JSON 格式回傳：
            {
              "title": "具體的菜名",
              "description": "口味特色描述",
              "imageUrl": "https://images.unsplash.com/photo-1546069901-ba9599a7e63c",
              "cookingTime": 30,
              "difficulty": 3,
              "step": "1. 準備食材：洗淨所有蔬菜，將肉類切塊備用。\\n\\n2. 醃製：將肉類加入醬油、料酒醃製10分鐘。\\n\\n3. 爆香：熱鍋下油，爆香蔥薑蒜。"
              "ingredients": [
                {"name": "番茄", "category": "vegetable", "amount": 2.0, "unit": "顆"}
              ]
            }
            
            規則：
            1. 只回傳 JSON，不要 Markdown
            2. title 必須是「%s」的道地菜名，10 字內
            3. description 強調「%s」的傳統風味
            4. cookingTime 是分鐘數（5-120）
            5. difficulty 等級（1=入門、2=基礎、3=進階、4=功夫、5=專業）
            6. step 格式必須遵守以下格式：
            - 每個步驟之間用「\\n\\n」分隔（兩個換行）
            - 總共 3-8 個步驟
            - 每個步驟包含：步驟編號 + 名稱 + 冒號 + 說明
            - 請完全按照 JSON 範例中 step 的格式，不要重複「步驟 X」
            7. ingredients 必須 5-10 個，且符合「%s」傳統食材
            8. category：vegetable、fruit、meat、seafood、egg、bean、oil、dairy、seasoning、other
            9. name 用中文，amount 用小數，unit 用中文單位
            10. 請生成完全不同的食譜（編號：%d）
            11. 這必須是純正的「%s」料理，不可混搭
            """, 
            cuisineText, excludeText, cuisineText,
            cuisineText, cuisineText, cuisineText,
            randomNumber, cuisineText
        );
    }
    
    private String buildRequest(String prompt) throws Exception {

      Map<String, Object> request = new HashMap<>();
      request.put("model", MODEL);
      request.put("temperature", 0.3);  //降低，提高穩定性
      request.put("max_tokens", 1000);
      
      Map<String, String> systemMsg = new HashMap<>();
      systemMsg.put("role", "system");
      systemMsg.put("content", "你是專精全球道地美食的主廚。你堅持各國料理的純正血統，絕不混搭。你必須嚴格遵守用戶指定的菜系限制。");
      
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
        String step = recipeJson.get("step").asText();
        step = cleanDuplicateStepTitles(step);
        recipe.put("step", step);
        
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
    
    //驗證並標準化 category

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
            case "beans", "bean", "legume", "legumes", "tofu", "豆類", "豆", "豆腐", "豆製品" -> "bean";
            case "oil", "oils", "fat", "fats", "油", "油類", "食用油" -> "oil";
            default -> "other";
        };
    }

    private String cleanDuplicateStepTitles(String step) {
      // 移除連續重複的「步驟 X」
      // 例如：「步驟 1\n步驟 1\n1. ...」 → 「步驟 1\n1. ...」
      step = step.replaceAll("(步驟 \\d+)\\n\\1", "$1");
      
      return step;
    }
}