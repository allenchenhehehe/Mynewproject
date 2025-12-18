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

import com.myfridge.myfridge.entity.ShoppingListItem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShoppingListItemRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BY_USER_ID = "Select "
			+ "id, userId, recipeId, recipeName, ingredientId, ingredientName, "
			+ "amount, unit, category, isPurchased, createdAt "
			+ "From ShoppingListItem "
			+ "Where userId = ? "
			+ "Order By isPurchased ASC, createdAt DESC ";
	
	private static final String SELECT_BY_ID = "Select "
			+ "id, userId, recipeId, recipeName, ingredientId, ingredientName, "
			+ "amount, unit, category, isPurchased, createdAt "
			+ "From ShoppingListItem "
			+ "Where id = ? And userId = ? ";

	
	private static final String INSERT_ITEM = "INSERT INTO ShoppingListItem"
			+ " (userId, recipeId, recipeName, ingredientId, ingredientName,"
			+ "amount, unit, category, isPurchased)"
			+ " VALUES (?,?,?,?,?,?,?,?,?)";
	
	private static final String UPDATE_ITEM = 
	        "UPDATE ShoppingListItem " +
	        "SET ingredientName = ?, amount = ?, unit = ?, category = ?, isPurchased = ? " +
	        "WHERE id = ? AND userId = ?";
	    
    private static final String UPDATE_PURCHASED_STATUS = 
        "UPDATE ShoppingListItem " +
        "SET isPurchased = ? " +
        "WHERE id = ? AND userId = ?";
    
    private static final String DELETE_ITEM = 
        "DELETE FROM ShoppingListItem " +
        "WHERE id = ? AND userId = ?";
    
    private static final String SELECT_PURCHASED_ITEMS = 
        "SELECT id, userId, recipeId, recipeName, ingredientId, ingredientName, " +
        "amount, unit, category, isPurchased, createdAt " +
        "FROM ShoppingListItem " +
        "WHERE userId = ? AND isPurchased = TRUE";
    
    private static final String DELETE_PURCHASED_ITEMS = 
        "DELETE FROM ShoppingListItem " +
        "WHERE userId = ? AND isPurchased = TRUE";


    private final RowMapper<ShoppingListItem> rowMapper = (rs, rowNum) -> {
        ShoppingListItem item = new ShoppingListItem();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("userId"));
        
        // 處理可能為 null 的欄位
        item.setRecipeId(rs.getObject("recipeId") != null ? rs.getInt("recipeId") : null);
        item.setRecipeName(rs.getString("recipeName"));
        item.setIngredientId(rs.getObject("ingredientId") != null ? rs.getInt("ingredientId") : null);
        
        item.setIngredientName(rs.getString("ingredientName"));
        item.setAmount(rs.getDouble("amount"));
        item.setUnit(rs.getString("unit"));
        item.setCategory(rs.getString("category"));
        item.setIsPurchased(rs.getBoolean("isPurchased"));
        
        Timestamp createdAt = rs.getTimestamp("createdAt");
        item.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        
        return item;
    };


    public List<ShoppingListItem> findByUserId(Integer id){
        return jdbcTemplate.query(SELECT_BY_USER_ID, rowMapper, id);
    }

    public ShoppingListItem findById(Integer id, Integer userId){
        List<ShoppingListItem> item = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id, userId);
        return item.isEmpty() ? null : item.get(0);
    }

    public ShoppingListItem insert(ShoppingListItem item){

        //KeyHolder：用來存放自動生成的主鍵（ID）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //jdbcTemplate.update() 執行 INSERT
        jdbcTemplate.update(connection -> {

            //建立 PreparedStatement，指定要回傳生成的 Key
            PreparedStatement ps = connection.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, item.getUserId());
            ps.setObject(2, item.getRecipeId());
            ps.setString(3, item.getRecipeName());
            ps.setObject(4, item.getIngredientId());
            ps.setString(5, item.getIngredientName());
            ps.setDouble(6, item.getAmount());
            ps.setString(7, item.getUnit());
            ps.setString(8, item.getCategory());
            ps.setBoolean(9, item.getIsPurchased() != null ? item.getIsPurchased() : false);

            //回傳 PreparedStatement 給 Spring 執行
            return ps;
        }, keyHolder);//keyHolder 會接收生成的 ID

        //從 keyHolder 取出 ID，設定回物件
        item.setId(keyHolder.getKey().intValue());
        //回傳帶有 ID 的物件
        return item;

    }

    @Transactional
    public Integer update(Integer userId, Integer id, ShoppingListItem item) {
        return jdbcTemplate.update(
            UPDATE_ITEM,
            item.getIngredientName(),
            item.getAmount(),
            item.getUnit(),
            item.getCategory(),
            item.getIsPurchased(),
            id,
            userId
        );
    }

    @Transactional
    public int updatePurchasedItem(Integer userId, Integer id, Boolean isPurchased) {
    return jdbcTemplate.update(UPDATE_PURCHASED_STATUS,  isPurchased, id, userId);
    }

    @Transactional
    public Integer delete(Integer userId, Integer id) {
        return jdbcTemplate.update(DELETE_ITEM, id, userId);
    }

    public List<ShoppingListItem> findPurchasedItem(Integer userId){
        return jdbcTemplate.query(SELECT_PURCHASED_ITEMS, rowMapper, userId);
    }

    @Transactional
    public Integer deletePurchasedItem(Integer userId) {
        return jdbcTemplate.update(DELETE_PURCHASED_ITEMS, userId);
    }
}
