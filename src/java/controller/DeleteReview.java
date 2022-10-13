package controller;

import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductReview;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "DeleteReview", urlPatterns = {"/DeleteReview"})
public class DeleteReview extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int pID = Integer.parseInt(request.getParameter("pID"));
            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();

            Customer c = (Customer) request.getSession().getAttribute("cust");
            Product p = (Product) s.load(Product.class, pID);

            Criteria cr = s.createCriteria(ProductReview.class);
            cr.add(Restrictions.eq("customer", c));
            cr.add(Restrictions.eq("product", p));
            ProductReview pr = (ProductReview) cr.uniqueResult();
            s.delete(pr);

            s.beginTransaction().commit();
            s.close();
            response.getWriter().write("ok");
        } catch (Exception e) {
            response.getWriter().write("err");
        }
    }
}
