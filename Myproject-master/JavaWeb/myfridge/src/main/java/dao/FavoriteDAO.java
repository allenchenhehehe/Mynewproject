package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.Favorite;
import model.Recipe;
import model.ShoppingListItem;
import util.DBUtil;

public class FavoriteDAO {
	private static final String SELECT_ALL = 
			"SELECT f.id, f.userId, f.recipeId, f.savedAt, " + 
			"u.userName, r.title AS recipeTitle " +
			"FROM Favorite f " +
			"JOIN User u ON f.userId = u.id " +
			"JOIN Recipe r ON f.recipeId = r.id " + 
			"ORDER BY f.savedAt DESC";
	private static final String SELECT_BY_USER_ID = 
			"Select f.id, f.userId, f.recipeId, f.savedAt, " + 
			"r.title, r.description, r.imageUrl, r.cookingTime, r.difficulty " +
			"From Favorite f " +
			"Join Recipe r On f.recipeId = r.id " + 
			"Where f.userId = ? " +
			"Order By f.savedAt desc";
	
	private static final String CHECK_EXISTS = 
			"Select COUNT(*) FROM Favorite WHERE userId = ? AND recipeId = ?";
	
	private static final String INSERT = 
			"INSERT INTO Favorite (userId, recipeId) VALUES (?, ?)";
	
	private static final String DELETE = 
			"DELETE FROM Favorite WHERE userId = ? AND recipeId = ?";
	
	public FavoriteDAO() {}
	
	private Favorite buildFavorite(ResultSet rs) throws SQLException {
		Favorite favorite = new Favorite();
		favorite.setId(rs.getInt("id"));	
		favorite.setUserId(rs.getInt("userId"));	 	  
		favorite.setRecipeId(rs.getInt("recipeId"));	 
		favorite.setSavedAt(rs.getTimestamp("savedAt"));
		favorite.setTitle(rs.getString("title"));
		favorite.setImageUrl(rs.getString("imageUrl"));
		favorite.setDescription(rs.getString("description"));
		favorite.setCookingTime(rs.getInt("cookingTime"));
		favorite.setDifficulty(rs.getInt("difficulty"));
		return favorite;
	 }
	//管理員專用的 buildFavorite（包含 userName 和 recipeTitle）
	private Favorite buildFavoriteForAdmin(ResultSet rs) throws SQLException {
		Favorite favorite = new Favorite();
		favorite.setId(rs.getInt("id"));	
		favorite.setUserId(rs.getInt("userId"));	 	  
		favorite.setRecipeId(rs.getInt("recipeId"));	 
		favorite.setSavedAt(rs.getTimestamp("savedAt"));
		favorite.setUserName(rs.getString("userName"));
		favorite.setRecipeTitle(rs.getString("recipeTitle"));
		return favorite;
	}
	
	//查詢所有收藏（管理員用)
	public List<Favorite> findAll() {
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    List<Favorite> list = new LinkedList<>();
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(SELECT_ALL);
	        rs = psmt.executeQuery();
	        while(rs.next()) {
	        	Favorite favorite = buildFavoriteForAdmin(rs);
				list.add(favorite);
			}
	    } catch(SQLException se) {
	    	se.printStackTrace();
		    throw new RuntimeException("查詢所有收藏失敗", se);
	    } finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	    return list;
	}
	
	//使用者的所有收藏
	public List<Favorite> findByUserId(Integer userId){
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    List<Favorite> list = new LinkedList<>();
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(SELECT_BY_USER_ID);
	        psmt.setInt(1, userId);
	        rs = psmt.executeQuery();
	        while(rs.next()) {
	        	Favorite favorite = buildFavorite(rs);
				list.add(favorite);
			}
	    }catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("用戶查詢收藏食譜失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	    return list;
	}
	
	//檢查是否已收藏
	public Boolean check(Integer userId, Integer recipeId) {
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(CHECK_EXISTS);
	        psmt.setInt(1, userId);
	        psmt.setInt(2, recipeId);
	        rs = psmt.executeQuery();
	        while(rs.next()) {
	        	//第一個欄位是count(*)，如果大於0代表已收藏了
	        	return rs.getInt(1)>0;
			}
	        return false;
	    }catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("用戶查詢收藏食譜失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	}
	
	public Favorite insert(Integer userId, Integer recipeId) {
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
	        psmt.setInt(1, userId);
	        psmt.setInt(2, recipeId);
	        //新增是return的是一個整數，代表受影響的行數
	        int rows = psmt.executeUpdate();
	        //行數>0代表新增成功
	        if(rows>0) {
	        	//getGeneratedKeys()會回傳一個ResultSet，裡面包含DB生成的id
	        	rs = psmt.getGeneratedKeys();
	        	if(rs.next()) {
	        		int newId = rs.getInt(1);
	        		Favorite favorite = new Favorite();
	        		favorite.setId(newId);
	        		favorite.setUserId(userId);
	        		favorite.setRecipeId(recipeId);
	        		return favorite;
	        	}
	        }
	        throw new RuntimeException("新增收藏失敗：未取得生成的 ID");
	    }catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("新增收藏食譜失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	}
	
	public Integer deleteItem(Integer userId, Integer recipeId) {
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(DELETE);
	        psmt.setInt(1, userId);
	        psmt.setInt(2, recipeId);        
	        int rows = psmt.executeUpdate();
	        return rows;
	        
	    }catch(SQLException se) {
	    	 se.printStackTrace(); // 印出完整錯誤
	    	 try {
	             if (conn != null) conn.rollback();
	         } catch (SQLException rollbackEx) {
	             rollbackEx.printStackTrace();
	         }
		     throw new RuntimeException("用戶刪除食材失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	}
}
