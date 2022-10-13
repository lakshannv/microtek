package controller;

import hibernate.Category;
import hibernate.HiberUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@WebServlet(name = "UpdateCat", urlPatterns = {"/UpdateCat"})
public class UpdateCat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int catID = Integer.parseInt(request.getParameter("catID"));
            String catName = request.getParameter("catName");

            if (Validation.isValidName(catName)) {
                catName = Validation.getValidatedName(catName);
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria cr = s.createCriteria(Category.class);
                List<Category> catList = (List<Category>) cr.list();

                Category existingCat = null;
                for (Category cat : catList) {
                    if (cat.getId() == catID) {
                        existingCat = cat;
                        break;
                    }
                }

                if (existingCat == null) {
                    response.getWriter().write("err");
                } else {
                    byte isActive;
                    if (request.getParameter("isActive").equals("true")) {
                        isActive = 1;
                    } else {
                        isActive = 0;
                    }

                    boolean exists = false;
                    for (Category cat : catList) {
                        if (cat.getName().equalsIgnoreCase(catName) && cat.getId() != catID) {
                            exists = true;
                            break;
                        }
                    }

                    if (exists && (existingCat.getActive() == isActive)) {
                        response.getWriter().write("dup");
                    } else {
                        existingCat.setName(catName);;
                        existingCat.setActive(isActive);
                        s.update(existingCat);
                        response.getWriter().write("ok");
                    }
                }

                Transaction t = s.beginTransaction();
                t.commit();
                s.close();
            } else {
                response.getWriter().write("inv");
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }
    }
}
