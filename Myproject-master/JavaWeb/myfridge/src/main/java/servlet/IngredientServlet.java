package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import model.Ingredient;
import service.IngredientService;


@WebServlet("/api/ingredients/*")
public class IngredientServlet extends HttpServlet {
	
	private IngredientService ingredientservice;
	private Gson gson;
	
	@Override
	public void init() {
		ingredientservice = new IngredientService();
		gson = new Gson();
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String pathInfo = req.getPathInfo();
		if(pathInfo == null || pathInfo == "/") {
			
			List<Ingredient> list = ingredientservice.getAllIngre();
			out.println(gson.toJson(list));
			
		}else {
			String idStr = pathInfo.substring(1);
			try {
				Integer id = Integer.parseInt(idStr);
				Ingredient ingre = ingredientservice.getIngreById(id);
				if(ingre!=null) {
					out.println(gson.toJson(ingre));
				}else {
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					out.println("{\"error\": \"找不到此食材\"}");
				}
			}catch(NumberFormatException nfe){
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"ID 格式錯誤\"}");
			}
		}
		out.flush();	
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
