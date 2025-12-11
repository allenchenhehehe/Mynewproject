package service;

import java.util.List;

import dao.FavoriteDAO;
import model.Favorite;

public class FavoriteService {
	private FavoriteDAO favoriteDAO;
	private Favorite favorite;
	
	public FavoriteService() {
		this.favoriteDAO = new FavoriteDAO();
	}
	
	public List<Favorite> getFavorites(Integer userId){
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		try {
			return favoriteDAO.findByUserId(userId);
		}catch(Exception e) {
			System.err.println("查詢收藏失敗: " + e.getMessage());
            throw new RuntimeException("查詢收藏失敗", e);
		}
	}
	
	public Boolean isFavorited(Integer userId, Integer recipeId) {
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}
		try {
			return favoriteDAO.check(userId, recipeId);
		}catch(Exception e) {
			System.err.println("檢查收藏失敗: " + e.getMessage());
            throw new RuntimeException("檢查收藏失敗", e);
		}
	}
	
	public Favorite addFavorite(Integer userId, Integer recipeId) {
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}
		try {
			if(favoriteDAO.check(userId, recipeId)) {
				 throw new RuntimeException("已經收藏過此食譜");
			}
			return favoriteDAO.insert(userId, recipeId);
		}catch(Exception e) {
			System.err.println("新增收藏失敗: " + e.getMessage());
            throw new RuntimeException("新增收藏失敗", e);
		}
	}
	
	public void deleteFavorite(Integer userId, Integer recipeId) {
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}
		try {
			int rows = favoriteDAO.deleteItem(userId, recipeId);
			if(rows == 0) {
				 throw new RuntimeException("找不到此收藏或無權刪除");
			}
		}catch(Exception e) {
			System.err.println("取消收藏失敗: " + e.getMessage());
            throw new RuntimeException("取消收藏失敗", e);
		}
	}
	
	public boolean toggleFavorite(Integer userId, Integer recipeId) {
		if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}
		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}
		try {
			if(favoriteDAO.check(userId, recipeId)) {
				deleteFavorite(userId, recipeId);
				return false;
			}else {
				addFavorite(userId, recipeId);
				return true;
			}
		}catch(Exception e) {
			System.err.println("取消收藏失敗: " + e.getMessage());
            throw new RuntimeException("取消收藏失敗", e);
		}
	}
}
