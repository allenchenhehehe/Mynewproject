package com.myfridge.myfridge.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.myfridge.myfridge.entity.FridgeItem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FridgeItemRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BY_USER_ID = 
			"SELECT f.id, f.userId, f.ingredientId, f.amount, f.unit, " +
	        "f.purchasedDate, f.expiredDate, " +
	        "i.ingredientName, i.category " +
	        "FROM FridgeItem f " +
	        "JOIN Ingredient i ON f.ingredientId = i.id " +
	        "WHERE f.userId = ?";

    private static final String SELECT_BY_ID = 
			"SELECT f.id, f.userId, f.ingredientId, f.amount, f.unit, " +
	        "f.purchasedDate, f.expiredDate, " +
	        "i.ingredientName, i.category " +
	        "FROM FridgeItem f " +
	        "JOIN Ingredient i ON f.ingredientId = i.id " +
	        "WHERE f.id = ?";

    private static final String INSERT_ITEM = 
			"INSERT INTO FRIDGEITEM(USERID,INGREDIENTID,AMOUNT,UNIT,PURCHASEDDATE,EXPIREDDATE)"
			+ " VALUES(?,?,?,?,?,?)";

    private static final String UPDATE_ITEM= 
			"UPDATE FRIDGEITEM SET "
			+ "AMOUNT=?, UNIT=?, PURCHASEDDATE=?, EXPIREDDATE=? WHERE ID=? AND userId = ?";

    private static final String DELETE_ITEM= 
			"DELETE  FROM FRIDGEITEM WHERE ID=? AND userId = ?";

    
    private final RowMapper<FridgeItem> rowMapper = (rs, rowNum) -> {

		FridgeItem fridgeItem = new FridgeItem();
		fridgeItem.setId(rs.getInt("id"));		
        fridgeItem.setUserId(rs.getInt("userId")); 	  
        fridgeItem.setIngredientId(rs.getInt("ingredientId")); 	  
        fridgeItem.setAmount(rs.getDouble("amount")); 	  
        fridgeItem.setUnit(rs.getString("unit")); 	  

        Date purchasedDate = rs.getDate("purchasedDate");
        fridgeItem.setPurchasedDate(purchasedDate != null ? purchasedDate.toLocalDate() : null);
        Date expiredDate = rs.getDate("expiredDate");
        fridgeItem.setExpiredDate(expiredDate != null ? expiredDate.toLocalDate() : null);
 	  
		fridgeItem.setIngredientName(rs.getString("ingredientName"));	 
		fridgeItem.setCategory(rs.getString("category"));	 
	 
		return fridgeItem;
	};

    public List<FridgeItem> findByUserId(Integer userId){
        return jdbcTemplate.query(SELECT_BY_USER_ID, rowMapper, userId);
    }

    public FridgeItem findById(Integer id){
        List<FridgeItem> item = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
        return item.isEmpty() ? null : item.get(0);
    }

    public FridgeItem insert(FridgeItem item){

        //KeyHolder：用來存放自動生成的主鍵（ID）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //jdbcTemplate.update() 執行 INSERT
        jdbcTemplate.update(connection -> {

            //建立 PreparedStatement，指定要回傳生成的 Key
            PreparedStatement ps = connection.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, item.getUserId());
            ps.setInt(2, item.getIngredientId());
            ps.setDouble(3, item.getAmount());
            ps.setString(4, item.getUnit());

             // LocalDate → java.sql.Date
            ps.setDate(5, Date.valueOf(item.getPurchasedDate()));
            // 處理可能為 null 的過期日期
            if (item.getExpiredDate() != null) {
                ps.setDate(6, java.sql.Date.valueOf(item.getExpiredDate()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            //回傳 PreparedStatement 給 Spring 執行
            return ps;
        }, keyHolder);//keyHolder 會接收生成的 ID

        //從 keyHolder 取出 ID，設定回物件
        item.setId(keyHolder.getKey().intValue());
        //回傳帶有 ID 的物件
        return item;

    }

    @Transactional
    public int update(FridgeItem item){
        
       return jdbcTemplate.update(connection -> {

            //建立 PreparedStatement，指定要回傳生成的 Key
            PreparedStatement ps = connection.prepareStatement(UPDATE_ITEM);
            ps.setDouble(1, item.getAmount());
            ps.setString(2, item.getUnit());

             // LocalDate → java.sql.Date
            ps.setDate(3, Date.valueOf(item.getPurchasedDate()));
            // 處理可能為 null 的過期日期
            if (item.getExpiredDate() != null) {
                ps.setDate(4, java.sql.Date.valueOf(item.getExpiredDate()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setInt(5, item.getId());
            ps.setInt(6, item.getUserId());

            //回傳 PreparedStatement 給 Spring 執行
            return ps;
        });
    }

    public boolean delete(Integer userId, Integer id){
        int rows = jdbcTemplate.update(DELETE_ITEM, id, userId);
        return rows>0;
    }
    
}
