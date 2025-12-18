package com.myfridge.myfridge.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfridge.myfridge.entity.ShoppingListItem;
import com.myfridge.myfridge.service.ShoppingListItemService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shopping-list")
@RequiredArgsConstructor
public class ShoppingListItemController {

    private final ShoppingListItemService shoppingListItemService;

    //Helper，從 Session 取得 userId
    private Integer getUserIdFromSession(HttpSession session){

    	if(session == null) {
    		return null;
    	}

        //session.gatAttribute是拿到物件，要強制轉型成Integer
    	Object userIdObj = session.getAttribute("id");
        return userIdObj != null ? (Integer) userIdObj : null;
    }

    // POST 請求的 DTO
    public record ShoppingListItemRequest(
        Integer recipeId,
        String recipeName,
        Integer ingredientId,
        String ingredientName,
        Double amount,
        String unit,
        String category
    ){}

    // PUT 請求的 DTO
    public record ShoppingListUpdateItemRequest(
        String ingredientName,
        Double amount,
        String unit,
        String category,
        Boolean isPurchased
    ){}

    // PATCH 請求的 DTO
    public record TogglePurchaseRequest(
        Boolean isPurchased
    ){}

    //清除購物車的DTO
    public record CleanPurchaseResponse(   
        boolean success,
        int count,
        String message
    ){}

    // 錯誤回應
    public record ErrorResponse(String error) {}
    
    // 成功回應
    public record SuccessResponse(String message) {}


    @GetMapping
    public ResponseEntity<?> getAllList(HttpSession session){

        Integer userId = getUserIdFromSession(session);

        if(userId == null){
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入")); 
        }

        try {
            List<ShoppingListItem> items = shoppingListItemService.getShoppingList(userId);
            //回傳json
            return ResponseEntity.ok(items);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("查詢購物清單失敗: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody ShoppingListItemRequest request, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {

            ShoppingListItem item = new ShoppingListItem();
            item.setRecipeId(request.recipeId());
            item.setRecipeName(request.recipeName());
            item.setIngredientId(request.ingredientId());
            item.setIngredientName(request.ingredientName());
            item.setAmount(request.amount());
            item.setUnit(request.unit());
            item.setCategory(request.category());

            ShoppingListItem added = shoppingListItemService.addItem(userId,item);
            return ResponseEntity.status(201).body(added);                    

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("新增購物項目失敗: " + e.getMessage()));
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Integer id ,@RequestBody ShoppingListUpdateItemRequest request, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try{    
            ShoppingListItem item = new ShoppingListItem();
            
            item.setIngredientName(request.ingredientName());
            item.setAmount(request.amount());
            item.setUnit(request.unit());
            item.setCategory(request.category());
            item.setIsPurchased(request.isPurchased());

            ShoppingListItem updated = shoppingListItemService.updateItem(userId, id, item);
            return ResponseEntity.status(201).body(updated);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("更新購物項目失敗: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> togglePuchased(
        @PathVariable Integer id, @RequestBody TogglePurchaseRequest request,HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try{    

            ShoppingListItem toggle = shoppingListItemService.togglePurchase(userId, id, request.isPurchased());
            return ResponseEntity.status(201).body(toggle);

        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("更新購買狀態失敗: " + e.getMessage()));
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Integer id, HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {

            shoppingListItemService.deleteItem(userId, id);
            return ResponseEntity.ok(new SuccessResponse("刪除成功"));
            
        }catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("刪除購物項目失敗: " + e.getMessage()));
        }
    }

    @PostMapping("/clear-purchased")
    public ResponseEntity<?> cleanPurchased(HttpSession session){

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("尚未登入"));
        }

        try {

            int count = shoppingListItemService.clearPurchasedAndAddToFridge(userId);
            CleanPurchaseResponse response = new CleanPurchaseResponse(
                true,
                count,
                "成功加入 " + count + " 個項目到冰箱"
            );

            return ResponseEntity.ok(response);
    
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(new ErrorResponse("清除已購買項目失敗: " + e.getMessage()));
        }
    }
}
