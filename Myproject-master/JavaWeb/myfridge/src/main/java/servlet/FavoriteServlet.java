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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.Favorite;
import service.FavoriteService;
import service.ShoppingListItemService;


@WebServlet("/api/favorites/*")
public class FavoriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FavoriteService service;
    private Gson gson;
    
    public void init() throws ServletException {
        this.service = new FavoriteService();
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
    
    private Integer getUserIdFromSession(HttpServletRequest req) throws IOException {
        HttpSession session = req.getSession(false);
        if(session == null) {
        	return null;
        }
        return (Integer)session.getAttribute("id");
    }
    
    private JsonObject favoriteToJson(Favorite favorite) {
    	JsonObject json = new JsonObject();
    	json.addProperty("id", favorite.getId());
    	json.addProperty("userId", favorite.getUserId());
    	json.addProperty("recipeId", favorite.getRecipeId());
    	json.addProperty("savedAt", favorite.getSavedAt() != null ? favorite.getSavedAt().toString() : null);
    	json.addProperty("title", favorite.getTitle());
    	json.addProperty("description", favorite.getDescription());
    	json.addProperty("imageUrl", favorite.getImageUrl());
    	json.addProperty("cookingTime", favorite.getCookingTime());
    	json.addProperty("difficulty", favorite.getDifficulty());
    	return json;
    }
    
    private JsonArray favoritesToJsonArray(List<Favorite> favorites) {
    	JsonArray ja = new JsonArray();
    	for(Favorite fav : favorites) {
    		ja.add(favoriteToJson(fav));
    	}
    	return ja;
    }
       
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
		String pathInfo = req.getPathInfo();
		try {
			Integer userId = getUserIdFromSession(req);
			if(userId == null) {
				sendErrorResponse(resp, 401, "請先登入");
                return;
			}
			
			//1. GET /api/favorites - 取得使用者的所有收藏 ==========
			if(pathInfo == null || pathInfo.equals("/")) {
				List<Favorite> favs = service.getFavorites(userId);
				JsonArray ja = favoritesToJsonArray(favs);
				sendJsonResponse(resp, 200, ja);
                return;//不執行之後的敘述
			}
			
			//2. GET /api/favorites/check/{recipeId} - 檢查是否已收藏
			if(pathInfo.startsWith("/check/")) {
				Integer recipeId = Integer.parseInt(pathInfo.substring(7));
				Boolean isFavorite = service.isFavorited(userId, recipeId);
				JsonObject json = new JsonObject();
				json.addProperty("isFavorite", isFavorite);
				sendJsonResponse(resp, 200, json);
				return;
			}
			
			sendErrorResponse(resp, 404, "找不到此資源");
			
		}catch (IllegalArgumentException e) {
            sendErrorResponse(resp, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "伺服器錯誤: " + e.getMessage());
        }
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
	    String pathInfo = req.getPathInfo();
	    try {
			Integer userId = getUserIdFromSession(req);
			if(userId == null) {
				sendErrorResponse(resp, 401, "請先登入");
                return;
			}
			// POST /api/favorites/{recipeId} - 新增收藏 
			//\\d是個位數數字格式，後面有個+代表不只有個位數，可以好幾位
			if(pathInfo != null && pathInfo.matches("/\\d+")) {
				try {
					Integer recipeId = Integer.parseInt(pathInfo.substring(1));
					Favorite fav = service.addFavorite(userId, recipeId);
					JsonObject json = favoriteToJson(fav);
					sendJsonResponse(resp, 201, json);
					return;
				}catch(NumberFormatException e) {
					 sendErrorResponse(resp, 400, "無效的食譜 ID");
	                 return;
				}	
			}
			
			// POST /api/favorites/toggle/{recipeId} - 切換收藏狀態
			if(pathInfo != null && pathInfo.startsWith("/toggle/")) {
				try {
					Integer recipeId = Integer.parseInt(pathInfo.substring(8));
					Boolean newStatus = service.toggleFavorite(userId, recipeId);
					JsonObject json = new JsonObject();
					json.addProperty("isFavorite", newStatus);	
					json.addProperty("message", newStatus ? "收藏成功" : "取消收藏成功");
					sendJsonResponse(resp, 200, json);
					return;
				}catch (NumberFormatException e) {
                    sendErrorResponse(resp, 400, "無效的食譜 ID");
                    return;
                }
			}
			
			sendErrorResponse(resp, 404, "找不到此資源");
			
	    }catch (IllegalArgumentException e) {
            sendErrorResponse(resp, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "伺服器錯誤: " + e.getMessage());
        }
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
	    String pathInfo = req.getPathInfo();
	    
	    try {
			Integer userId = getUserIdFromSession(req);
			if(userId == null) {
				sendErrorResponse(resp, 401, "請先登入");
                return;
			}
			// DELETE /api/favorites/{recipeId} - 刪除收藏 
			//\\d是個位數數字格式，後面有個+代表不只有個位數，可以好幾位
			if(pathInfo != null && pathInfo.matches("/\\d+")) {
				try {
					Integer recipeId = Integer.parseInt(pathInfo.substring(1));
					service.deleteFavorite(userId, recipeId);
					JsonObject json = new JsonObject();
					json.addProperty("message","取消收藏成功");
					sendJsonResponse(resp, 200, json);
					return;
				}catch(NumberFormatException e) {
					 sendErrorResponse(resp, 400, "無效的食譜 ID");
	                 return;
				}	
			}
			
			sendErrorResponse(resp, 404, "找不到此資源");
			
	    }catch (IllegalArgumentException e) {
            sendErrorResponse(resp, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "伺服器錯誤: " + e.getMessage());
        }
	}
}
