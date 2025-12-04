package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import model.FridgeItem;
import service.FridgeItemService;
import service.IngredientService;

@WebServlet("/api/fridge-items/*")
public class FridgeItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FridgeItemService fridgeItemService;
	private Gson gson;
	
	@Override
	public void init() {
		fridgeItemService = new FridgeItemService();
		gson = new Gson();
	}
	//處理跨域的問題
	private void setCrossHeader(HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
	} 
	//處理跨域的請求
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		setCrossHeader(resp);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	//讀取 Request Body  
    private String getRequestBody(HttpServletRequest req) throws IOException {
		BufferedReader br = req.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while((line=br.readLine())!=null) {
			sb.append(line);
		}
		return sb.toString();
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
    //從session拿userId
    private Integer getUserIdFromSession(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
    	HttpSession session = req.getSession(false);
    	if(session == null|| session.getAttribute("userId")==null) {
    		sendErrorResponse(resp,401,"尚未登入!");
    		return null;
    	}
    	return (Integer)session.getAttribute("userId");
    }
    
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			Integer userId = getUserIdFromSession(req, resp);
			if(userId == null) {
				return;
			}
			List<FridgeItem> list = fridgeItemService.getAllItems(userId);
			sendJsonResponse(resp,200,list);
		}catch(Exception e) {
			e.printStackTrace();
			sendErrorResponse(resp,500,"伺服器錯誤!"+e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			Integer userId = getUserIdFromSession(req, resp);
			if(userId == null) {
				return;
			}
			String requestBody = getRequestBody(req);
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			String ingredientName = json.get("name").getAsString();
			String category = json.get("category").getAsString();
			Integer amount = json.get("quantity").getAsInt();
			String unit = json.get("unit").getAsString();
			String purchasedDateStr = json.get("purchased_date").getAsString();
			String expiredDateStr = json.has("expired_date") && !json.get("expired_date").isJsonNull()
	                ? json.get("expired_date").getAsString() : null;
			LocalDate purchasedDate = LocalDate.parse(purchasedDateStr);
			LocalDate expiredDate = expiredDateStr != null && !expiredDateStr.isEmpty()
	                ? LocalDate.parse(expiredDateStr): null;
			FridgeItem item = fridgeItemService.addToFridge(userId, ingredientName, category, amount, unit, purchasedDate, expiredDate);
			sendJsonResponse(resp,200,item);
		}catch(Exception e) {
			e.printStackTrace();
			sendErrorResponse(resp,500,"新增失敗!"+e.getMessage());
		}
	}
	
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Integer userId = getUserIdFromSession(req, resp);
			if(userId == null) {
				return;
			}
			String pathInfo = req.getPathInfo();
			if(pathInfo == null || pathInfo.length()<=1) {
				sendErrorResponse(resp,400,"缺少id!");
				return;
			}
			
			Integer id = Integer.parseInt(pathInfo.substring(1));
			String requestBody = getRequestBody(req);
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			
			Integer amount = json.get("quantity").getAsInt();
			String unit = json.get("unit").getAsString();
			String purchasedDateStr = json.get("purchased_date").getAsString();
			String expiredDateStr = json.has("expired_date") && !json.get("expired_date").isJsonNull()
	                ? json.get("expired_date").getAsString() : null;
			LocalDate purchasedDate = LocalDate.parse(purchasedDateStr);
			LocalDate expiredDate = expiredDateStr != null && !expiredDateStr.isEmpty()
	                ? LocalDate.parse(expiredDateStr): null;
			FridgeItem item = fridgeItemService.updateItem(id, amount, unit, purchasedDate, expiredDate);
			sendJsonResponse(resp,200,item);
		}catch (NumberFormatException e) {
            sendErrorResponse(resp, 400, "ID 格式錯誤");
        }catch(Exception e) {
			e.printStackTrace();
			sendErrorResponse(resp,500,"新增失敗!"+e.getMessage());
		}
	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Integer userId = getUserIdFromSession(req, resp);
			if(userId == null) {
				return;
			}
			String pathInfo = req.getPathInfo();
			if(pathInfo == null || pathInfo.length()<=1) {
				sendErrorResponse(resp,400,"缺少id!");
				return;
			}
			
			Integer id = Integer.parseInt(pathInfo.substring(1));
			String requestBody = getRequestBody(req);
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			
			Integer amount = json.get("quantity").getAsInt();
			String unit = json.get("unit").getAsString();
			String purchasedDateStr = json.get("purchased_date").getAsString();
			String expiredDateStr = json.has("expired_date") && !json.get("expired_date").isJsonNull()
	                ? json.get("expired_date").getAsString() : null;
			LocalDate purchasedDate = LocalDate.parse(purchasedDateStr);
			LocalDate expiredDate = expiredDateStr != null && !expiredDateStr.isEmpty()
	                ? LocalDate.parse(expiredDateStr): null;
			FridgeItem item = fridgeItemService.updateItem(id, amount, unit, purchasedDate, expiredDate);
			sendJsonResponse(resp,200,item);
		}catch (NumberFormatException e) {
            sendErrorResponse(resp, 400, "ID 格式錯誤");
        }catch(Exception e) {
			e.printStackTrace();
			sendErrorResponse(resp,500,"新增失敗!"+e.getMessage());
		}
	}
	
	
}
