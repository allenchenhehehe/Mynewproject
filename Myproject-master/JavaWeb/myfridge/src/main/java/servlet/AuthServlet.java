package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import model.User;
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
    
    /**
     * 處理 CORS 預檢請求
     */
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    /**
     * 設定 CORS 標頭
     */
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
    
    /**
     * 讀取請求 Body
     */
    private String getRequestBody(HttpServletRequest req) throws IOException {
        BufferedReader br = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    
    /**
     * 發送成功回應
     */
    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        PrintWriter out = resp.getWriter();
        out.println(gson.toJson(data));
        out.flush();
    }
    
    /**
     * 發送錯誤回應
     */
    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        
        PrintWriter out = resp.getWriter();
        out.println(gson.toJson(error));
        out.flush();
    }
    
    /**
     * 登入請求 DTO
     */
    private static class LoginRequest {
        String email;
        String password;
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        setCorsHeaders(resp);
        
        String pathInfo = req.getPathInfo();
        
        if ("/check".equals(pathInfo)) {
            handleCheckAuth(req, resp);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "找不到該 API");
        }
    }

    /**
     * 檢查登入狀態
     */
    private void handleCheckAuth(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("id") == null) {
            // 未登入
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "未登入");
            return;
        }
        
        // 已登入,回傳使用者資訊
        Integer userId = (Integer) session.getAttribute("id");
        String email = (String) session.getAttribute("email");
        String userName = (String) session.getAttribute("userName");
        
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("email", email);
        user.put("userName", userName);
        
        sendSuccess(resp, user);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 設定 CORS
        setCorsHeaders(resp);
        
        // 設定編碼
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        try {
            if ("/login".equals(pathInfo)) {
                handleLogin(req, resp);
                
            } else if ("/register".equals(pathInfo)) {
                handleRegister(req, resp);
                
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(req, resp);
                
            } else {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "找不到該 API");
            }
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    
    /**
     * 處理登入
     */
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        // 解析請求
        LoginRequest loginReq = gson.fromJson(getRequestBody(req), LoginRequest.class);
        
        System.out.println(" 登入請求: " + loginReq.email);
        
        // 驗證帳密
        User user = userService.login(loginReq.email, loginReq.password);
        
        // 建立 Session
        HttpSession session = req.getSession(true);
        session.setAttribute("id", user.getId());  // ← 統一用 userId
        session.setAttribute("email", user.getEmail());
        session.setAttribute("userName", user.getUserName());
        
        System.out.println("登入成功,Session ID: " + session.getId());
        System.out.println("   User ID: " + user.getId());
        
        // 移除密碼後回傳
        user.setPassword(null);
        
        sendSuccess(resp, user);
    }
    
    /**
     * 處理註冊
     */
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        // 解析請求
        User newUser = gson.fromJson(getRequestBody(req), User.class);
        
        System.out.println("註冊請求: " + newUser.getEmail());
        
        // 註冊使用者
        User savedUser = userService.register(newUser);     
        System.out.println("註冊成功,User ID: " + savedUser.getId());
        
        // 移除密碼後回傳
        savedUser.setPassword(null);
        
        resp.setStatus(HttpServletResponse.SC_CREATED);
        sendSuccess(resp, savedUser);
    }
    
    /**
     * 處理登出
     */
    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        HttpSession session = req.getSession(false);
        
        if (session != null) {
            System.out.println("登出 Session: " + session.getId());
            session.invalidate();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");
        
        sendSuccess(resp, response);
    }
}