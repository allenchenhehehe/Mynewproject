package dao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import model.Ingredient;
import model.User;
import util.DBUtil;

public class UserDAO {
	private static final String SELECT_ALL = 
			"SELECT * FROM USERS";
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
						rs.getString("username"),
						rs.getString("email"),
						rs.getString("password"),
						rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
					    rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
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
			"SELECT * FROM USERS WHERE ID = ?";
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
						rs.getString("username"),
						rs.getString("email"),
						rs.getString("password"),
						rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
					    rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
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
			"SELECT * FROM USERS  WHERE EMAIL = ?";
	public User findByEmail(String email){
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(SELECT_BY_EMAIL);
			psmt.setString(1, email);
			rs = psmt.executeQuery();
			while(rs.next()) {
				return new User(
						rs.getInt("id"), 
						rs.getString("username"),
						rs.getString("email"),
						rs.getString("password"),
						rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
					    rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
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
			"INSERT INTO USERS (USERNAME,EMAIL,PASSWORD) VALUES (?,?,?)";
	public User insert(User user) {
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			psmt = conn.prepareStatement(INSERT_USER,Statement.RETURN_GENERATED_KEYS);
			psmt.setString(1, user.getUserName());
			psmt.setString(2, user.getEmail());
			psmt.setString(3, user.getPassword());
			psmt.executeUpdate();
			
			rs = psmt.getGeneratedKeys();
			if(rs.next()) {
				user.setId(rs.getInt(1));
			}
			conn.commit();
			return user;
		} catch (SQLException e) {
			try {
	            if (conn != null) {
	                conn.rollback(); 
	            }
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
			throw new RuntimeException("新增用戶失敗", e);
		}finally {
			DBUtil.close(conn, psmt, rs);
		}	
	}
}
