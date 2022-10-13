package controller;

import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Stock;
import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet(name = "DeleteCartProduct", urlPatterns = {"/DeleteCartProduct"})
public class DeleteCartProduct extends HttpServlet {
    
    DecimalFormat df = new DecimalFormat("#,##0.##");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        int stockID = Integer.parseInt(request.getParameter("stockID"));
        Customer c = (Customer) request.getSession().getAttribute("cust");
        
        if (c == null) {
            double tot = 0;
            Cart del = null;
            LinkedList<Cart> cartItemList = (LinkedList<Cart>) request.getSession().getAttribute("cartItemList");
            for (Cart crt : cartItemList) {
                if (crt.getStock().getId() == stockID) {
                    del = crt;
                } else {
                    tot += crt.getQty() * crt.getStock().getSellingPrice() * (100 - crt.getStock().getDiscount()) / 100;
                }
            }
            cartItemList.remove(del);
            response.getWriter().write("Rs. " + df.format(tot));
        } else {
            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();
            
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
            response.getWriter().write("Rs. " + df.format(tot));
            s.beginTransaction().commit();
            s.close();
            
        }
    }
    
}
