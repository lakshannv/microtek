package controller;

import model.Validation;
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
import org.hibernate.Transaction;

@WebServlet(name = "AddNewCat", urlPatterns = {"/AddNewCat"})
public class AddNewCat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String catName = request.getParameter("catName");
            if (Validation.isValidName(catName)) {
                catName = Validation.getValidatedName(catName);
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria cr = s.createCriteria(Category.class);
                List<Category> catList = (List<Category>) cr.list();
                boolean exists = false;
                for (Category cat : catList) {
                    if (cat.getName().equalsIgnoreCase(catName)) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    response.getWriter().write("dup");
                } else {
                    Category c = new Category();
                    c.setName(catName);
                    c.setActive((byte) 1);
                    s.save(c);
                    response.getWriter().write("ok");
                }
                Transaction t = s.beginTransaction();
                t.commit();
                s.close();
            } else {
                response.getWriter().write("inv");
            }
        } catch (Exception e) {
            response.getWriter().write("err");
            e.printStackTrace();
        }
    }
}
