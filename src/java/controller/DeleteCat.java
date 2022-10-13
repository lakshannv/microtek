package controller;

import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.Spec;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "DeleteCat", urlPatterns = {"/DeleteCat"})
public class DeleteCat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int catID = Integer.parseInt(request.getParameter("catID"));

            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria cr = s.createCriteria(Category.class);
            cr.add(Restrictions.eq("id", catID));

            Category existingCat = (Category) cr.uniqueResult();

            Criteria prodCR = s.createCriteria(Product.class);
            prodCR.add(Restrictions.eq("category", existingCat));

            if (prodCR.list().isEmpty()) {
                Criteria specCR = s.createCriteria(Spec.class);
                specCR.add(Restrictions.eq("category", existingCat));
                for (Spec sp : (List<Spec>) specCR.list()) {
                    s.delete(sp);
                }
                s.delete(existingCat);
                response.getWriter().write("ok");
            } else {
                response.getWriter().write("ex");
            }

            Transaction t = s.beginTransaction();
            t.commit();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
    }
}
