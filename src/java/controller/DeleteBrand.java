package controller;

import hibernate.Brand;
import hibernate.HiberUtil;
import hibernate.Product;
import java.io.File;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "DeleteBrand", urlPatterns = {"/DeleteBrand"})
public class DeleteBrand extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int brandID = Integer.parseInt(request.getParameter("brandID"));

            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria cr = s.createCriteria(Brand.class);
            cr.add(Restrictions.eq("id", brandID));

            Brand existingBrand = (Brand) cr.uniqueResult();

            Criteria prodCR = s.createCriteria(Product.class);
            prodCR.add(Restrictions.eq("brand", existingBrand));

            if (prodCR.list().isEmpty()) {
                s.delete(existingBrand);
                File f = new File(getServletContext().getRealPath("") + "//assets//img/brands//" + brandID);
                if (f.exists()) {
                    f.delete();
                }
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
