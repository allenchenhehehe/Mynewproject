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
import org.springframework.transaction.annotation.Transactional;

import com.myfridge.myfridge.entity.Comment;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

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

    private final RowMapper<Comment> adminrowMapper = (rs, rowNum) -> {

        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));	
        comment.setUserId(rs.getInt("userId"));	
        comment.setRecipeId(rs.getInt("recipeId"));	
        comment.setRating(rs.getInt("rating"));	
        comment.setText(rs.getString("text"));	

        Timestamp createdAt = rs.getTimestamp("createdAt");
        comment.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        
        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        comment.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        
        comment.setUserName(rs.getString("userName"));	
        comment.setRecipeTitle(rs.getString("recipeTitle"));	

        return comment;

    };

    // 查詢食譜評論用的 RowMapper（只有 userName）
    private final RowMapper<Comment> recipeCommentRowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setUserId(rs.getInt("userId"));
        comment.setRecipeId(rs.getInt("recipeId"));
        comment.setRating(rs.getInt("rating"));
        comment.setText(rs.getString("text"));
        
        Timestamp createdAt = rs.getTimestamp("createdAt");
        comment.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        
        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        comment.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        
        comment.setUserName(rs.getString("userName"));
        
        return comment;
    };
    
    // 查詢使用者評論用的 RowMapper（只有 recipeTitle）
    private final RowMapper<Comment> userCommentRowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setUserId(rs.getInt("userId"));
        comment.setRecipeId(rs.getInt("recipeId"));
        comment.setRating(rs.getInt("rating"));
        comment.setText(rs.getString("text"));
        
        Timestamp createdAt = rs.getTimestamp("createdAt");
        comment.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        
        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        comment.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        
        comment.setRecipeTitle(rs.getString("recipeTitle"));
        
        return comment;
    };

    // 查詢所有評論（管理員用）
    public List<Comment> findAll(){
        return jdbcTemplate.query(SELECT_ALL, adminrowMapper);
    }

    public List<Comment> findByUserId(Integer userId){
        return jdbcTemplate.query(SELECT_BY_USER_ID, userCommentRowMapper, userId);
    }

    public List<Comment> findByRecipeId(Integer recipeId){
        return jdbcTemplate.query(SELECT_BY_RECIPE_ID, recipeCommentRowMapper, recipeId);
    }

    public Comment insert(Integer userId, Integer recipeId, Integer rating, String text) {
        //KeyHolder：用來存放自動生成的主鍵（ID）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //jdbcTemplate.update() 執行 INSERT
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            ps.setInt(3, rating);
            ps.setString(4, text);
            return ps;
        }, keyHolder);
        
        Comment comment = new Comment();
        comment.setId(keyHolder.getKey().intValue());
        comment.setUserId(userId);
        comment.setRecipeId(recipeId);
        comment.setRating(rating);
        comment.setText(text);
        
        return comment;
    }

    @Transactional
    public int update(Integer commentId, Integer userId, Integer rating, String text) {
        return jdbcTemplate.update(UPDATE, rating, text, commentId, userId);
    }

    @Transactional
    public int delete(Integer commentId, Integer userId) {
        return jdbcTemplate.update(DELETE, commentId, userId);
    }

    @Transactional
    public int deleteByAdmin(Integer commentId) {
        return jdbcTemplate.update(DELETE_BY_ADMIN, commentId);
    }
}
