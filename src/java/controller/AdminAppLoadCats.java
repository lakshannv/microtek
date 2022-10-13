package controller;

import com.google.gson.Gson;
import hibernate.Category;
import hibernate.HiberUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;


@WebServlet(name = "AdminAppLoadCats", urlPatterns = {"/AdminAppLoadCats"})
public class AdminAppLoadCats extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        
        HashMap<String, Object> responseData = new HashMap();
        LinkedList<HashMap<String, Object>> catList = new LinkedList();
        Gson g = new Gson();
        
        Criteria c = s.createCriteria(Category.class);
        c.addOrder(Order.asc("name"));
        
        List<Category> categoryList = c.list();
        for (Category category : categoryList) {
            HashMap<String, Object> cat = new HashMap();
            cat.put("id", String.valueOf(category.getId()));
            cat.put("name", category.getName());
            cat.put("isActive", category.getActive() == (byte) 1);
            catList.add(cat);
        }
        
        responseData.put("catList", catList);
        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
