package com.myfridge.myfridge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myfridge.myfridge.entity.Comment;
import com.myfridge.myfridge.entity.Favorite;
import com.myfridge.myfridge.entity.Recipe;
import com.myfridge.myfridge.repository.CommentRepository;
import com.myfridge.myfridge.repository.FavoriteRepository;
import com.myfridge.myfridge.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoriteRepository;

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

}
