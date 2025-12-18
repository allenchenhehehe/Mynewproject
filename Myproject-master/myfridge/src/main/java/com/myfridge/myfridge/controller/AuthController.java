package com.myfridge.myfridge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.User;
import com.myfridge.myfridge.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 登入請求 DTO
    public record LoginRequest(
        String email,
        String password
    ) {}
    
    // 註冊請求 DTO
    public record RegisterRequest(
        String userName,
        String email,
        String password
    ) {}
    
    // 使用者資訊回應 DTO
    public record UserResponse(
        Integer id,
        String userName,
        String email,
        String role
    ) {}
    
    // 錯誤回應
    public record ErrorResponse(
        boolean success,
        String error
    ) {}
    
    // 成功訊息回應
    public record SuccessResponse(
        boolean success,
        String message
    ) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session){

        try {

            User user = userService.login(request.email(), request.password());

            //建立 Session
            session.setMaxInactiveInterval(600);
            session.setAttribute("id", user.getId());  // ← 統一用 userId
            session.setAttribute("email", user.getEmail());
            session.setAttribute("userName", user.getUserName());
            session.setAttribute("userRole", user.getRole());

            //回傳使用者資訊（不包含密碼）
            UserResponse response = new UserResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){

        try {

            User newUser = new User();
            newUser.setUserName(request.userName);
            newUser.setEmail(request.email);
            newUser.setPassword(request.password);

            User savedUser = userService.register(newUser);

            //回傳使用者資訊（不包含密碼）
            UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getRole()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session){
        if(session != null){
            System.out.println("登出 Session: " + session.getId());
            session.invalidate();  // 銷毀 Session
        }

        return ResponseEntity.ok(new SuccessResponse(true, "登出成功"));
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkA(HttpSession session){

        if(session == null|| session.getAttribute("id") == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(false, "未登入"));
        }

        UserResponse response = new UserResponse(
            (Integer) session.getAttribute("id"),
            (String) session.getAttribute("userName"),
            (String) session.getAttribute("email"),
            (String) session.getAttribute("userRole")
        );

        return ResponseEntity.ok(response);
    }
}
