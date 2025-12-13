package service;

import java.util.List;

import dao.CommentDAO;
import dao.FavoriteDAO;
import dao.RecipeDAO;
import model.Comment;
import model.Favorite;
import model.Recipe;

public class AdminService {
	private CommentDAO commentDAO;
	private RecipeDAO recipeDAO;
	private FavoriteDAO favoriteDAO;
	
	public AdminService() {
		this.commentDAO = new CommentDAO();
        this.favoriteDAO = new FavoriteDAO();
        this.recipeDAO = new RecipeDAO();
	}
	
	//評論管理
	public List<Comment> getAllComments(){
		try {
			return commentDAO.findAll();
		}catch (Exception e) {
            System.err.println("查詢所有評論失敗: " + e.getMessage());
            throw new RuntimeException("查詢所有評論失敗", e);
        }
	}
	
	public void deleteComments(Integer commentId) {
		try {
			int rows = commentDAO.deleteByAdmin(commentId);
			if(rows == 0) {
				 throw new RuntimeException("找不到該評論");
			}
			
			System.out.println("管理員刪除評論: " + commentId);
		}catch (Exception e) {
            System.err.println("刪除評論失敗: " + e.getMessage());
            throw new RuntimeException("刪除評論失敗", e);
        }
	}
	
	//收藏管理
	public List<Favorite> getAllFavorites(){
		try {
			return favoriteDAO.findAll();
		}catch (Exception e) {
            System.err.println("查詢所有收藏失敗: " + e.getMessage());
            throw new RuntimeException("查詢所有收藏失敗", e);
        }
	}
	
	//食譜管理
	public List<Recipe> getAllRecipes(){
		try {
			return recipeDAO.findAll();
		}catch (Exception e) {
            System.err.println("查詢所有食譜失敗: " + e.getMessage());
            throw new RuntimeException("查詢所有食譜失敗", e);
        }
	}
	
	//刪除預設食譜
	public void deleteRecipes(Integer recipeId) {
		try {
			int rows = recipeDAO.delete(recipeId);
			if(rows == 0) {
				 throw new RuntimeException("找不到該評論");
			}
			
			System.out.println("管理員刪除評論: " + recipeId);
		}catch (Exception e) {
            System.err.println("刪除評論失敗: " + e.getMessage());
            throw new RuntimeException("刪除評論失敗", e);
        }
	}
}
