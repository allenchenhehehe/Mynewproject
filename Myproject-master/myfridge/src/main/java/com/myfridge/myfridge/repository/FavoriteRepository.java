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

import com.myfridge.myfridge.entity.Favorite;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FavoriteRepository {
    
    private final JdbcTemplate jdbcTemplate;

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

    private final RowMapper<Favorite> rowMapper = (rs, rowNum) -> {

		Favorite favorite = new Favorite();
        favorite.setId(rs.getInt("id"));
        favorite.setUserId(rs.getInt("userId"));
        favorite.setRecipeId(rs.getInt("recipeId"));
        
        // Timestamp → LocalDateTime
        Timestamp savedAt = rs.getTimestamp("savedAt");
        favorite.setSavedAt(savedAt != null ? savedAt.toLocalDateTime() : null);
        
        // JOIN 來的 Recipe 資料
        favorite.setTitle(rs.getString("title"));
        favorite.setDescription(rs.getString("description"));
        favorite.setImageUrl(rs.getString("imageUrl"));
        favorite.setCookingTime(rs.getInt("cookingTime"));
        favorite.setDifficulty(rs.getInt("difficulty"));
        
        return favorite;
	};

    private final RowMapper<Favorite> adminrowMapper = (rs, rowNum) -> {

		Favorite favorite = new Favorite();
        favorite.setId(rs.getInt("id"));
        favorite.setUserId(rs.getInt("userId"));
        favorite.setRecipeId(rs.getInt("recipeId"));
        
        // Timestamp → LocalDateTime
        Timestamp savedAt = rs.getTimestamp("savedAt");
        favorite.setSavedAt(savedAt != null ? savedAt.toLocalDateTime() : null);
        
        // 管理員需要的資訊
        favorite.setUserName(rs.getString("userName"));
        favorite.setRecipeTitle(rs.getString("recipeTitle"));
        
        return favorite;
	};

    //查詢所有收藏（管理員用)
    public List<Favorite> findAll(){
        return jdbcTemplate.query(SELECT_ALL, adminrowMapper);
    }

    public List<Favorite> findByUserId(Integer userId){
        return jdbcTemplate.query(SELECT_BY_USER_ID, rowMapper, userId);
    }

    public Boolean checkExists(Integer userId, Integer recipeId) {
        Integer count = jdbcTemplate.queryForObject(
            CHECK_EXISTS,
            Integer.class,
            userId,
            recipeId
        );
        return count != null && count > 0;
    }

    public Favorite insert(Integer userId, Integer recipeId){

        //KeyHolder：用來存放自動生成的主鍵（ID）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //jdbcTemplate.update() 執行 INSERT
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            return ps;
        }, keyHolder);
        
        Favorite favorite = new Favorite();
        favorite.setId(keyHolder.getKey().intValue());
        favorite.setUserId(userId);
        favorite.setRecipeId(recipeId);
        
        return favorite;

    }

    public Integer deleteItem(Integer userId, Integer recipeId) {
        return jdbcTemplate.update(DELETE, userId, recipeId);
    }
    
}
