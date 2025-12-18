package com.myfridge.myfridge.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.myfridge.myfridge.entity.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL = 
			"SELECT id, userName, email, role, createdAt, updatedAt FROM User ORDER BY id";

    private static final String SELECT_BY_ID = 
			"SELECT * FROM USER WHERE ID = ?";

    private static final String SELECT_BY_EMAIL = 
			"SELECT * FROM USER  WHERE EMAIL = ?";

    private static final String INSERT_USER= 
			"INSERT INTO USER (USERNAME,EMAIL,PASSWORD) VALUES (?,?,?)";

    private final RowMapper<User> fullRowMapper = (rs, rowNum) -> {

		User user = new User();
		
        user.setId(rs.getInt("id"));
        user.setUserName(rs.getString("userName"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setPassword(rs.getString("password"));

        Timestamp createdAt = rs.getTimestamp("createdAt");
        user.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        
        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        user.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
	 
		return user;
	};

    private final RowMapper<User> publicRowMapper = (rs, rowNum) -> {

		User user = new User();
		
        user.setId(rs.getInt("id"));
        user.setUserName(rs.getString("userName"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));

        Timestamp createdAt = rs.getTimestamp("createdAt");
        user.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        
        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        user.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
	 
		return user;
	};

    public List<User> findAll(){
        return jdbcTemplate.query(SELECT_ALL, publicRowMapper);
    }

    public User findById(Integer id){
        List<User> user =  jdbcTemplate.query(SELECT_BY_ID, fullRowMapper, id);
        return user.isEmpty() ? null : user.get(0);
    }

    public User findByEmail(String email){
        List<User> user =  jdbcTemplate.query(SELECT_BY_EMAIL, fullRowMapper, email);
        return user.isEmpty() ? null : user.get(0);
    }

    public User insert(User user){

        //KeyHolder：用來存放自動生成的主鍵（ID）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //jdbcTemplate.update() 執行 INSERT
        jdbcTemplate.update(connection -> {

            //建立 PreparedStatement，指定要回傳生成的 Key
            PreparedStatement ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            //回傳 PreparedStatement 給 Spring 執行
            return ps;
        }, keyHolder);//keyHolder 會接收生成的 ID

        //從 keyHolder 取出 ID，設定回物件
        user.setId(keyHolder.getKey().intValue());
        //回傳帶有 ID 的物件
        return findById(user.getId());

    }
}
