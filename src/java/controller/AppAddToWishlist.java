package controller;

import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.Wishlist;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppAddToWishlist", urlPatterns = {"/AppAddToWishlist"})
public class AppAddToWishlist extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Session s = HiberUtil.getSessionFactory().openSession();

        int prodID = Integer.parseInt(request.getParameter("prodID"));
        int custID = Integer.parseInt(request.getParameter("custID"));

        Customer c = (Customer) s.load(Customer.class, custID);
        Product p = (Product) s.load(Product.class, prodID);

        Criteria cr = s.createCriteria(Wishlist.class);
        cr.add(Restrictions.eq("customer", c));
        cr.add(Restrictions.eq("product", p));

        if (cr.list().isEmpty()) {
            Wishlist w = new Wishlist(c, p);
            s.save(w);
            response.getWriter().write("+");
        } else {
            Wishlist w = (Wishlist) cr.uniqueResult();
            s.delete(w);
            response.getWriter().write("-");

        }
        s.beginTransaction().commit();
        s.close();
    }

}
