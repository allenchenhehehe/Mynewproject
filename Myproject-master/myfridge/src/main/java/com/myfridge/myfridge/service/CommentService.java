package com.myfridge.myfridge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.myfridge.myfridge.entity.Comment;
import com.myfridge.myfridge.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getAllComments(){
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByUser(Integer userId){

        if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
        
        return commentRepository.findByUserId(userId);
    }

    public List<Comment> getCommentsByRecipe(Integer recipeId){

		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}
		
		return commentRepository.findByRecipeId(recipeId);
	}

    public Comment addComment(Integer userId, Integer recipeId, Integer rating, String text) {
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}
		if(rating == null || rating < 1 || rating > 5) {
			throw new IllegalArgumentException("評分必須在 1-5 之間");
		}
		if(text == null || text.trim().isEmpty()) {
			throw new IllegalArgumentException("評論內容不可為空");
		}
		if(text.length()>1000) {
			throw new IllegalArgumentException("評論內容不可超過 1000 字");
		}

		return commentRepository.insert(userId, recipeId, rating, text);
		
	}

    public void updateComment(Integer commentId, Integer userId, Integer rating, String text) {
		if(commentId == null) {
			throw new IllegalArgumentException("評論 ID 不可為空");
		}
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		if(rating == null || rating < 1 || rating > 5) {
			throw new IllegalArgumentException("評分必須在 1-5 之間");
		}
		if(text == null || text.trim().isEmpty()) {
			throw new IllegalArgumentException("評論內容不可為空");
		}
		if(text.length()>1000) {
			throw new IllegalArgumentException("評論內容不可超過 1000 字");
		}

        int rows =  commentRepository.update(commentId, userId, rating, text);
        if (rows == 0) {
            throw new RuntimeException("找不到此評論或無權修改");
        }
	}

    public void deleteComment(Integer commentId, Integer userId) {
		if(commentId == null) {
			throw new IllegalArgumentException("評論 ID 不可為空");
		}
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		
        int rows =  commentRepository.delete(commentId, userId);
        if (rows == 0) {
            throw new RuntimeException("找不到此評論或無權刪除");
        }
	}
}
