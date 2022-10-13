package controller;

import hibernate.Category;
import hibernate.HiberUtil;
import java.io.IOException;
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

/**
 *
 * @author laksh
 */
@WebServlet(name = "LoadCats", urlPatterns = {"/LoadCats"})
public class LoadCats extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria c = s.createCriteria(Category.class);
        c.addOrder(Order.asc("id"));
        
        request.setAttribute("catList", (List<Category>) c.list());
        request.getRequestDispatcher("get_cats.jsp").forward(request, response);
        s.close();
    }

}
