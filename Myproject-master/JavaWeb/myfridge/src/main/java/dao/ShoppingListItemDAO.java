package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import model.FridgeItem;
import model.Ingredient;
import model.Recipe;
import model.RecipeIngredient;
import model.ShoppingListItem;
import util.DBUtil;

public class ShoppingListItemDAO {
	private static final String SELECT_BY_USER_ID = "Select "
			+ "id, user_id, recipe_id, recipe_name, ingredient_id, ingredient_name, "
			+ "amount, unit, category, is_purchased, created_at "
			+ "From shopping_list_items "
			+ "Where user_id = ? "
			+ "Order By is_purchased ASC, created_at DESC ";
	
	private static final String SELECT_BY_ID = "Select "
			+ "id, user_id, recipe_id, recipe_name, ingredient_id, ingredient_name, "
			+ "amount, unit, category, is_purchased, created_at "
			+ "From shopping_list_items "
			+ "Where id = ? And user_id = ? ";

	
	private static final String INSERT_ITEM = "INSERT INTO shopping_list_items"
			+ " (user_id, recipe_id, recipe_name, ingredient_id, ingredient_name,"
			+ "amount, unit, category, is_purchased)"
			+ " VALUES (?,?,?,?,?,?,?,?,?)";
	
	private static final String UPDATE_ITEM = 
	        "UPDATE shopping_list_items " +
	        "SET ingredient_name = ?, amount = ?, unit = ?, category = ?, is_purchased = ? " +
	        "WHERE id = ? AND user_id = ?";
	    
	    private static final String UPDATE_PURCHASED_STATUS = 
	        "UPDATE shopping_list_items " +
	        "SET is_purchased = ? " +
	        "WHERE id = ? AND user_id = ?";
	    
	    private static final String DELETE_ITEM = 
	        "DELETE FROM shopping_list_items " +
	        "WHERE id = ? AND user_id = ?";
	    
	    private static final String SELECT_PURCHASED_ITEMS = 
	        "SELECT id, user_id, recipe_id, recipe_name, ingredient_id, ingredient_name, " +
	        "amount, unit, category, is_purchased, created_at " +
	        "FROM shopping_list_items " +
	        "WHERE user_id = ? AND is_purchased = TRUE";
	    
	    private static final String DELETE_PURCHASED_ITEMS = 
	        "DELETE FROM shopping_list_items " +
	        "WHERE user_id = ? AND is_purchased = TRUE";
	    
	    private ShoppingListItem buildShoppingItem(ResultSet rs) throws SQLException {
	    	ShoppingListItem shoppingItem = new ShoppingListItem();
	    	shoppingItem.setId(rs.getInt("id"));	
	    	shoppingItem.setUserId(rs.getInt("user_id"));	 	  
	    	shoppingItem.setRecipeId(rs.getObject("recipe_id") != null ? rs.getInt("recipe_id") : null);	 
	    	shoppingItem.setRecipeName(rs.getString("recipe_name"));	 
	    	shoppingItem.setIngredientId(rs.getObject("ingredient_id") != null ? rs.getInt("ingredient_id") : null);	 
	    	shoppingItem.setIngredientName(rs.getString("ingredient_name"));	 
	    	shoppingItem.setAmount(rs.getDouble("amount")); 
	    	shoppingItem.setUnit(rs.getString("unit"));
	    	shoppingItem.setCategory(rs.getString("category"));
	    	shoppingItem.setIsPurchased(rs.getBoolean("is_purchased"));
	    	shoppingItem.setCreatedAt(rs.getTimestamp("created_at"));	
			return shoppingItem;
		 }
	    
	    public List<ShoppingListItem> findByUserId(Integer userId){
	    	Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			List<ShoppingListItem> list = new LinkedList<>();
			try {
				conn = DBUtil.getConnection();
				psmt = conn.prepareStatement(SELECT_BY_USER_ID);
				psmt.setInt(1, userId);
				rs = psmt.executeQuery();
				while(rs.next()) {
					ShoppingListItem item = buildShoppingItem(rs);
					list.add(item);
				}
			}catch(SQLException se) {
				throw new RuntimeException("查詢食譜失敗" + se.getMessage());
			}finally {
				DBUtil.close(conn, psmt, rs);
			}
			return list;
	    }
	    
	    public ShoppingListItem findById(Integer id, Integer userId){
	    	Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			List<ShoppingListItem> list = new LinkedList<>();
			try {
				conn = DBUtil.getConnection();
				psmt = conn.prepareStatement(SELECT_BY_ID);
				psmt.setInt(1, id);
				psmt.setInt(2, userId);				
				rs = psmt.executeQuery();
				if	(rs.next()) {
					ShoppingListItem item = buildShoppingItem(rs);
					return item;
				}
				return null;
			}catch(SQLException se) {
				throw new RuntimeException("查詢食譜失敗" + se.getMessage());
			}finally {
				DBUtil.close(conn, psmt, rs);
			}
	    }
	    
	    public ShoppingListItem insertItem(ShoppingListItem item) {
			Connection conn = null;
		    PreparedStatement psmt = null;
		    ResultSet rs = null;
		    try {
		        conn = DBUtil.getConnection();
		        psmt = conn.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS);
		        psmt.setInt(1, item.getUserId());
	            psmt.setObject(2, item.getRecipeId());
	            psmt.setString(3, item.getRecipeName());
	            psmt.setObject(4, item.getIngredientId());
	            psmt.setString(5, item.getIngredientName());
	            psmt.setDouble(6, item.getAmount());
	            psmt.setString(7, item.getUnit());
	            psmt.setString(8, item.getCategory());
	            psmt.setBoolean(9, item.getIsPurchased() != null ? item.getIsPurchased() : false);
		        int rows = psmt.executeUpdate();
		        if (rows > 0) {
		            rs = psmt.getGeneratedKeys();
		            if (rs.next()) {
		                int newId = rs.getInt(1);
		                item.setId(newId);
		            }
		        }
		        return item	;
		    }catch(SQLException se) {
		    	 se.printStackTrace();  // 印出完整錯誤
			     throw new RuntimeException("用戶新增食材失敗", se);
		    }finally {
		        DBUtil.close(conn, psmt, rs);
		    }
		}
	    
	    public ShoppingListItem updateItem(Integer userId, Integer id, ShoppingListItem item) {
			Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;

			try {
				conn = DBUtil.getConnection();
				psmt = conn.prepareStatement(UPDATE_ITEM);
				psmt.setString(1, item.getIngredientName());
	            psmt.setDouble(2, item.getAmount());
	            psmt.setString(3, item.getUnit());
	            psmt.setString(4, item.getCategory());
	            psmt.setBoolean(5, item.getIsPurchased());
	            psmt.setInt(6, id);
	            psmt.setInt(7, userId);;
	            int rows = psmt.executeUpdate();
	            if(rows > 0) {
	            	return(findById(id,userId));
	            }
		        return null;
			}catch(SQLException se) {
		    	 se.printStackTrace();  // 印出完整錯誤
			     throw new RuntimeException("用戶更新食材失敗", se);
		    }finally {
		        DBUtil.close(conn, psmt, rs);
		    }
		}
	    
	    public ShoppingListItem updatePurchasedItem(Integer userId, Integer id, Boolean isPurchased) {
			Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;

			try {
				conn = DBUtil.getConnection();
				psmt = conn.prepareStatement(UPDATE_PURCHASED_STATUS);
				psmt.setBoolean(1, isPurchased);
	            psmt.setInt(2, id);
	            psmt.setInt(3, userId);
	            int rows = psmt.executeUpdate();
	            if(rows > 0) {
	            	return(findById(id,userId));
	            }
		        return null;
			}catch(SQLException se) {
		    	 se.printStackTrace();  // 印出完整錯誤
			     throw new RuntimeException("用戶更新食材失敗", se);
		    }finally {
		        DBUtil.close(conn, psmt, rs);
		    }
		}
	    
	    public Integer deleteItem(Integer userId, Integer id) {
			Connection conn = null;
		    PreparedStatement psmt = null;
		    ResultSet rs = null;
		    try {
		        conn = DBUtil.getConnection();
		        psmt = conn.prepareStatement(DELETE_ITEM);
		        psmt.setInt(1, id);
		        psmt.setInt(2, userId);        
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
	    
	    public List<ShoppingListItem> findPurchasedItem(Integer userId){
	    	Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			List<ShoppingListItem> list = new LinkedList<>();
			try {
				conn = DBUtil.getConnection();
				psmt = conn.prepareStatement(SELECT_PURCHASED_ITEMS);
				psmt.setInt(1, userId);
				rs = psmt.executeQuery();
				while(rs.next()) {
					ShoppingListItem item = buildShoppingItem(rs);
					list.add(item);
				}
			}catch(SQLException se) {
				throw new RuntimeException("查詢食譜失敗" + se.getMessage());
			}finally {
				DBUtil.close(conn, psmt, rs);
			}
			return list;
	    }
	    
	    public Integer deletePurchasedItem(Integer userId) {
			Connection conn = null;
		    PreparedStatement psmt = null;
		    ResultSet rs = null;
		    try {
		        conn = DBUtil.getConnection();
		        psmt = conn.prepareStatement(DELETE_PURCHASED_ITEMS);
		        psmt.setInt(1, userId);        
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
