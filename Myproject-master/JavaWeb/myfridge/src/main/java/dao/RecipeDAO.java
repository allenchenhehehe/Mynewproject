package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.Ingredient;
import model.Recipe;
import model.RecipeIngredient;
import util.DBUtil;

public class RecipeDAO {
	private static final String SELECT_ALL = "Select "
			+ "id, user_id, title, description,image_url, cooking_time, "
			+ "difficulty, step, is_public "
			+ "From recipes "
			+ "Where is_public = True "
			+ "Order By id ";
	
	private static final String SELECT_BY_ID = "Select "
			+ "id, user_id, title, description,image_url, cooking_time,"
			+ "difficulty, step, is_public "
			+ "From recipes "
			+ "Where id =? ";
	
	private static final String SELECT_INGREDIENTS_BY_RECIPE_ID = 
	        "SELECT ri.id, ri.recipe_id, ri.ingredient_id, ri.amount, ri.unit, " +
	        "i.ingredient_name, i.category " +
	        "FROM recipe_ingredients ri " +
	        "JOIN ingredients i ON ri.ingredient_id = i.id " +
	        "WHERE ri.recipe_id = ?";
	
	 private static final String SEARCH_BY_KEYWORD = "Select "
			 + "id, user_id, title, description,image_url, cooking_time, "
			 + "difficulty, step, is_public "
			 + "From recipes "
			 + "Where is_public = True "
			 + "And (title like ? or description like ?) "
			 + "Order By id";
	 
	 private Recipe buildRecipe(ResultSet rs) throws SQLException {
		 Recipe recipe = new Recipe();
		 recipe.setId(rs.getInt("id"));	
		 recipe.setUserId(rs.getInt("user_id"));	 	  
		 recipe.setTitle(rs.getString("title"));	 
		 recipe.setDescription(rs.getString("description"));	 
		 recipe.setImageURL(rs.getString("image_url"));	 
		 recipe.setCookingTime(rs.getInt("cooking_time"));	 
		 recipe.setDifficulty(rs.getInt("difficulty"));	 
		 recipe.setStep(rs.getString("step"));	 
		 recipe.setIsPublic(rs.getBoolean("is_public"));	
		 return recipe;
	 }
	 
	 private List<RecipeIngredient> getIngredientsByRecipeId(Integer recipeId, Connection conn)
			 throws SQLException{
		 	PreparedStatement psmt = null;
	        ResultSet rs = null;
	        List<RecipeIngredient> ingredients = new LinkedList<>();
	        try {
				psmt = conn.prepareStatement(SELECT_INGREDIENTS_BY_RECIPE_ID);
				psmt.setInt(1, recipeId);
				rs = psmt.executeQuery();
				while(rs.next()) {
					RecipeIngredient ingredient = new RecipeIngredient(
							rs.getInt("id"),
							rs.getInt("recipe_id"),
							rs.getInt("ingredient_id"),
							rs.getDouble("amount"),
							rs.getString("unit"),
							rs.getString("ingredient_name"),
							rs.getString("category")				
					);
					ingredients.add(ingredient);
				}   	
	        }catch(SQLException se) {
				throw new RuntimeException("查詢食譜失敗" + se.getMessage());
			}finally {
				DBUtil.close(null, psmt, rs);
			}
	        return ingredients;
	 }
	 
	 //第一次進到Myfridge掛載所有recipe
	 public List<Recipe> findAll(){
			Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			List<Recipe> list = new LinkedList<>();
			try {
				conn = DBUtil.getConnection();
				psmt = conn.prepareStatement(SELECT_ALL);
				rs = psmt.executeQuery();
				while(rs.next()) {
					Recipe recipe = buildRecipe(rs);
					list.add(recipe);
				}
				for (Recipe recipe : list) {
			        List<RecipeIngredient> ingredients = getIngredientsByRecipeId(recipe.getId(), conn);
			        recipe.setIngredients(ingredients);
				}
			}catch(SQLException se) {
				throw new RuntimeException("查詢食譜失敗" + se.getMessage());
			}finally {
				DBUtil.close(conn, psmt, rs);
			}
			return list;		
		}
	 
	//點我想做進到recipe detial
		public Recipe findById(Integer id){
			Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			try {
				conn = DBUtil.getConnection();
				//假設我要做番茄炒蛋(recipe_id = 1)
				psmt = conn.prepareStatement(SELECT_BY_ID);
				psmt.setInt(1, id);
				rs = psmt.executeQuery();
				Recipe recipe = null;
				if(rs.next()) {
					//此食的recipe不包含ingredient)
					recipe = buildRecipe(rs);
				}
				if(recipe == null) {
					return null;
				}
				List<RecipeIngredient> ingredients = getIngredientsByRecipeId(id, conn);
	            recipe.setIngredients(ingredients);
	            return recipe;
			}catch(SQLException se) {
				throw new RuntimeException("" + se.getMessage());
			}finally {
				DBUtil.close(conn, psmt, rs);
			}
		}
		
		//用右邊搜尋框搜尋食譜
		public List<Recipe> findByKeyword(String keyword){
			Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			List<Recipe> list = new LinkedList<>();
			try {
				conn = DBUtil.getConnection();
				psmt = conn.prepareStatement(SEARCH_BY_KEYWORD);
				/*模糊比對，%keyword代表keyword前面可以放任意字串
				 keyword%代表keyword後可以放任意字串，%keyword%代表前後都可以有任意字*/
				String searchPattern = "%" + keyword + "%";
				psmt.setString(1, searchPattern);
				psmt.setString(2, searchPattern);
				rs = psmt.executeQuery();
				while(rs.next()) {
					Recipe recipe = buildRecipe(rs);
					list.add(recipe);
				}
				for (Recipe recipe : list) {
	                List<RecipeIngredient> ingredients = getIngredientsByRecipeId(recipe.getId(), conn);
	                recipe.setIngredients(ingredients);
	            }
			}catch(SQLException se) {
				throw new RuntimeException("" + se.getMessage());
			}finally {
				DBUtil.close(conn, psmt, rs);
			}
			return list;
		}
		
		public List<Recipe>filter(Integer difficulty, Integer minCookingTime, Integer maxCookingTime){
			Connection conn = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			List<Recipe> list = new LinkedList<>();
			try {
				conn = DBUtil.getConnection();
				StringBuilder sql = new StringBuilder( "Select "
						+ "id, user_id, title, description, image_url, cooking_time, "
						+ "difficulty, step, is_public "
						+ "From recipes "
						+ "Where is_public = True ");
				List<Object> params = new LinkedList();
				if(difficulty != null) {
					sql.append(" And difficulty = ? ");
					params.add(difficulty);
					
				}
				if(minCookingTime != null) {
					sql.append(" AND cooking_time >= ?");
					params.add(minCookingTime);
					
				}
				if(maxCookingTime != null) {
					sql.append(" AND cooking_time <= ?");
					params.add(maxCookingTime);
					
				}		
				sql.append(" Order By id");
				psmt = conn.prepareStatement(sql.toString());
				for(int i = 0; i < params.size(); i++) {
			            psmt.setObject(i + 1, params.get(i));	        
				}
				rs = psmt.executeQuery();
				
				while(rs.next()) {
					Recipe recipe = buildRecipe(rs);
					list.add(recipe);
				}
				for (Recipe recipe : list) {
	                List<RecipeIngredient> ingredients = getIngredientsByRecipeId(recipe.getId(), conn);
	                recipe.setIngredients(ingredients);
	            }
			}catch(SQLException se) {
				throw new RuntimeException("" + se.getMessage());
			}finally {
				DBUtil.close(conn, psmt, rs);
			}
			return list;
		}
		
		
			
}
