package util;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.*;
import javax.sql.*;

public class DBUtil {
	private static DataSource datasource;  
	static {
		try {
			Context ctx = new InitialContext();
			datasource = (DataSource)ctx.lookup("java:comp/env/jdbc/MyFridge");
		} catch (NamingException e) {
			throw new RuntimeException("DataSource could not be found.",e);
		}
	}
	public static Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}
	public static void close(Connection conn, PreparedStatement psmt, ResultSet rs) {
		try {
			if(conn!=null) {
				conn.close();
			}
			if(psmt!=null) {
				psmt.close();
			}
			if(rs!=null) {
				rs.close();
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
