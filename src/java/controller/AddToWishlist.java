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

@WebServlet(name = "AddToWishlist", urlPatterns = {"/AddToWishlist"})
public class AddToWishlist extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Session s = HiberUtil.getSessionFactory().openSession();

        try {
            int prodID = Integer.parseInt(request.getParameter("prodID"));

            Customer c = (Customer) request.getSession().getAttribute("cust");
                Product p = (Product) s.load(Product.class, prodID);

                Criteria cr = s.createCriteria(Wishlist.class);
                cr.add(Restrictions.eq("customer", c));
                cr.add(Restrictions.eq("product", p));

                if (cr.list().isEmpty()) {
                    Wishlist w = new Wishlist(c, p);
                    s.save(w);
                    response.getWriter().write("ok-" + p.getName() + " has been addded to Wishlist");
                } else {
                    Wishlist w = (Wishlist) cr.uniqueResult();
                    s.delete(w);
                    response.getWriter().write("ok-" + p.getName() + " has been removed from Wishlist");

                }
                s.beginTransaction().commit();
        } catch (Exception e) {
            response.getWriter().write("er");
        }
        s.close();
    }

}
