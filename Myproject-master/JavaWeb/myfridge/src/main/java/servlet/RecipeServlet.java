package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.Recipe;
import model.RecipeIngredient;
import service.RecipeService;


@WebServlet("/api/recipes/*")
public class RecipeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RecipeService recipeService;
	private Gson gson;
	
	public void init() throws ServletException{
		recipeService = new RecipeService();
		gson = new GsonBuilder()
	            .disableHtmlEscaping()  // 正確顯示中文
	            .create();
	}
	//處理跨域的問題
	private void setCrossHeader(HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
	}
	
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
		setCrossHeader(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
	//Servlet處理完請求後，將結果回傳給前端的方法。
    private void sendJsonResponse(HttpServletResponse resp, int statusCode, Object data)
    		throws IOException {
    	setCrossHeader(resp);
    	resp.setContentType("application/json; charset = UTF-8");
    	resp.setStatus(statusCode);
    	resp.getWriter().write(gson.toJson(data));
    }
    //Servlet處理完請求後如果報錯，將error回傳給前端的方法。
    private void sendErrorResponse(HttpServletResponse resp, int statusCode,String message)
    		throws IOException {
    	setCrossHeader(resp);
    	resp.setContentType("application/json; charset = UTF-8");
    	resp.setStatus(statusCode);
    	JsonObject error = new JsonObject();
    	error.addProperty("error", message);
    	resp.getWriter().write(gson.toJson(error));
    }
    
    private JsonObject recipeToJson(Recipe recipe) {
    	JsonObject json = new JsonObject();
    	json.addProperty("id", recipe.getId());
        json.addProperty("user_id", recipe.getUserId());
        json.addProperty("creator_id", recipe.getUserId() != null ? recipe.getUserId() : 0);  // 前端用 creator_id
        json.addProperty("title", recipe.getTitle());
        json.addProperty("description", recipe.getDescription());
        json.addProperty("image_url", recipe.getImageURL());
        json.addProperty("cooking_time", recipe.getCookingTime());
        json.addProperty("difficulty", recipe.getDifficulty());
        json.addProperty("step", recipe.getStep());
        json.addProperty("is_public", recipe.getIsPublic());
        
     // 轉換 ingredients (amount → quantity)
        JsonArray ingredientsArray = new JsonArray();
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ing : recipe.getIngredients()) {
                JsonObject ingJson = new JsonObject();
                ingJson.addProperty("ingredient_id", ing.getIngredientId());
                ingJson.addProperty("ingredient_name", ing.getIngredientName());
                ingJson.addProperty("quantity", ing.getAmount());  //amount → quantity
                ingJson.addProperty("unit", ing.getUnit());
                ingJson.addProperty("category", ing.getCategory());
                ingredientsArray.add(ingJson);
            }
        }
        json.add("ingredients", ingredientsArray);
    	return json;
    }
    private JsonArray recipesToJsonArray(List<Recipe> recipes) {
        JsonArray array = new JsonArray();
        for (Recipe recipe : recipes) {
            array.add(recipeToJson(recipe));
        }
        return array;
    }

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
	    String pathInfo = req.getPathInfo();
	    try{
	    	// ========== 1. GET /api/recipes - 查詢所有食譜 ==========
	    	if(pathInfo == null || pathInfo.equals("/")) {
	    		List<Recipe> recipes = recipeService.getAllRecipes();
	    		JsonArray jsonArray = recipesToJsonArray(recipes);
	    		sendJsonResponse(resp, 200, jsonArray);
                return;
	    	}
	    	// ========== 2. GET /api/recipes/search?keyword=xxx - 搜尋 ==========
	    	if(pathInfo.equals("/search")) {
	    		String keyword = req.getParameter("keyword");
	    		if(keyword == null || keyword.trim().isEmpty()) {
	    			List<Recipe> recipes = recipeService.getAllRecipes();
		    		JsonArray jsonArray = recipesToJsonArray(recipes);
		    		sendJsonResponse(resp, 200, jsonArray); 
	    		}else {
	    			List<Recipe> recipes = recipeService.searchRecipes(keyword);
		    		JsonArray jsonArray = recipesToJsonArray(recipes);
		    		sendJsonResponse(resp, 200, jsonArray);
	    		}
	    		return;
	    	}
	    	// ========== 3. GET /api/recipes/filter?difficulty=3&minCookingTime=60 - 篩選 ==========
	    	if(pathInfo.equals("/filter")) {
				String difficultyStr = req.getParameter("difficulty");
				String minCookingTimeStr = req.getParameter("minCookingTime");
				String maxCookingTimeStr = req.getParameter("maxCookingTime");
				Integer difficulty = difficultyStr != null ? Integer.parseInt(difficultyStr) : null;
                Integer minCookingTime = minCookingTimeStr != null ? Integer.parseInt(minCookingTimeStr) : null;
                Integer maxCookingTime = maxCookingTimeStr != null ? Integer.parseInt(maxCookingTimeStr) : null;

    			List<Recipe> recipes = recipeService.filterRecipes(difficulty, minCookingTime, maxCookingTime);
	    		JsonArray jsonArray = recipesToJsonArray(recipes);
	    		sendJsonResponse(resp, 200, jsonArray); 
	    		return;
	    	}
	    	// ========== 4. GET /api/recipes/{id} - 查詢單一食譜 ==========
            // pathInfo 例如: "/1", "/12"
	    	try {
	            Integer recipeId = Integer.parseInt(pathInfo.substring(1));
	            Recipe recipe = recipeService.getRecipeDetail(recipeId);
	            if(recipe == null) {
	            	 sendErrorResponse(resp, 404, "找不到食譜 ID: " + recipeId);
	            	 return;
	            }else {
	            	 JsonObject json = recipeToJson(recipe);
	                 sendJsonResponse(resp, 200, json);
	            }
	                
	        }catch(NumberFormatException e) {
                sendErrorResponse(resp, 400, "無效的食譜 ID");
                return;
	        }   
	    }catch(IllegalArgumentException e) {
	    	sendErrorResponse(resp, 400, e.getMessage());
	    }
	    catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "伺服器錯誤: " + e.getMessage());
        }
	}
}
