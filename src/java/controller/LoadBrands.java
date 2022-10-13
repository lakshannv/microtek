package controller;

import hibernate.Brand;
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

@WebServlet(name = "LoadBrands", urlPatterns = {"/LoadBrands"})
public class LoadBrands extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria c = s.createCriteria(Brand.class);
        c.addOrder(Order.asc("id"));
        
        request.setAttribute("brandList", (List<Brand>) c.list());
        request.getRequestDispatcher("get_brands.jsp").forward(request, response);
        s.close();
    }

}
