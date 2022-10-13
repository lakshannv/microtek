package controller;

import com.google.gson.Gson;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Spec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadSpecs", urlPatterns = {"/LoadSpecs"})
public class LoadSpecs extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Criteria cr = s.createCriteria(Category.class);

        String catName = request.getParameter("catName");

        if (catName == null) {
            int catID = Integer.parseInt(request.getParameter("catID"));
            cr.add(Restrictions.eq("id", catID));
        } else {
            cr.add(Restrictions.eq("name", catName));
            request.setAttribute("toPTable", true);
        }
        Category c = (Category) cr.uniqueResult();

        Criteria crSpecs = s.createCriteria(Spec.class);
        crSpecs.add(Restrictions.eq("category", c));
        crSpecs.addOrder(Order.asc("id"));

        request.setAttribute("specList", (List<Spec>) crSpecs.list());
        request.getRequestDispatcher("get_specs.jsp").forward(request, response);
        s.close();
    }

}
