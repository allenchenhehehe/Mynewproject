package com.myfridge.myfridge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myfridge.myfridge.entity.Comment;
import com.myfridge.myfridge.entity.Favorite;
import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.entity.User;
import com.myfridge.myfridge.repository.CommentRepository;
import com.myfridge.myfridge.repository.FavoriteRepository;
import com.myfridge.myfridge.repository.RecipeRepository;
import com.myfridge.myfridge.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public List<Comment> getAllComments(){
		return commentRepository.findAll();
	}

    public void deleteComments(Integer commentId){
        int rows = commentRepository.deleteByAdmin(commentId);
        if (rows == 0) {
            throw new RuntimeException("找不到該評論");
        }
        System.out.println("管理員刪除評論: " + commentId);
    }

    public List<Recipe> getAllRecipes(){
		return recipeRepository.findAll();
	}

    public void deleteRecipes(Integer recipeId){
        int rows = recipeRepository.delete(recipeId);
        if (rows == 0) {
            throw new RuntimeException("找不到該食譜");
        }
        System.out.println("管理員刪除食譜: " + recipeId);
    }

    public List<Favorite> getAllFavorites(){
		return favoriteRepository.findAll();
	}

    // 查詢所有使用者
public List<User> getAllUsers() {
    return userRepository.findAll();
}

// 更新使用者狀態
public void updateUserStatus(Integer userId, String status) {
    // 驗證 status 值
    if (!List.of("active", "inactive", "banned").contains(status)) {
        throw new IllegalArgumentException("無效的狀態值");
    }
    
    int rows = userRepository.updateStatus(userId, status);
    if (rows == 0) {
        throw new RuntimeException("找不到該使用者");
    }
    System.out.println("管理員更新使用者狀態: userId=" + userId + ", status=" + status);
}

    // 刪除使用者
    public void deleteUser(Integer userId) {
        int rows = userRepository.delete(userId);
        if (rows == 0) {
            throw new RuntimeException("找不到該使用者");
        }
        System.out.println("管理員刪除使用者: userId=" + userId);
    }

}
