package com.myfridge.myfridge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.Comment;
import com.myfridge.myfridge.entity.Favorite;
import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.service.AdminService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 錯誤回應
    record ErrorResponse(String error) {}
    
    // 成功回應
    record SuccessResponse(String message) {}

    // 從 Session 取得 userId
    private Integer getUserIdFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object userIdObj = session.getAttribute("id");
        return userIdObj != null ? (Integer) userIdObj : null;
    }

    // 從 Session 取得 userRole
    private String getUserRoleFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object roleObj = session.getAttribute("userRole");
        return roleObj != null ? (String) roleObj : null;
    }

    // 檢查是否為管理員
    private boolean isAdmin(HttpSession session) {
        String role = getUserRoleFromSession(session);
        return "admin".equals(role);
    }

    // 統一的權限檢查方法
    private ResponseEntity<?> checkAdminPermission(HttpSession session){

        Integer userId = getUserIdFromSession(session);

        if(userId == null){
            return ResponseEntity.status(401)
                .body(new ErrorResponse("未登入"));
        }

        if (!isAdmin(session)) {
            return ResponseEntity.status(403)
                .body(new ErrorResponse("權限不足"));
        }
        
        return null;  // 通過檢查
    }

    @GetMapping("/comments")
    public ResponseEntity<?> gellAllComments(HttpSession session){

        ResponseEntity<?> permissionCheck = checkAdminPermission(session);
        if (permissionCheck != null) {
            return permissionCheck;  // 權限不足，直接回傳
        }

        try {

            List<Comment> comments = adminService.getAllComments();
            return ResponseEntity.ok(comments);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("載入評論失敗: " + e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComments(@PathVariable Integer commentId, HttpSession session){

        ResponseEntity<?> permissionCheck = checkAdminPermission(session);
        if (permissionCheck != null) {
            return permissionCheck;  // 權限不足，直接回傳
        }

        try {

           adminService.deleteComments(commentId);
           return ResponseEntity.ok(new SuccessResponse("評論刪除成功"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("刪除評論失敗: " + e.getMessage()));
        }

    }

    @GetMapping("/favorites")
     public ResponseEntity<?> gellAllFavorites(HttpSession session){

        ResponseEntity<?> permissionCheck = checkAdminPermission(session);
        if (permissionCheck != null) {
            return permissionCheck;  // 權限不足，直接回傳
        }

        try {

            List<Favorite> favorites = adminService.getAllFavorites();
            return ResponseEntity.ok(favorites);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("載入收藏失敗: " + e.getMessage()));
        }

     }

     @GetMapping("/recipes")
     public ResponseEntity<?> gellAllRecipes(HttpSession session){

        ResponseEntity<?> permissionCheck = checkAdminPermission(session);
        if (permissionCheck != null) {
            return permissionCheck;  // 權限不足，直接回傳
        }

        try {

            List<Recipe> recipes = adminService.getAllRecipes();
            return ResponseEntity.ok(recipes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("載入食譜失敗: " + e.getMessage()));
        }

     }

     @DeleteMapping("/recipes/{recipeId}")
    public ResponseEntity<?> deleteRecipes(@PathVariable Integer recipeId, HttpSession session){

        ResponseEntity<?> permissionCheck = checkAdminPermission(session);
        if (permissionCheck != null) {
            return permissionCheck;  // 權限不足，直接回傳
        }

        try {

           adminService.deleteRecipes(recipeId);
           return ResponseEntity.ok(new SuccessResponse("食譜刪除成功"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("刪除食譜失敗: " + e.getMessage()));
        }

    }
}
