package service;

import java.util.List;
import dao.IngredientDAO;
import dao.UserDAO;
import model.Ingredient;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
private UserDAO userDAO;
	
	public UserService() {
		this.userDAO = new UserDAO();
	}
	public List<User> getAllUser (){
		return userDAO.findAll();
	}
	public User getUserById (Integer id) {
		return userDAO.findById(id);
	}
	public User getUserByEmail (String email) {
		return userDAO.findByEmail(email);
	}
	public User register (User user) {
		User existing = userDAO.findByEmail(user.getEmail());
		if(existing!=null) {
			throw new RuntimeException("此Email已經註冊!");
		}else{
			String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
			user.setPassword(hashedPassword);
			return userDAO.insert(user);
		}
	}
	public User login (String email, String password) {
		User user = userDAO.findByEmail(email);
		if(user == null || !BCrypt.checkpw(password, user.getPassword())) {
	        throw new RuntimeException("帳號或密碼錯誤!"); 
	    }
		return user;
	}
}
