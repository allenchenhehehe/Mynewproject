package com.myfridge.myfridge.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.FridgeItem;
import com.myfridge.myfridge.service.FridgeItemService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fridge-items")
@RequiredArgsConstructor
public class FridgeItemController {
    
    private FridgeItemService fridgeItemService;

    //Helper，從 Session 取得 userId
    private Integer getUserIdFromSession(HttpSession session){

    	if(session == null) {
    		return null;
    	}

        //session.gatAttribute是拿到物件，要強制轉型成Integer
    	Object userIdObj = session.getAttribute("id");
        return userIdObj != null ? (Integer) userIdObj : null;
    }

    @GetMapping
    public ResponseEntity<?> getAllItems(HttpSession session){

        Integer userId = getUserIdFromSession(session);

        if(userId == null){
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入")); 
        }

        try {
            List<FridgeItem> list = fridgeItemService.getAllItems(userId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ErrorResponse("伺服器錯誤: " + e.getMessage()));
        }
        
    }

    //把前端傳來的 JSON 自動轉成 FridgeItemRequest 物件
    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody FridgeItemRequest request, HttpSession session) {
        // 1. 檢查登入
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        // 2. 新增食材
        try {
            FridgeItem item = fridgeItemService.addToFridge(
                userId,
                request.ingredientName(),
                request.category(),
                request.amount(),
                request.unit(),
                request.purchasedDate(),
                request.expiredDate()
            );

            return ResponseEntity.ok(item);

        } catch (Exception e) {
            return ResponseEntity.status(500)
            .body(new ErrorResponse("新增失敗: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Integer id, @RequestBody FridgeItemUpdateRequest request, HttpSession session){
        // 1. 檢查登入
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {
            FridgeItem item = fridgeItemService.updateItem(
                userId,
                id,
                request.amount(),
                request.unit(),
                request.purchasedDate(),
                request.expiredDate()
            );

            return ResponseEntity.ok(item);

        } catch (RuntimeException e) {

            if (e.getMessage().contains("找不到")) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
            } else if (e.getMessage().contains("無權")) {
                return ResponseEntity.status(403)
                    .body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.status(500)
                .body(new ErrorResponse("更新失敗: " + e.getMessage()));
        }

    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteItem(@PathVariable Integer id,  HttpSession session){
        // 1. 檢查登入
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {
            boolean success = fridgeItemService.deleteItem(userId, id);
            if(success){
                return ResponseEntity.ok(new SuccessResponse("刪除成功"));
            }else{
                return ResponseEntity.status(404)
                .body(new ErrorResponse("找不到此食材"));
            }
        } catch (RuntimeException e) {

            if (e.getMessage().contains("找不到")) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
            } else if (e.getMessage().contains("無權")) {
                return ResponseEntity.status(403)
                    .body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.status(500)
                .body(new ErrorResponse("刪除失敗: " + e.getMessage()));
        }

    }


    record FridgeItemRequest(
        String ingredientName,
        String category,
        Double amount,
        String unit,
        LocalDate purchasedDate,
        LocalDate expiredDate
    ) {}


    record FridgeItemUpdateRequest(
        Double amount,
        String unit,
        LocalDate purchasedDate,
        LocalDate expiredDate
    ) {}

    // 錯誤回應
    record ErrorResponse(String error) {}
    // 成功回應
    record SuccessResponse(String message) {}
}
