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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import model.FridgeItem;
import service.FridgeItemService;
import service.IngredientService;
import java.lang.reflect.Type;

@WebServlet("/api/fridge-items/*")
public class FridgeItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FridgeItemService fridgeItemService;
	private Gson gson;
	
	@Override
	public void init() {
		fridgeItemService = new FridgeItemService();
		gson = new GsonBuilder()
				.disableHtmlEscaping()
	            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
	                @Override
	                public JsonElement serialize(LocalDate date, Type type, 
	                                            JsonSerializationContext context) {
	                    return date != null ? new JsonPrimitive(date.toString()) : null;
	                }
	            })
	            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
	                @Override
	                public LocalDate deserialize(JsonElement json, Type type, 
	                                            JsonDeserializationContext context) {
	                    return json != null ? LocalDate.parse(json.getAsString()) : null;
	                }
	            })
	            .create();
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
    	if(session == null|| session.getAttribute("id")==null) {
    		sendErrorResponse(resp,401,"尚未登入!");
    		return null;
    	}
    	return (Integer)session.getAttribute("id");
    }
    
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
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
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
		try {
			Integer userId = getUserIdFromSession(req, resp);
			if(userId == null) {
				return;
			}
			String requestBody = getRequestBody(req);
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			String ingredientName = json.get("ingredientName").getAsString();
			String category = json.get("category").getAsString();
			Double amount = json.get("amount").getAsDouble();
			String unit = json.get("unit").getAsString();
			String purchasedDateStr = json.get("purchasedDate").getAsString();
			String expiredDateStr = json.has(" expiredDate") && !json.get(" expiredDate").isJsonNull()
	                ? json.get(" expiredDate").getAsString() : null;
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
	
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
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
			
			Double amount = json.get("amount").getAsDouble();
			String unit = json.get("unit").getAsString();
			String purchasedDateStr = json.get("purchasedDate").getAsString();
			String expiredDateStr = json.has("expiredDate") && !json.get("expiredDate").isJsonNull()
	                ? json.get("expiredDate").getAsString() : null;
			LocalDate purchasedDate = LocalDate.parse(purchasedDateStr);
			LocalDate expiredDate = expiredDateStr != null && !expiredDateStr.isEmpty()
	                ? LocalDate.parse(expiredDateStr): null;
			FridgeItem item = fridgeItemService.updateItem(userId,id, amount, unit, purchasedDate, expiredDate);
			sendJsonResponse(resp,200,item);
		}catch (NumberFormatException e) {
            sendErrorResponse(resp, 400, "ID 格式錯誤");
        }catch(Exception e) {
			e.printStackTrace();
			sendErrorResponse(resp,500,"更新失敗!"+e.getMessage());
		}
	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
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
			Boolean success = fridgeItemService.deleteItem(userId,id);
			if (success) {
			    JsonObject response = new JsonObject();
			    response.addProperty("message", "刪除成功");
			    sendJsonResponse(resp, 200, response);
			} else {
			    sendErrorResponse(resp, 404, "找不到此食材");
			}
		}catch (NumberFormatException e) {
            sendErrorResponse(resp, 400, "ID 格式錯誤");
        }catch(Exception e) {
			e.printStackTrace();
			sendErrorResponse(resp,500,"刪除失敗!"+e.getMessage());
		}
	}
	
	
}
