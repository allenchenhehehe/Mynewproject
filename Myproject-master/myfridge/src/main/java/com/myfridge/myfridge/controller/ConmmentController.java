package com.myfridge.myfridge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.Comment;
import com.myfridge.myfridge.service.CommentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class ConmmentController {

    private final CommentService commentService;

    // Helper: 從 Session 取得 userId
    private Integer getUserIdFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object userIdObj = session.getAttribute("id");
        return userIdObj != null ? (Integer) userIdObj : null;
    }

    // POST 請求的 DTO
    public record CommentRequest(
        Integer recipeId,
        Integer rating,
        String text
    ) {}
    
    // PUT 請求的 DTO
    public record CommentUpdateRequest(
        Integer rating,
        String text
    ) {}
    
    // 錯誤回應
    public record ErrorResponse(String error) {}
    
    // 成功回應
    public record SuccessResponse(String message) {}

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<?> getCommentsFromRecipe(@PathVariable Integer recipeId){

        try {

            List<Comment> coms = commentService.getCommentsByRecipe(recipeId);
            //回傳json
            return ResponseEntity.ok(coms);

        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("查詢評論失敗: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCommentsFromUser(HttpSession session){

        Integer userId = getUserIdFromSession(session);

        if(userId == null){
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入")); 
        }

        try {

            List<Comment> coms = commentService.getCommentsByUser(userId);
            //回傳json
            return ResponseEntity.ok(coms);

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("查詢評論失敗: " + e.getMessage()));
        }
    }

    // POST /api/favorites/{recipeId} - 新增收藏
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentRequest request, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {
            Comment comment = commentService.addComment(userId, request.recipeId(), request.rating(), request.text());
            return ResponseEntity.status(201).body(comment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("新增評論失敗: " + e.getMessage()));
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Integer commentId, @RequestBody CommentUpdateRequest request, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {

            commentService.updateComment(
            commentId,
            userId,
            request.rating(),
            request.text()

        );
        
        return ResponseEntity.ok(new SuccessResponse("評論更新成功"));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("更新評論失敗: " + e.getMessage()));
        }

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {
            
            commentService.deleteComment(commentId, userId);
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

}
