package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.FridgeItem;
import model.Ingredient;
import model.User;
import util.DBUtil;

public class FridgeItemDAO {
	private static final String SELECT_BY_USER_ID = 
			"SELECT f.id, f.user_id, f.ingredient_id, f.amount, f.unit, " +
	        "f.purchased_date, f.expired_date, " +
	        "i.ingredient_name, i.category " +
	        "FROM fridge_items f " +
	        "JOIN ingredients i ON f.ingredient_id = i.id " +
	        "WHERE f.user_id = ?";
	public List<FridgeItem> findByUserId(Integer userId) {
		 	Connection conn = null;
		    PreparedStatement psmt = null;
		    ResultSet rs = null;
		    List<FridgeItem> list = new LinkedList<>();
		    try {
		        conn = DBUtil.getConnection();
		        psmt = conn.prepareStatement(SELECT_BY_USER_ID);
		        psmt.setInt(1, userId);
		        rs = psmt.executeQuery();
		        while(rs.next()) {
					FridgeItem item = new FridgeItem(
							rs.getInt("id"),
							rs.getInt("user_id"),
							rs.getInt("ingredient_id"),
							rs.getInt("amount"),
							rs.getString("unit"),
							rs.getDate("purchased_date").toLocalDate(),
							rs.getDate("expired_date") != null ?
									rs.getDate("expired_date").toLocalDate():null,
							rs.getString("ingredient_name"),
							rs.getString("category")
					);
					list.add(item);
				}
		    }catch(SQLException se) {
		    	 se.printStackTrace();  // 印出完整錯誤
			     throw new RuntimeException("用戶查詢食材失敗", se);
		    }finally {
		        DBUtil.close(conn, psmt, rs);
		    }
		    return list;
	    }
	
	private static final String SELECT_BY_ID = 
			"SELECT f.id, f.user_id, f.ingredient_id, f.amount, f.unit, " +
	        "f.purchased_date, f.expired_date, " +
	        "i.ingredient_name, i.category " +
	        "FROM fridge_items f " +
	        "JOIN ingredients i ON f.ingredient_id = i.id " +
	        "WHERE f.id = ?";
	public FridgeItem findById(Integer id) {
		 	Connection conn = null;
		    PreparedStatement psmt = null;
		    ResultSet rs = null;

		    try {
		        conn = DBUtil.getConnection();
		        psmt = conn.prepareStatement(SELECT_BY_ID);
		        psmt.setInt(1, id);
		        rs = psmt.executeQuery();
		        if(rs.next()) {
					FridgeItem item = new FridgeItem(
							rs.getInt("id"),
							rs.getInt("user_id"),
							rs.getInt("ingredient_id"),
							rs.getInt("amount"),
							rs.getString("unit"),
							rs.getDate("purchased_date").toLocalDate(),
							rs.getDate("expired_date") != null ?
									rs.getDate("expired_date").toLocalDate():null,
							rs.getString("ingredient_name"),
							rs.getString("category")					
					);
					return item;
				}
		        return null;
		    }catch(SQLException se) {
		    	 se.printStackTrace();  // 印出完整錯誤
			     throw new RuntimeException("用戶查詢食材失敗", se);
		    }finally {
		        DBUtil.close(conn, psmt, rs);
		    }
	    }
	
	private static final String INSERT_ITEM = 
			"INSERT INTO FRIDGE_ITEMS(USER_ID,INGREDIENT_ID,AMOUNT,UNIT,PURCHASED_DATE,EXPIRED_DATE)"
			+ " VALUES(?,?,?,?,?,?)";
	public FridgeItem insert(FridgeItem item) {
			Connection conn = null;
		    PreparedStatement psmt = null;
		    ResultSet rs = null;
		    try {
		        conn = DBUtil.getConnection();
		        psmt = conn.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS);
		        psmt.setInt(1,item.getUserId());
		        psmt.setInt(2,item.getIngredientId());
		        psmt.setInt(3,item.getAmount());
		        psmt.setString(4,item.getUnit());
		        psmt.setDate(5,java.sql.Date.valueOf(item.getPurchasedDate())); 
		        if (item.getExpiredDate() != null) {
		            psmt.setDate(6, java.sql.Date.valueOf(item.getExpiredDate()));
		        } else {
		            psmt.setNull(6, Types.DATE);
		        }
		        int rows = psmt.executeUpdate();
		        if (rows > 0) {
		            rs = psmt.getGeneratedKeys();
		            if (rs.next()) {
		                int newId = rs.getInt(1);
		                item.setId(newId);
		            }
		        }
		        return item;
		    }catch(SQLException se) {
		    	 se.printStackTrace();  // 印出完整錯誤
			     throw new RuntimeException("用戶新增食材失敗", se);
		    }finally {
		        DBUtil.close(conn, psmt, rs);
		    }
	}
	
	private static final String DELETE_ITEM= 
			"DELETE  FROM FRIDGE_ITEMS WHERE ID=? AND user_id = ?";
	public boolean deleteItem(Integer userId, Integer id) {
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    try {
	        conn = DBUtil.getConnection();
	        conn.setAutoCommit(false);
	        psmt = conn.prepareStatement(DELETE_ITEM);
	        psmt.setInt(1, id);
	        psmt.setInt(2, userId);        
	        int rows = psmt.executeUpdate();
	        conn.commit();
	        return rows>0;
	        
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
	
	private static final String UPDATE_ITEM= 
			"UPDATE FRIDGE_ITEMS SET "
			+ "AMOUNT=?, UNIT=?, PURCHASED_DATE=?, EXPIRED_DATE=? WHERE ID=? AND user_id = ?";
	public FridgeItem updateItem(FridgeItem item) {
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(UPDATE_ITEM);
			psmt.setInt(1,item.getAmount());
			psmt.setString(2,item.getUnit());
	        psmt.setDate(3,java.sql.Date.valueOf(item.getPurchasedDate()));
	        if (item.getExpiredDate() != null) {
	            psmt.setDate(4, java.sql.Date.valueOf(item.getExpiredDate()));
	        } else {
	            psmt.setNull(4, Types.DATE);
	        }
	        psmt.setInt(5,item.getId());
	        psmt.setInt(6,item.getUserId());
	        int rows =psmt.executeUpdate();
	        if (rows == 0) {
	            throw new RuntimeException("找不到此食材,更新失敗");
	        }
	        return item;
		}catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("用戶更新食材失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	}
}
