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
			+ "id, userId, title, description, imageUrl, cookingTime, "
			+ "difficulty, step, isPublic "
			+ "From Recipe "
			+ "Where isPublic = True "
			+ "Order By id ";
	
	private static final String SELECT_BY_ID = "Select "
			+ "id, userId, title, description,imageUrl, cookingTime,"
			+ "difficulty, step, isPublic "
			+ "From Recipe "
			+ "Where id =? ";
	
	private static final String SELECT_INGREDIENTS_BY_RECIPE_ID = 
	        "SELECT ri.id, ri.recipeId, ri.ingredientId, ri.amount, ri.unit, " +
	        "i.ingredientName, i.category " +
	        "FROM RecipeIngredient ri " +
	        "JOIN Ingredient i ON ri.ingredientId = i.id " +
	        "WHERE ri.recipeId = ?";
	
	 private static final String SEARCH_BY_KEYWORD = "Select "
			 + "id, userId, title, description,imageUrl, cookingTime, "
			 + "difficulty, step, isPublic "
			 + "From Recipe "
			 + "Where isPublic = True "
			 + "And (title like ? or description like ?) "
			 + "Order By id";
	 //刪除食譜
	 private static final String DELETE = "DELETE FROM Recipe WHERE id = ?"; 
	 //刪除食譜的所有食材關聯
	 private static final String DELETE_INGREDIENTS = "DELETE FROM RecipeIngredient WHERE recipeId = ?";
	 
	 private Recipe buildRecipe(ResultSet rs) throws SQLException {
		 Recipe recipe = new Recipe();
		 recipe.setId(rs.getInt("id"));	
		 recipe.setUserId(rs.getInt("userId"));	 	  
		 recipe.setTitle(rs.getString("title"));	 
		 recipe.setDescription(rs.getString("description"));	 
		 recipe.setImageUrl(rs.getString("imageUrl"));	 
		 recipe.setCookingTime(rs.getInt("cookingTime"));	 
		 recipe.setDifficulty(rs.getInt("difficulty"));	 
		 recipe.setStep(rs.getString("step"));	 
		 recipe.setIsPublic(rs.getBoolean("isPublic"));	
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
							rs.getInt("recipeId"),
							rs.getInt("ingredientId"),
							rs.getDouble("amount"),
							rs.getString("unit"),
							rs.getString("ingredientName"),
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
				//假設我要做番茄炒蛋(recipeId = 1)
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
						+ "id, userId, title, description, imageUrl, cookingTime, "
						+ "difficulty, step, isPublic "
						+ "From Recipe "
						+ "Where isPublic = True ");
				List<Object> params = new LinkedList();
				if(difficulty != null) {
					sql.append(" And difficulty = ? ");
					params.add(difficulty);
					
				}
				if(minCookingTime != null) {
					sql.append(" AND cookingTime >= ?");
					params.add(minCookingTime);
					
				}
				if(maxCookingTime != null) {
					sql.append(" AND cookingTime <= ?");
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
		
		public Integer delete(Integer recipeId) {
			Connection conn = null;
			PreparedStatement psmtIngredients = null;
			PreparedStatement psmtRecipe = null;
			
			try {
				conn = DBUtil.getConnection();
				//關閉自動提交，使用交易
				conn.setAutoCommit(false);
				
				//步驟1：先刪除 RecipeIngredient 中的關聯資料
				psmtIngredients = conn.prepareStatement(DELETE_INGREDIENTS);
				psmtIngredients.setInt(1, recipeId);
				psmtIngredients.executeUpdate();
				
				//步驟2：再刪除 Recipe
				psmtRecipe = conn.prepareStatement(DELETE);
				psmtRecipe.setInt(1, recipeId);
				int rows = psmtRecipe.executeUpdate();
				
				//提交交易
				conn.commit();
				
				return rows;
				
			} catch(SQLException se) {
				//發生錯誤時回滾
				try {
					if (conn != null) {
						conn.rollback();
					}
				} catch (SQLException rollbackEx) {
					rollbackEx.printStackTrace();
				}
				se.printStackTrace();
				throw new RuntimeException("刪除食譜失敗: " + se.getMessage());
			} finally {
				//關閉資源
				try {
					if (psmtIngredients != null) psmtIngredients.close();
					if (psmtRecipe != null) psmtRecipe.close();
					if (conn != null) {
						conn.setAutoCommit(true);  // 恢復自動提交
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
}
