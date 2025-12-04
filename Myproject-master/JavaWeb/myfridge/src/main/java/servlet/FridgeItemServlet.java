package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}
	
	
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	
	
}
