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

public class IngredientDAO {
	private static final String SELECT_ALL = "SELECT * FROM INGREDIENTS";
	public IngredientDAO() {};
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
				Ingredient ingredients = new Ingredient(
						rs.getInt("id"), 
						rs.getString("ingredient_name"),
						rs.getString("category"),
						rs.getInt("shelf_life_days")
						);
				list.add(ingredients);
			}
			
		}catch(SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
		
		return list;	
		
	}
	
	private static final String SELECT_BY_ID = "SELECT * FROM INGREDIENTS WHERE ID=?";
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
				return new Ingredient(
						rs.getInt("id"), 
						rs.getString("ingredient_name"),
						rs.getString("category"),
						rs.getInt("shelf_life_days")
						);
			}
			return null;	
		}catch(SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
	}
	
	private static final String SELECT_BY_NAME = "SELECT * FROM INGREDIENTS WHERE INGREDIENT_NAME=?";
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
				return new Ingredient(
						rs.getInt("id"), 
						rs.getString("ingredient_name"),
						rs.getString("category"),
						rs.getInt("shelf_life_days")
						);
			}
			return null;	
		}catch(SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
	}
	
	private static final String INSERT_INGREDIENT = 
			"INSERT INTO INGREDIENTS (INGREDIENT_NAME,CATEGORY,SHELF_LIFE_DAYS) VALUES (?,?,?)";
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
}
	