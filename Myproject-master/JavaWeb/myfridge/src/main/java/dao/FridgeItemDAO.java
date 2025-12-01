package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.Ingredient;
import util.DBUtil;

public class FridgeItemDAO {
	private static final String SELECT_BY_USER_ID = 
			"SELECT * FROM FRIDGE_ITEMS WHERE USER_ID=?";
	private static final String INSERT_ITEM = 
			"INSERT INTO FRIDGE_ITEMS(ID,USER_ID,INGREDIENT_ID,QUANTITY,UNIT,PURCHASED_DATE,EXPIRED_DATE) VALUES(?,?,?,?,?,?)";
	private static final String DELETE_ITEM= 
			"DELETE * FROM FRIDGE_ITEMS WHERE ID=?";

}
