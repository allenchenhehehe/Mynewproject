package com.myfridge.myfridge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.Favorite;
import com.myfridge.myfridge.service.FavoriteService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // Helper: 從 Session 取得 userId
    private Integer getUserIdFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object userIdObj = session.getAttribute("id");
        return userIdObj != null ? (Integer) userIdObj : null;
    }

    public record CheckFavoriteResponse(boolean isFavorite) {}
    
    // 切換收藏的回應
    public record ToggleFavoriteResponse(
        boolean isFavorite,
        String message
    ) {}
    
    // 錯誤回應
    public record ErrorResponse(String error) {}
    
    // 成功回應
    public record SuccessResponse(String message) {}

    @GetMapping
    public ResponseEntity<?> getAllFavorite(HttpSession session){

        Integer userId = getUserIdFromSession(session);

        if(userId == null){
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入")); 
        }

        try {

            List<Favorite> favs = favoriteService.getFavoriteByUserId(userId);
            //回傳json
            return ResponseEntity.ok(favs);

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("查詢收藏失敗: " + e.getMessage()));
        }
    }

    // GET /api/favorites/check/{recipeId} - 檢查是否已收藏
    @GetMapping("/check/{recipeId}")
    public ResponseEntity<?> checkFavorite(@PathVariable Integer recipeId, HttpSession session){

        Integer userId = getUserIdFromSession(session);

        if(userId == null){
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入")); 
        }

        try {

            boolean isFavorite = favoriteService.isFavorited(userId, recipeId);
            //回傳json
            return ResponseEntity.ok(new CheckFavoriteResponse(isFavorite));

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("檢查收藏失敗: " + e.getMessage()));
        }
    }

    // POST /api/favorites/{recipeId} - 新增收藏
    @PostMapping("/{recipeId}")
    public ResponseEntity<?> addFavorite(@PathVariable Integer recipeId, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {
            Favorite favorite = favoriteService.addFavorite(userId, recipeId);
            return ResponseEntity.status(201).body(favorite);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("已經收藏")) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.status(500)
                .body(new ErrorResponse("新增收藏失敗: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("新增收藏失敗: " + e.getMessage()));
        }
    }

     // POST /api/favorites/toggle/{recipeId} - 切換收藏狀態
    @PostMapping("/toggle/{recipeId}")
    public ResponseEntity<?> toggleFavorite(@PathVariable Integer recipeId, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {
            
            Boolean isFavorite = favoriteService.toggleFavorite(userId, recipeId);
            ToggleFavoriteResponse response = new ToggleFavoriteResponse(
                isFavorite,
                isFavorite ? "收藏成功" : "取消收藏成功"
            );           
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("切換收藏狀態失敗: " + e.getMessage()));
        }
    }

     // DELETE /api/favorites/{recipeId} - 刪除收藏
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable Integer recipeId, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {
            
            favoriteService.deleteFavorite(userId, recipeId);
            return ResponseEntity.ok(new SuccessResponse("取消收藏成功"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("取消收藏失敗: " + e.getMessage()));
        }
    }

}
