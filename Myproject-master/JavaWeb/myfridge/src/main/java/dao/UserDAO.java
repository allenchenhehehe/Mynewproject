package dao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import model.Ingredient;
import model.User;
import util.DBUtil;

public class UserDAO {
	private static final String SELECT_ALL = 
			"SELECT * FROM USER";
	public List<User> findAll(){
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		List<User> list = new LinkedList<User>();
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(SELECT_ALL);
			rs = psmt.executeQuery();
			while(rs.next()) {
				User user = new User(
						rs.getInt("id"), 
						rs.getString("userName"),
						rs.getString("email"),
						rs.getString("password"),
						rs.getTimestamp("createdAt") != null ? rs.getTimestamp("createdAt").toLocalDateTime() : null,
					    rs.getTimestamp("updatedAt") != null ? rs.getTimestamp("updatedAt").toLocalDateTime() : null
						);
				list.add(user);
			}
			
		}catch(SQLException se) {
			throw new RuntimeException("查詢用戶失敗", se);
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
		return list;	
	}
	
	private static final String SELECT_BY_ID = 
			"SELECT * FROM USER WHERE ID = ?";
	public User findById(Integer id){
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(SELECT_BY_ID);
			psmt.setInt(1, id);
			rs = psmt.executeQuery();
			if(rs.next()) {
				return new User(
						rs.getInt("id"), 
						rs.getString("userName"),
						rs.getString("email"),
						rs.getString("password"),
						rs.getTimestamp("createdAt") != null ? rs.getTimestamp("createdAt").toLocalDateTime() : null,
					    rs.getTimestamp("updatedAt") != null ? rs.getTimestamp("updatedAt").toLocalDateTime() : null
						);
			}
			return null;	
		}catch(SQLException se) {
			throw new RuntimeException("查詢用戶失敗", se);
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
	}
	
	private static final String SELECT_BY_EMAIL = 
			"SELECT * FROM USER  WHERE EMAIL = ?";
	public User findByEmail(String email){
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(SELECT_BY_EMAIL);
			psmt.setString(1, email);
			rs = psmt.executeQuery();
			if(rs.next()) {
				return new User(
						rs.getInt("id"), 
						rs.getString("userName"),
						rs.getString("email"),
						rs.getString("password"),
						rs.getTimestamp("createdAt") != null ? rs.getTimestamp("createdAt").toLocalDateTime() : null,
					    rs.getTimestamp("updatedAt") != null ? rs.getTimestamp("updatedAt").toLocalDateTime() : null
						);
			}
			return null;	
		}catch(SQLException se) {
			throw new RuntimeException("查詢用戶失敗", se);
		}finally {
			DBUtil.close(conn, psmt, rs);
		}
	}
	
	private static final String INSERT_USER= 
			"INSERT INTO USER (USERNAME,EMAIL,PASSWORD) VALUES (?,?,?)";
	public User insert(User user) {
	    Connection conn = null;
	    PreparedStatement psmt = null;
	    ResultSet rs = null;
	    try {
	        conn = DBUtil.getConnection();
	        psmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
	        psmt.setString(1, user.getUserName());
	        psmt.setString(2, user.getEmail());
	        psmt.setString(3, user.getPassword());
	        
	        int rows = psmt.executeUpdate();     
	        rs = psmt.getGeneratedKeys();
	        if(rs.next()) {
	            int newId = rs.getInt(1);
	            System.out.println("新增的 ID: " + newId);
	            user.setId(newId);
	            return user;
	        }
	        
	        throw new RuntimeException("無法取得新增的 ID");   
	        
	    } catch (SQLException e) {
	        e.printStackTrace();  // 印出完整錯誤
	        throw new RuntimeException("新增用戶失敗", e);
	    } finally {
	        DBUtil.close(conn, psmt, rs);
	    }   
	}
}
