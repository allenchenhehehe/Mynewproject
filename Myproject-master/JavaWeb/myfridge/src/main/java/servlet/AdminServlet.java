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

import dao.CommentDAO;
import dao.FavoriteDAO;
import dao.RecipeDAO;
import model.Comment;
import model.Favorite;
import service.AdminService;


@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AdminService adminService;
	private Gson gson;
	
	@Override
    public void init() throws ServletException {
        this.adminService = new AdminService();
        this.gson = new Gson();
    }
	
	private void setCrossHeader(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        if (origin != null && origin.startsWith("http://localhost:")) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
        }
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
	
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        setCrossHeader(req, resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
	
	private void sendJsonResponse(HttpServletRequest req, HttpServletResponse resp, 
            int statusCode, Object data) throws IOException {

        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(statusCode);
        resp.getWriter().write(gson.toJson(data));
    }
	
	private void sendErrorResponse(HttpServletRequest req, HttpServletResponse resp, 
            int statusCode, String message) throws IOException {

        JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("error", message);

        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(statusCode);

        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(error));
        out.flush();
    }
	
	//檢查是否為管理員
	private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession(false);
		
		if(session == null || session.getAttribute("userRole") == null) {
			sendErrorResponse(req, resp, HttpServletResponse.SC_UNAUTHORIZED, "未登入");
            return false;
		}
		
		String role = (String)session.getAttribute("userRole");
		if(!"admin".equals(role)) {
			sendErrorResponse(req, resp, HttpServletResponse.SC_FORBIDDEN, "權限不足");
            return false;
		}
		
		return true;
	}
       
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
	    setCrossHeader(req, resp);
	    
		if(!isAdmin(req, resp)) return;
		String pathInfo = req.getPathInfo();
		
		if(pathInfo == null || pathInfo.equals("/")) {
			sendErrorResponse(req, resp, HttpServletResponse.SC_BAD_REQUEST, "無效的路徑");
            return;
		}
		
		if(pathInfo.equals("/comments")) {
			handleGetAllComments(req, resp);
		}
		
		else if(pathInfo.equals("/favorites")) {
			handleGetAllFavorites(req, resp);
		}
		
		else {
            sendErrorResponse(req, resp, HttpServletResponse.SC_NOT_FOUND, "找不到路徑");
        }
	}
	
	private void handleGetAllComments(HttpServletRequest req, HttpServletResponse resp)
			throws IOException{
		try {
            List<Comment> comments = adminService.getAllComments();
            sendJsonResponse(req, resp, HttpServletResponse.SC_OK, comments);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(req, resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "載入評論失敗: " + e.getMessage());
        }
	}
	
	private void handleGetAllFavorites(HttpServletRequest req, HttpServletResponse resp)
			throws IOException{
		try {
            List<Favorite> favs = adminService.getAllFavorites();
            sendJsonResponse(req, resp, HttpServletResponse.SC_OK, favs);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(req, resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "載入收藏失敗: " + e.getMessage());
        }
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
	    resp.setCharacterEncoding("UTF-8");
		setCrossHeader(req, resp);
		
		if(!isAdmin(req, resp)) return;
		String pathInfo = req.getPathInfo();
		
		if(pathInfo == null || pathInfo.equals("/")) {
			sendErrorResponse(req, resp, HttpServletResponse.SC_BAD_REQUEST, "無效的路徑");
            return;
		}
		
		// DELETE /api/admin/recipes/{recipeId}
        if (pathInfo.startsWith("/recipes/")) {
            String recipeIdStr = pathInfo.substring("/recipes/".length());
            handleDeleteRecipe(req, resp, recipeIdStr);
        }
        // DELETE /api/admin/comments/{commentId}
        else if (pathInfo.startsWith("/comments/")) {
            String commentIdStr = pathInfo.substring("/comments/".length());
            handleDeleteComment(req, resp, commentIdStr);
        }
        else {
            sendErrorResponse(req, resp, HttpServletResponse.SC_NOT_FOUND, "找不到路徑");
        }
	}

	private void handleDeleteRecipe(HttpServletRequest req, HttpServletResponse resp, String recipeIdStr) 
            throws IOException {
        try {
            Integer recipeId = Integer.parseInt(recipeIdStr);
            adminService.deleteRecipes(recipeId);
            
            JsonObject result = new JsonObject();
            result.addProperty("message", "食譜刪除成功");
            sendJsonResponse(req, resp, HttpServletResponse.SC_OK, result);
           
        } catch (NumberFormatException e) {
            sendErrorResponse(req, resp, HttpServletResponse.SC_BAD_REQUEST, "無效的食譜 ID");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(req, resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "刪除食譜失敗: " + e.getMessage());
        }
    }

    // 刪除評論
    private void handleDeleteComment(HttpServletRequest req, HttpServletResponse resp, String commentIdStr) 
            throws IOException {
        try {
            Integer commentId = Integer.parseInt(commentIdStr);
            
            // 管理員可以刪除任何評論，所以不需要檢查 userId
            adminService.deleteComments(commentId);          

            JsonObject result = new JsonObject();
            result.addProperty("message", "評論刪除成功");
            sendJsonResponse(req, resp, HttpServletResponse.SC_OK, result);

        } catch (NumberFormatException e) {
            sendErrorResponse(req, resp, HttpServletResponse.SC_BAD_REQUEST, "無效的評論 ID");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(req, resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "刪除評論失敗: " + e.getMessage());
        }
    }
	

}
