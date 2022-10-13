package controller;

import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductReview;
import hibernate.ProductReviewId;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AddReview", urlPatterns = {"/AddReview"})
public class AddReview extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int pID = Integer.parseInt(request.getParameter("pID"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String desc = request.getParameter("desc");
            
            if (rating == 0) {
                response.getWriter().write("rat");  
            } else if (desc.isEmpty()) {
                response.getWriter().write("emp");  
            } else {
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();
                
                Customer c = (Customer) request.getSession().getAttribute("cust");
                Product p = (Product) s.load(Product.class, pID);
                
                ProductReview pr = new ProductReview(new ProductReviewId(pID, c.getId()), c, p, rating, desc, new Date());
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
