package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import model.User;
import service.IngredientService;
import service.UserService;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
	
	private UserService userService;
	private Gson gson;
	
	@Override
	public void init() {
		userService = new UserService();
		gson = new Gson();
	}
	
	private String getRequestBody(HttpServletRequest req) throws IOException {
		BufferedReader br = req.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while((line=br.readLine())!=null) {
			sb.append(line);
		}
		return sb.toString();
	}
	private static class LoginRequest {
		String email;
		String password;
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");
		resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");  // Vue 開發伺服器
	    resp.setHeader("Access-Control-Allow-Credentials", "true");  // ← 允許帶 Cookie
	    resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
	    resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
		PrintWriter out = resp.getWriter();
		String pathInfo = req.getPathInfo();
		try {
			if("/login".equals(pathInfo)) {
				LoginRequest loginReq = gson.fromJson(getRequestBody(req), LoginRequest.class);
				User user = userService.login(loginReq.email, loginReq.password);
				HttpSession session = req.getSession(true);
				session.setAttribute("userId",user.getId());
				session.setAttribute("userName",user.getUserName());
				session.setAttribute("userEmail",user.getEmail());			
				user.setPassword(null);
				out.println(gson.toJson(user));
			}else if("/register".equals(pathInfo)) {
				User newUser = gson.fromJson(getRequestBody(req), User.class);
				User savedUser = userService.register(newUser);
				HttpSession session = req.getSession(true);
				session.setAttribute("userId",savedUser.getId());
				session.setAttribute("userName",savedUser.getUserName());
				session.setAttribute("userEmail",savedUser.getEmail());
				savedUser.setPassword(null);
				resp.setStatus(HttpServletResponse.SC_CREATED);
				out.println(gson.toJson(savedUser));
			}else if("/logout".equals(pathInfo)) {
				HttpSession session = req.getSession(false);
				if(session!=null) {
					session.invalidate();
				}
				resp.setStatus(HttpServletResponse.SC_OK);
				out.print("{\"message\": \"登出成功!\"}");
			}else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				out.print("{\"error\": \"尚未登入!\"}");
			}
		}catch(RuntimeException e) {
			 resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	         out.print("{\"error\": \"" + e.getMessage() + "\"}");
		}
		out.flush();	
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
	    resp.setHeader("Access-Control-Allow-Credentials", "true");
	    resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
	    resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    resp.setStatus(HttpServletResponse.SC_OK);
	}
	
}
