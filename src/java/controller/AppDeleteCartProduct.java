package controller;

import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppDeleteCartProduct", urlPatterns = {"/AppDeleteCartProduct"})
public class AppDeleteCartProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int stockID = Integer.parseInt(request.getParameter("stockID"));
        int custID = Integer.parseInt(request.getParameter("custID"));

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Customer c = (Customer) s.load(Customer.class, custID);
        Criteria cr = s.createCriteria(Cart.class);
        cr.add(Restrictions.eq("customer", c));

        List<Cart> cartItemList = cr.list();

        double tot = 0;
        for (Cart crt : cartItemList) {
            if (crt.getStock().getId() == stockID) {
                s.delete(crt);
            } else {
                tot += crt.getQty() * crt.getStock().getSellingPrice() * (100 - crt.getStock().getDiscount()) / 100;
            }
        }
        response.getWriter().write(String.valueOf(tot));
        s.beginTransaction().commit();
        s.close();
    }

}
