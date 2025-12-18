package com.myfridge.myfridge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.Comment;
import com.myfridge.myfridge.entity.Favorite;
import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.entity.User;
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

    record UpdateStatusRequest(String status) {}

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

    // GET /api/admin/users - 查詢所有使用者
@GetMapping("/users")
public ResponseEntity<?> getAllUsers(HttpSession session) {
    ResponseEntity<?> permissionCheck = checkAdminPermission(session);
    if (permissionCheck != null) return permissionCheck;
    
    try {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
            .body(new ErrorResponse("載入使用者失敗: " + e.getMessage()));
    }
}

// PATCH /api/admin/users/{userId}/status - 更新使用者狀態
@PatchMapping("/users/{userId}/status")
public ResponseEntity<?> updateUserStatus(
    @PathVariable Integer userId,
    @RequestBody UpdateStatusRequest request,
    HttpSession session
) {
    ResponseEntity<?> permissionCheck = checkAdminPermission(session);
    if (permissionCheck != null) return permissionCheck;
    
    try {
        adminService.updateUserStatus(userId, request.status());
        return ResponseEntity.ok(new SuccessResponse("使用者狀態更新成功"));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(e.getMessage()));
    } catch (RuntimeException e) {
        return ResponseEntity.status(404)
            .body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
            .body(new ErrorResponse("更新失敗: " + e.getMessage()));
    }
}

// DELETE /api/admin/users/{userId} - 刪除使用者
@DeleteMapping("/users/{userId}")
public ResponseEntity<?> deleteUser(
    @PathVariable Integer userId,
    HttpSession session
) {
    ResponseEntity<?> permissionCheck = checkAdminPermission(session);
    if (permissionCheck != null) return permissionCheck;
    
    try {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(new SuccessResponse("使用者刪除成功"));
    } catch (RuntimeException e) {
        return ResponseEntity.status(404)
            .body(new ErrorResponse(e.getMessage()));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
            .body(new ErrorResponse("刪除失敗: " + e.getMessage()));
    }
}

}
