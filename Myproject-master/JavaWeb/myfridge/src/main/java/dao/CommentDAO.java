package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import model.Comment;
import model.Favorite;
import model.FridgeItem;
import model.Recipe;
import model.RecipeIngredient;
import util.DBUtil;

public class CommentDAO {
	private static final String SELECT_ALL = 
			"SELECT c.id, c.userId, c.recipeId, c.rating, c.text, c.createdAt, c.updatedAt, " +
			"u.userName, r.title AS recipeTitle " +
			"FROM Comment c " +
			"JOIN User u ON c.userId = u.id " +
			"JOIN Recipe r ON c.recipeId = r.id " +
			"ORDER BY c.createdAt DESC";
	private static final String SELECT_BY_RECIPE_ID = 
			"Select c.id, c.userId, c.recipeId, c.rating, c.text, c.createdAt, c.updatedAt, " +
			"u.userName, r.title AS recipeTitle " +
	        "FROM Comment c " +
	        "JOIN User u ON c.userId = u.id " +
	        "JOIN Recipe r ON c.recipeId = r.id " +
	        "WHERE c.recipeId = ? " +
	        "ORDER BY c.createdAt DESC";
	private static final String SELECT_BY_USER_ID = 
			"Select c.id, c.userId, c.recipeId, c.rating, c.text, c.createdAt, c.updatedAt, " +
			"u.userName, r.title AS recipeTitle " +
	        "FROM Comment c " +
	        "JOIN User u ON c.userId = u.id " +
	        "JOIN Recipe r ON c.recipeId = r.id " +
	        "WHERE c.userId = ? " +
	        "ORDER BY c.createdAt DESC";
	private static final String INSERT = 
	        "INSERT INTO Comment (userId, recipeId, rating, text) VALUES (?, ?, ?, ?)";
	private static final String UPDATE = 
	        "UPDATE Comment SET rating = ?, text = ? WHERE id = ? AND userId = ?";
	private static final String DELETE = 
	        "DELETE FROM Comment WHERE id = ? AND userId = ?";
	private static final String DELETE_BY_ADMIN = 
	        "DELETE FROM Comment WHERE id = ?";
	
	private Comment buildComment(ResultSet rs) throws SQLException {
		Comment comment = new Comment();
		comment.setId(rs.getInt("id"));	
		comment.setUserId(rs.getInt("userId"));	
		comment.setRecipeId(rs.getInt("recipeId"));	
		comment.setRating(rs.getInt("rating"));	
		comment.setText(rs.getString("text"));	
		comment.setCreatedAt(rs.getTimestamp("createdAt"));	
		comment.setUpdatedAt(rs.getTimestamp("updatedAt"));	
		comment.setUserName(rs.getString("userName"));	
		comment.setRecipeTitle(rs.getString("recipeTitle"));	
		return comment;
	 }
	
	public List<Comment> findAll(){
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		List<Comment> list = new LinkedList<>();
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(SELECT_ALL);
			rs = psmt.executeQuery();
			while(rs.next()) {
				Comment comment = buildComment(rs);
				list.add(comment);
			}
		}catch(SQLException se) {
			throw new RuntimeException("查詢食譜失敗" + se.getMessage());
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
		return list;		
	}
	
	public List<Comment> findByUserId(Integer userId){
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    List<Comment> list = new LinkedList<>();
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(SELECT_BY_USER_ID);
	        psmt.setInt(1, userId);
	        rs = psmt.executeQuery();
	        while(rs.next()) {
	        	Comment comment = buildComment(rs);
				list.add(comment);
			}
	    }catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("查詢用戶評論失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	    return list;
	}
	public List<Comment> findByRecipeId(Integer recipeId){
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    List<Comment> list = new LinkedList<>();
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(SELECT_BY_RECIPE_ID);
	        psmt.setInt(1, recipeId);
	        rs = psmt.executeQuery();
	        while(rs.next()) {
	        	Comment comment = buildComment(rs);
				list.add(comment);
			}
	    }catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("查詢食譜評論失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	    return list;
	}
	
	public Comment insert(Integer userId, Integer recipeId, Integer rating, String text) {
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
	        psmt.setInt(1, userId);
	        psmt.setInt(2, recipeId);
	        psmt.setInt(3, rating);
	        psmt.setString(4, text);
	        //新增是return的是一個整數，代表受影響的行數
	        int rows = psmt.executeUpdate();
	        //行數>0代表新增成功
	        if(rows>0) {
	        	//getGeneratedKeys()會回傳一個ResultSet，裡面包含DB生成的id
	        	rs = psmt.getGeneratedKeys();
	        	if(rs.next()) {
	        		int newId = rs.getInt(1);
	        		Timestamp now = new Timestamp(System.currentTimeMillis());
	        		Comment comment = new Comment();
	        		comment.setId(newId);
	        		comment.setUserId(userId);
	        		comment.setRecipeId(recipeId);
	        		comment.setRating(rating);
	        		comment.setText(text);
	        		comment.setCreatedAt(now);
	        		comment.setUpdatedAt(now);
	        		return comment;
	        	}
	        }
	        throw new RuntimeException("新增評論失敗");
	    }catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("新增評論失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	}
	
	public Integer update(Integer commentId, Integer userId, Integer rating, String text) {
		Connection conn = null;
		PreparedStatement psmt = null;

		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(UPDATE);
			psmt.setInt(1, rating);
	        psmt.setString(2, text);
			psmt.setInt(3, commentId);
	        psmt.setInt(4, userId);        
	        return  psmt.executeUpdate();
		}catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("更新評論失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, null);
	    }
	}
	
	public Integer delete(Integer commentId, Integer userId) {
		Connection conn = null;
	    PreparedStatement psmt = null;

	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(DELETE);
	        psmt.setInt(1, commentId);
	        psmt.setInt(2, userId);        
	        int rows = psmt.executeUpdate();
	        return rows;
	        
	    }catch(SQLException se) {
	    	 se.printStackTrace(); // 印出完整錯誤
		     throw new RuntimeException("用戶刪除食材失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, null);
	    }
	}
	
	public Integer deleteByAdmin(Integer commentId) {
		Connection conn = null;
	    PreparedStatement psmt = null;

	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(DELETE_BY_ADMIN);
	        psmt.setInt(1, commentId);      
	        int rows = psmt.executeUpdate();
	        return rows;
	        
	    }catch(SQLException se) {
	    	 se.printStackTrace(); // 印出完整錯誤
		     throw new RuntimeException("用戶刪除食材失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, null);
	    }
	}
	
}
