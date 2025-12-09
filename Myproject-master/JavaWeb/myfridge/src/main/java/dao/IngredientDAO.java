package dao;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import javax.sql.*;
import model.Ingredient;
import model.Recipe;

public class IngredientDAO {
	//sql statement
	private static final String SELECT_ALL = "SELECT * FROM INGREDIENT";
	
	private static final String SELECT_BY_ID = "SELECT * FROM INGREDIENT WHERE ID=?";
	
	private static final String SELECT_BY_NAME = "SELECT * FROM INGREDIENT"
			+ " WHERE INGREDIENTNAME=?";
	
	private static final String INSERT_INGREDIENT = "INSERT INTO INGREDIENT"
			+ " (INGREDIENTNAME,CATEGORY,SHELFLIFEDAYS) VALUES (?,?,?)";
	
	//default constructor
	public IngredientDAO() {};
	
	//helper(Get Ingredient Object)
	private Ingredient buildIngredient(ResultSet rs) throws SQLException {
		Ingredient ingredient = new Ingredient();
		ingredient.setId(rs.getInt("id"));		 	  
		ingredient.setIngredientName(rs.getString("ingredientName"));	 
		ingredient.setCategory(rs.getString("category"));	 
		ingredient.setShelfLifeDays(rs.getInt("shelfLifeDays"));	 
		return ingredient;
	 }
	
	//查詢所有
	public List<Ingredient> findAll(){
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		List<Ingredient> list = new LinkedList<Ingredient>();
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(SELECT_ALL);
			rs = psmt.executeQuery();
			while(rs.next()) {
				Ingredient ingredients = buildIngredient(rs);
				list.add(ingredients);
			}	
		}catch(SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
		return list;		
	}
	
	//用Id查詢
	public Ingredient findById(Integer id){
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(SELECT_BY_ID);
			psmt.setInt(1, id);
			rs = psmt.executeQuery();
			if(rs.next()) {
				Ingredient ingredients = buildIngredient(rs);
				return ingredients;
			}
			return null;	
		}catch(SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
	}
	
	//用名稱查詢
	public Ingredient findByName(String name){
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(SELECT_BY_NAME);
			psmt.setString(1, name);
			rs = psmt.executeQuery();
			if(rs.next()) {
				Ingredient ingredients = buildIngredient(rs);
				return ingredients;
			}
			return null;	
		}catch(SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
	}
	
	//當使用者新增食材，如果沒有在ingredients就新增進去
	public Ingredient insert (Ingredient ingre) {
		Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(INSERT_INGREDIENT, Statement.RETURN_GENERATED_KEYS);
	        psmt.setString(1,ingre.getIngredientName());
	        psmt.setString(2,ingre.getCategory());
	        psmt.setInt(3,ingre.getShelfLifeDays());
	        int rows = psmt.executeUpdate();
	        if (rows > 0) {
	            rs = psmt.getGeneratedKeys();
	            if (rs.next()) {
	                int newId = rs.getInt(1);
	                ingre.setId(newId);
	            }
	        }
	        return ingre	;
	    }catch(SQLException se) {
	    	 se.printStackTrace();  // 印出完整錯誤
		     throw new RuntimeException("用戶新增食材失敗", se);
	    }finally {
	        DBUtil.close(conn, psmt, rs);
	    }
	}
	
	public Integer findOrCreate(String ingredientName, String category) {
	    if (ingredientName == null || ingredientName.trim().isEmpty()) {
	        throw new IllegalArgumentException("食材名稱不可為空");
	    }
	    
	    try {
	        // 1. 先查詢是否已存在
	        Ingredient existing = findByName(ingredientName);
	        
	        if (existing != null) {
	            // 已存在,回傳 ID
	            return existing.getId();
	        }
	        
	        // 2. 不存在,建立新的
	        Ingredient newIngredient = new Ingredient();
	        newIngredient.setIngredientName(ingredientName);
	        newIngredient.setCategory(category != null ? category : "other");
	        newIngredient.setShelfLifeDays(7);  // ✅ 簡單的預設值就好
	        
	        Ingredient inserted = insert(newIngredient);
	        return inserted.getId();
	        
	    } catch (Exception e) {
	        System.err.println("findOrCreate 失敗 (食材: " + ingredientName + "): " + e.getMessage());
	        throw new RuntimeException("處理食材失敗", e);
	    }
	}
	
}
	