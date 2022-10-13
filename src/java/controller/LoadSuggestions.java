package controller;

import com.google.gson.Gson;
import hibernate.Brand;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;

@WebServlet(name = "LoadSuggestions", urlPatterns = {"/LoadSuggestions"})
public class LoadSuggestions extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session s = HiberUtil.getSessionFactory().openSession();
        TreeSet<String> suggestions = new TreeSet();
        Criteria catCR = s.createCriteria(Category.class);
        for (Category c : (List<Category>) catCR.list()) {
            suggestions.add(c.getName());
        }
        Criteria brandCR = s.createCriteria(Brand.class);
        for (Brand b : (List<Brand>) brandCR.list()) {
            suggestions.add(b.getName());
        }
        Criteria prodCR = s.createCriteria(Product.class);
        for (Product p : (List<Product>) prodCR.list()) {
            suggestions.add(p.getName());
            suggestions.add(p.getBrand().getName() + " " + p.getName());
        }
        response.getWriter().write(new Gson().toJson(suggestions));
    }

}
