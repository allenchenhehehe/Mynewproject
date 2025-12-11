package servlet;

import java.io.BufferedReader;
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
import com.google.gson.JsonParser;

import model.Comment;
import service.CommentService;


@WebServlet("/api/comments/*")
public class CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CommentService service;
    private Gson gson;
    
    public void init() throws ServletException {
        this.service = new CommentService();
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
    
    private JsonObject commentToJson(Comment comment) {
    	JsonObject json = new JsonObject();
    	json.addProperty("id", comment.getId());
    	json.addProperty("userId", comment.getUserId());
    	json.addProperty("recipeId", comment.getRecipeId());
    	json.addProperty("rating",comment.getRating());
    	json.addProperty("text", comment.getText());
    	json.addProperty("createdAt", comment.getCreatedAt() != null ?comment.getCreatedAt().toString() : null);
    	json.addProperty("updatedAt", comment.getUpdatedAt() != null ? comment.getUpdatedAt().toString() : null);
    	json.addProperty("userName", comment.getUserName());
    	json.addProperty("recipeTitle", comment.getRecipeTitle());
    	return json;
    }
    
    private JsonArray commentsToJsonArray(List<Comment> comments) {
    	JsonArray ja = new JsonArray();
    	for(Comment com : comments) {
    		ja.add(commentToJson(com));
    	}
    	return ja;
    }

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
		String pathInfo = req.getPathInfo();
		try {
		
			//1. GET /api/comments/recipe/{recipeId} - 取得食譜的所有評論
			if(pathInfo != null && pathInfo.startsWith("/recipe/")) {
				try {
					Integer recipeId = Integer.parseInt(pathInfo.substring(8));
					List<Comment> coms = service.getCommentsByRecipe(recipeId);
					JsonArray ja = commentsToJsonArray(coms);
					sendJsonResponse(resp, 200, ja);
	                return;//不執行之後的敘述
				}catch (NumberFormatException e) {
                    sendErrorResponse(resp, 400, "無效的食譜 ID");
                    return;
                }
			}
			
			//2. GET /api/comments/user - 取得使用者的所有評論
			if(pathInfo == null || pathInfo.equals("/user") || pathInfo.equals("/user/")) {
				Integer userId = getUserIdFromSession(req);
				if (userId == null) {
                    sendErrorResponse(resp, 401, "請先登入");
                    return;
                }
				List<Comment> coms = service.getCommentsByUser(userId);
				JsonArray ja = commentsToJsonArray(coms);		
				sendJsonResponse(resp, 200, ja);
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
			if (userId == null) {
            sendErrorResponse(resp, 401, "請先登入");
            return;
			}
			
			if(pathInfo == null || pathInfo.equals("/")) {
				//SON 是以串流 (Stream) 的方式傳送，需要一行一行讀取
				String rb = getRequestBody(req);
				//將 JSON 字串轉換成 JsonObject
				JsonObject requestBody = JsonParser.parseString(rb).getAsJsonObject();
				//取出 JSON 裡的欄位
				Integer recipeId = requestBody.get("recipeId").getAsInt();
                Integer rating = requestBody.get("rating").getAsInt();
                String text = requestBody.get("text").getAsString();
                //呼叫 Service 新增評論
                Comment comment = service.addComment(userId, recipeId, rating, text);            
                //將 Comment 轉換成 JSON 並回傳
                JsonObject json = commentToJson(comment);
                sendJsonResponse(resp, 201, json);
				return;      
			}
			sendErrorResponse(resp, 404, "找不到此資源");
		} catch (IllegalArgumentException e) {
            sendErrorResponse(resp, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "伺服器錯誤: " + e.getMessage());
        }
		
	}
	
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	    req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
	    String pathInfo = req.getPathInfo();
	    
	    try {
	        Integer userId = getUserIdFromSession(req);
	        if (userId == null) {
	            sendErrorResponse(resp, 401, "請先登入");
	            return;
	        }
	        
	        // PUT /api/comments/{commentId} - 更新評論
	        if (pathInfo != null && pathInfo.matches("/\\d+")) {
	            try {
	                Integer commentId = Integer.parseInt(pathInfo.substring(1));
	                
	                // 讀取 JSON 請求體
	                String rb = getRequestBody(req);
	                JsonObject requestBody = JsonParser.parseString(rb).getAsJsonObject();
	                
	                Integer rating = requestBody.get("rating").getAsInt();
	                String text = requestBody.get("text").getAsString();
	                
	                service.updateComment(commentId, userId, rating, text);
	                
	                JsonObject json = new JsonObject();
	                json.addProperty("message", "評論更新成功");
	                sendJsonResponse(resp, 200, json);
	                return;
	            } catch (NumberFormatException e) {
	                sendErrorResponse(resp, 400, "無效的評論 ID");
	                return;
	            }
	        }
	        
	        sendErrorResponse(resp, 404, "找不到此資源");
	        
	    } catch (IllegalArgumentException e) {
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
			if (userId == null) {
            sendErrorResponse(resp, 401, "請先登入");
            return;
			}
			
			if(pathInfo != null && pathInfo.matches("/\\d+")) {
				try {
                    Integer commentId = Integer.parseInt(pathInfo.substring(1));
                    
                    service.deleteComment(commentId, userId);
                    
                    JsonObject json = new JsonObject();
                    json.addProperty("message", "評論刪除成功");
                    sendJsonResponse(resp, 200, json);
                    return;
                } catch (NumberFormatException e) {
                    sendErrorResponse(resp, 400, "無效的評論 ID");
                    return;
                }  
			}
			sendErrorResponse(resp, 404, "找不到此資源");
		} catch (IllegalArgumentException e) {
            sendErrorResponse(resp, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, 500, "伺服器錯誤: " + e.getMessage());
        }
	}
}
