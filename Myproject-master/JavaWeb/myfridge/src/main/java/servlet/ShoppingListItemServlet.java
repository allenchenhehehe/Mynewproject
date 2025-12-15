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

import model.ShoppingListItem;
import service.ShoppingListItemService;

@WebServlet("/api/shopping-list/*")
public class ShoppingListItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ShoppingListItemService service;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        this.service = new ShoppingListItemService();
        this.gson = new Gson();
    }
    private void setCrossHeader(HttpServletResponse resp, HttpServletRequest req) {
    	String origin = req.getHeader("Origin");
        if (origin != null && origin.startsWith("http://localhost:")) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
        }
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
	}
	
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
		setCrossHeader(resp, req);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
  //Servlet處理完請求後，將結果回傳給前端的方法。
    private void sendJsonResponse(HttpServletResponse resp, HttpServletRequest req, int statusCode, Object data)
    		throws IOException {
    	setCrossHeader(resp, req);
    	resp.setContentType("application/json; charset = UTF-8");
    	resp.setStatus(statusCode);
    	resp.getWriter().write(gson.toJson(data));
    }
    //Servlet處理完請求後如果報錯，將error回傳給前端的方法。
    private void sendErrorResponse(HttpServletResponse resp, HttpServletRequest req, int statusCode,String message)
    		throws IOException {
    	setCrossHeader(resp, req);
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
				if(session == null || session.getAttribute("id") == null){
					 sendErrorResponse(resp, req, 401, "請先登入");
			         return;
				}
				Integer userId = (Integer)session.getAttribute("id");
				try {
					List<ShoppingListItem> items = service.getShoppingList(userId);
					//回傳json
					sendJsonResponse(resp, req, 200, items); 
				}catch (Exception e) {
		            e.printStackTrace();
		            sendErrorResponse(resp, req, 500, "查詢購物清單失敗: " + e.getMessage());
		        }
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
				req.setCharacterEncoding("UTF-8");
			    resp.setCharacterEncoding("UTF-8");
				HttpSession session = req.getSession(false);
				if(session == null || session.getAttribute("id") == null){
					 sendErrorResponse(resp, req, 401, "請先登入");
			         return;
				}
				Integer userId = (Integer) session.getAttribute("id");		        
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
            ShoppingListItem item = new ShoppingListItem();
            
            // 選填欄位
            if (json.has("recipeId") && !json.get("recipeId").isJsonNull()) {
                item.setRecipeId(json.get("recipeId").getAsInt());
            }
            
            if (json.has("recipeName") && !json.get("recipeName").isJsonNull()) {
                item.setRecipeName(json.get("recipeName").getAsString());
            }
            
            if (json.has("ingredientId") && !json.get("ingredientId").isJsonNull()) {
                item.setIngredientId(json.get("ingredientId").getAsInt());
            }
            
            // 必填欄位
            item.setIngredientName(json.get("ingredientName").getAsString());
            item.setAmount(json.get("amount").getAsDouble());
            item.setUnit(json.get("unit").getAsString());
            item.setCategory(json.get("category").getAsString());
            
            // 呼叫 Service
            ShoppingListItem added = service.addItem(userId, item);
            
            //使用輔助方法回傳 JSON
            sendJsonResponse(resp, req, 201, added);
            
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, req, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, req, 500, "新增購物項目失敗: " + e.getMessage());
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
            sendJsonResponse(resp, req, 200, result);
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, req, 500, "清除已購買項目失敗: " + e.getMessage());
        }
    }
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			req.setCharacterEncoding("UTF-8");
		    resp.setCharacterEncoding("UTF-8");
			HttpSession session = req.getSession(false);
			if(session == null || session.getAttribute("id") == null){
				 sendErrorResponse(resp, req, 401, "請先登入");
		         return;
			}
			Integer userId = (Integer)session.getAttribute("id");
			String pathInfo = req.getPathInfo();  
	        if (pathInfo == null || pathInfo.equals("/")) {
	        	sendErrorResponse(resp, req, 400, "缺少項目 ID");
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
	        	ShoppingListItem update = new ShoppingListItem();
	        	update.setIngredientName(json.get("ingredientName").getAsString());
	        	update.setAmount(json.get("amount").getAsDouble());
	        	update.setUnit(json.get("unit").getAsString());
	        	update.setCategory(json.get("category").getAsString());
	        	update.setIsPurchased(json.get("isPurchased").getAsBoolean());
	        	ShoppingListItem updated = service.updateItem(userId, id, update);
	        	sendJsonResponse(resp, req, 200, updated);
	        	
	        }catch (NumberFormatException e) {
	            sendErrorResponse(resp, req, 400, "無效的項目 ID");
	        } catch (IllegalArgumentException e) {
	            sendErrorResponse(resp, req, 400, e.getMessage());
	        } catch (RuntimeException e) {
	            sendErrorResponse(resp, req, 404, e.getMessage());
	        } catch (Exception e) {
	            e.printStackTrace();
	            sendErrorResponse(resp, req, 500, "更新購物項目失敗: " + e.getMessage());
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
        if (session == null || session.getAttribute("id") == null) {
            sendErrorResponse(resp, req, 401, "請先登入");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("id");
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, req, 400, "缺少項目 ID");
            return;
        }
        
        try {
            // 解析 ID
            Integer id = Integer.parseInt(pathInfo.substring(1));
            
            //使用輔助方法解析 JSON
            JsonObject json = parseJsonRequest(req);
            Boolean isPurchased = json.get("isPurchased").getAsBoolean();
            
            //呼叫 Service
            ShoppingListItem updated = service.togglePurchase(userId, id, isPurchased);
            
            //使用輔助方法回傳 JSON
            sendJsonResponse(resp, req, 200, updated);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, req, 400, "無效的項目 ID");
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, req, 400, e.getMessage());
        } catch (RuntimeException e) {
            sendErrorResponse(resp, req, 404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, req, 500, "更新購買狀態失敗: " + e.getMessage());
        }
    }
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession(false);
		if(session == null || session.getAttribute("id") == null){
			 sendErrorResponse(resp, req, 401, "請先登入");
	         return;
		}
		Integer userId =  (Integer)session.getAttribute("id");
		String pathInfo = req.getPathInfo();   
        if (pathInfo == null || pathInfo.equals("/")) {
        	sendErrorResponse(resp, req, 400, "缺少項目 ID");
            return;
        }	        
        try {
        	Integer id = Integer.parseInt(pathInfo.substring(1));
        	service.deleteItem(userId, id);
        	JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("message", "刪除成功");
        	sendJsonResponse(resp, req, 200, result);
        	
        }catch (NumberFormatException e) {
            sendErrorResponse(resp, req, 400, "無效的項目 ID");
        } catch (RuntimeException e) {
            sendErrorResponse(resp, req, 404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, req, 500, "刪除購物項目失敗: " + e.getMessage());
        }
	}
	
	

}
