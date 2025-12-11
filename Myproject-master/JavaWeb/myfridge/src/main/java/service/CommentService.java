package service;

import java.util.List;

import dao.CommentDAO;
import model.Comment;

public class CommentService {
	private CommentDAO commentDAO;
	public CommentService() {
		this.commentDAO = new CommentDAO();
	}
	
	public List<Comment> getCommentsByUser(Integer userId){
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		try {
			return commentDAO.findByUserId(userId);
		}catch(Exception e) {
			System.err.println("查詢使用者評論失敗: " + e.getMessage());
            throw new RuntimeException("查詢使用者評論失敗", e);
		}
	}
	
	public List<Comment> getCommentsByRecipe(Integer recipeId){
		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}
		try {
			return commentDAO.findByRecipeId(recipeId);
		}catch(Exception e) {
			System.err.println("查詢食譜評論失敗: " + e.getMessage());
            throw new RuntimeException("查詢食譜評論失敗", e);
		}
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
		try {
			return commentDAO.insert(userId, recipeId, rating, text);
		}catch(Exception e) {
			System.err.println("新增評論失敗: " + e.getMessage());
            throw new RuntimeException("新增評論失敗", e);
		}
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
		try {
			int rows =  commentDAO.update(commentId, userId, rating, text);
			if (rows == 0) {
                throw new RuntimeException("找不到此評論或無權修改");
            }
		}catch(Exception e) {
			System.err.println("修改評論失敗: " + e.getMessage());
            throw new RuntimeException("修改評論失敗", e);
		}
	}
	
	public void deleteComment(Integer commentId, Integer userId) {
		if(commentId == null) {
			throw new IllegalArgumentException("評論 ID 不可為空");
		}
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		
		try {
			int rows =  commentDAO.delete(commentId, userId);
			if (rows == 0) {
                throw new RuntimeException("找不到此評論或無權刪除");
            }
		}catch(Exception e) {
			System.err.println("刪除評論失敗: " + e.getMessage());
            throw new RuntimeException("刪除評論失敗", e);
		}
	}
}
