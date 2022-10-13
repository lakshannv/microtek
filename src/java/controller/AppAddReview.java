package controller;

import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductReview;
import hibernate.ProductReviewId;
import hibernate.Stock;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AppAddReview", urlPatterns = {"/AppAddReview"})
public class AppAddReview extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int stockID = Integer.parseInt(request.getParameter("stockID"));
            int custID = Integer.parseInt(request.getParameter("custID"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String desc = request.getParameter("desc");

            if (rating == 0) {
                response.getWriter().write("rat");
            } else if (desc.isEmpty()) {
                response.getWriter().write("emp");
            } else {
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();

                Customer c = (Customer) s.load(Customer.class, custID);
                Stock stk = (Stock) s.load(Stock.class, stockID);
                Product p = stk.getProduct();

                ProductReview pr = new ProductReview(new ProductReviewId(p.getId(), c.getId()), c, p, rating, desc, new Date());
                s.save(pr);

                s.beginTransaction().commit();
                s.close();
                response.getWriter().write("ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
    }
}
