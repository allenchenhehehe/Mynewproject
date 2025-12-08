package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.ShoppingItem;
import service.ShoppingListService;

@WebServlet("/api/shopping-list/*")
public class ShoppingListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ShoppingListService service;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        this.service = new ShoppingListService();
        this.gson = new Gson();
    }
    private void setCrossHeader(HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
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
    	JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("error", message);
        
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(statusCode);
        
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(error));
        out.flush();
    }
    private JsonObject parseJsonRequest(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        
        return JsonParser.parseString(sb.toString()).getAsJsonObject();
    }
    
       
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
				req.setCharacterEncoding("UTF-8");
			    resp.setCharacterEncoding("UTF-8");
				HttpSession session = req.getSession(false);
				if(session == null || session.getAttribute("userId") == null){
					 sendErrorResponse(resp, 401, "請先登入");
			         return;
				}
				Integer userId = (Integer)session.getAttribute("userId");
				try {
					List<ShoppingItem> items = service.getShoppingList(userId);
					//回傳json
					sendJsonResponse(resp, 200, items); 
				}catch (Exception e) {
		            e.printStackTrace();
		            sendErrorResponse(resp, 500, "查詢購物清單失敗: " + e.getMessage());
		        }
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
				req.setCharacterEncoding("UTF-8");
			    resp.setCharacterEncoding("UTF-8");
				HttpSession session = req.getSession(false);
				if(session == null || session.getAttribute("userId") == null){
					 sendErrorResponse(resp, 401, "請先登入");
			         return;
				}
				Integer userId = (Integer) session.getAttribute("userId");		        
		        String pathInfo = req.getPathInfo();   
		        if (pathInfo != null && pathInfo.equals("/clear-purchased")) {
		            handleClearPurchased(req, resp, userId);
		            return;
		        }	        
		        handleAddItem(req, resp, userId);		        
	}
	private void handleAddItem(HttpServletRequest req, HttpServletResponse resp, Integer userId) 
            throws IOException {
        
        try {
            //使用輔助方法解析 JSON
            JsonObject json = parseJsonRequest(req);
            
            // 建立 ShoppingItem
            ShoppingItem item = new ShoppingItem();
            
            // 選填欄位
            if (json.has("recipe_id") && !json.get("recipe_id").isJsonNull()) {
                item.setRecipeId(json.get("recipe_id").getAsInt());
            }
            
            if (json.has("recipe_name") && !json.get("recipe_name").isJsonNull()) {
                item.setRecipeName(json.get("recipe_name").getAsString());
            }
            
            if (json.has("ingredient_id") && !json.get("ingredient_id").isJsonNull()) {
                item.setIngredientId(json.get("ingredient_id").getAsInt());
            }
            
            // 必填欄位
            item.setIngredientName(json.get("ingredient_name").getAsString());
            item.setAmount(json.get("quantity").getAsDouble());
            item.setUnit(json.get("unit").getAsString());
            item.setCategory(json.get("category").getAsString());
            
            // 呼叫 Service
            ShoppingItem added = service.addItem(userId, item);
            
            //使用輔助方法回傳 JSON
            sendJsonResponse(resp, 201, added);
            
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "新增購物項目失敗: " + e.getMessage());
        }
    }
	
	private void handleClearPurchased(HttpServletRequest req, HttpServletResponse resp, Integer userId) 
            throws IOException {
        
        try {
            int count = service.clearPurchasedAndAddToFridge(userId);
            
            // 建立回應物件
            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("count", count);
            result.addProperty("message", "成功加入 " + count + " 個項目到冰箱");
            
            // 使用輔助方法回傳 JSON
            sendJsonResponse(resp, 200, result);
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "清除已購買項目失敗: " + e.getMessage());
        }
    }
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			req.setCharacterEncoding("UTF-8");
		    resp.setCharacterEncoding("UTF-8");
			HttpSession session = req.getSession(false);
			if(session == null || session.getAttribute("userId") == null){
				 sendErrorResponse(resp, 401, "請先登入");
		         return;
			}
			Integer userId = (Integer)session.getAttribute("userId");
			String pathInfo = req.getPathInfo();  
	        if (pathInfo == null || pathInfo.equals("/")) {
	        	sendErrorResponse(resp, 400, "缺少項目 ID");
	            return;
	        }	        
	        try {
//	        	if (pathInfo == null || pathInfo.length() <= 1) {
//	                sendErrorResponse(resp, 400, "缺少項目 ID");
//	                return;
//	            }
	        	Integer id = Integer.parseInt(pathInfo.substring(1));
//	        	System.out.println("解析到的 ID: " + id);
	        	JsonObject json = parseJsonRequest(req);
	        	ShoppingItem update = new ShoppingItem();
	        	update.setIngredientName(json.get("ingredient_name").getAsString());
	        	update.setAmount(json.get("quantity").getAsDouble());
	        	update.setUnit(json.get("unit").getAsString());
	        	update.setCategory(json.get("category").getAsString());
	        	update.setIsPurchased(json.get("is_purchased").getAsBoolean());
	        	ShoppingItem updated = service.updateItem(userId, id, update);
	        	sendJsonResponse(resp, 200, updated);
	        	
	        }catch (NumberFormatException e) {
	            sendErrorResponse(resp, 400, "無效的項目 ID");
	        } catch (IllegalArgumentException e) {
	            sendErrorResponse(resp, 400, e.getMessage());
	        } catch (RuntimeException e) {
	            sendErrorResponse(resp, 404, e.getMessage());
	        } catch (Exception e) {
	            e.printStackTrace();
	            sendErrorResponse(resp, 500, "更新購物項目失敗: " + e.getMessage());
	        }		
	}
	
	protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String method = req.getMethod();
        
        if (method.equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
    
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
    	req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
        // 檢查 Session
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(resp, 401, "請先登入");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, 400, "缺少項目 ID");
            return;
        }
        
        try {
            // 解析 ID
            Integer id = Integer.parseInt(pathInfo.substring(1));
            
            //使用輔助方法解析 JSON
            JsonObject json = parseJsonRequest(req);
            Boolean isPurchased = json.get("is_purchased").getAsBoolean();
            
            //呼叫 Service
            ShoppingItem updated = service.togglePurchase(userId, id, isPurchased);
            
            //使用輔助方法回傳 JSON
            sendJsonResponse(resp, 200, updated);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, 400, "無效的項目 ID");
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, 400, e.getMessage());
        } catch (RuntimeException e) {
            sendErrorResponse(resp, 404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "更新購買狀態失敗: " + e.getMessage());
        }
    }
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession(false);
		if(session == null || session.getAttribute("userId") == null){
			 sendErrorResponse(resp, 401, "請先登入");
	         return;
		}
		Integer userId =  (Integer)session.getAttribute("userId");
		String pathInfo = req.getPathInfo();   
        if (pathInfo == null || pathInfo.equals("/")) {
        	sendErrorResponse(resp, 400, "缺少項目 ID");
            return;
        }	        
        try {
        	Integer id = Integer.parseInt(pathInfo.substring(1));
        	service.deleteItem(userId, id);
        	JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("message", "刪除成功");
        	sendJsonResponse(resp, 200, result);
        	
        }catch (NumberFormatException e) {
            sendErrorResponse(resp, 400, "無效的項目 ID");
        } catch (RuntimeException e) {
            sendErrorResponse(resp, 404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "刪除購物項目失敗: " + e.getMessage());
        }
	}
	
	

}
