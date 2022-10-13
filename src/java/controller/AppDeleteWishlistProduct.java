package controller;

import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Wishlist;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AppDeleteWishlistProduct", urlPatterns = {"/AppDeleteWishlistProduct"})
public class AppDeleteWishlistProduct extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int wishID = Integer.parseInt(request.getParameter("wishID"));
        int custID = Integer.parseInt(request.getParameter("custID"));

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Customer c = (Customer) s.load(Customer.class, custID);

        Wishlist w = (Wishlist) s.load(Wishlist.class, wishID);
        if (w != null) {
            c = (Customer) s.load(Customer.class, c.getId());
            if (w.getCustomer().getId() == c.getId()) {
                s.delete(w);
            }
        }

        s.beginTransaction().commit();
        s.close();
    }

}
