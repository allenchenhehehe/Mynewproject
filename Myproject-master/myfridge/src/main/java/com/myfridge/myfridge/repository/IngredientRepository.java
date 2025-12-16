package com.myfridge.myfridge.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.myfridge.myfridge.entity.Ingredient;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class IngredientRepository{
    
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL = "SELECT * FROM INGREDIENT";
	
	private static final String SELECT_BY_ID = "SELECT * FROM INGREDIENT WHERE ID=?";
	
	private static final String SELECT_BY_NAME = "SELECT * FROM INGREDIENT"
			+ " WHERE INGREDIENTNAME=?";
	
	private static final String INSERT_INGREDIENT = "INSERT INTO INGREDIENT"
			+ " (INGREDIENTNAME,CATEGORY,SHELFLIFEDAYS) VALUES (?,?,?)";

    private final RowMapper<Ingredient> rowMapper = (rs, rowNum) -> {
		Ingredient ingredient = new Ingredient();
		ingredient.setId(rs.getInt("id"));		 	  
		ingredient.setIngredientName(rs.getString("ingredientName"));	 
		ingredient.setCategory(rs.getString("category"));	 
		ingredient.setShelfLifeDays(rs.getInt("shelfLifeDays"));	 
		return ingredient;
	 };

    public List<Ingredient> findAll(){
        return jdbcTemplate.query(SELECT_ALL, rowMapper);
    }

    //直接用queryofObject會爆炸，原本在javaweb也會判斷if(rs.next())，有就回傳，沒有回傳null
    public Ingredient findById(Integer id){
        List<Ingredient> rs = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
        return rs.isEmpty() ? null:rs.get(0);
        }

    public Ingredient findByName(String name){
        List<Ingredient> rs = jdbcTemplate.query(SELECT_BY_NAME, rowMapper, name);
        return rs.isEmpty() ? null:rs.get(0);
    }

    public Ingredient insert(Ingredient ingredient){

        //KeyHolder：用來存放自動生成的主鍵（ID）
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //jdbcTemplate.update() 執行 INSERT
        jdbcTemplate.update(connection -> {
            //建立 PreparedStatement，指定要回傳生成的 Key
            PreparedStatement ps = connection.prepareStatement(INSERT_INGREDIENT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ingredient.getIngredientName());
            ps.setString(2, ingredient.getCategory());
            ps.setInt(3, ingredient.getShelfLifeDays());

            //回傳 PreparedStatement 給 Spring 執行
            return ps;
        }, keyHolder);//keyHolder 會接收生成的 ID

        //從 keyHolder 取出 ID，設定回物件
        ingredient.setId(keyHolder.getKey().intValue());
        //回傳帶有 ID 的物件
        return ingredient;

    }

    public Integer findOrCreate(String ingredientName, String category) {

        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            throw new IllegalArgumentException("食材名稱不可為空");
        }
        
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
        newIngredient.setShelfLifeDays(7);  // 簡單的預設值就好
        
        Ingredient inserted = insert(newIngredient);
        return inserted.getId();
        
    } 
}   