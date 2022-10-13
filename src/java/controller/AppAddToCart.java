package controller;

import com.google.gson.Gson;
import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Stock;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppAddToCart", urlPatterns = {"/AppAddToCart"})
public class AppAddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        
        HashMap<String, String> responseData = new HashMap();

        int stockID = Integer.parseInt(request.getParameter("stockID"));
        int qty = Integer.parseInt(request.getParameter("qty"));
        int custID = Integer.parseInt(request.getParameter("custID"));

        Customer c = (Customer) s.load(Customer.class, custID);
        Stock stk = (Stock) s.load(Stock.class, stockID);
        Criteria cr = s.createCriteria(Cart.class);
        cr.add(Restrictions.eq("customer", c));
        cr.add(Restrictions.eq("stock", stk));

        if (cr.list().isEmpty()) {
            //new item

            if (stk.getQty() >= qty) {
                Cart ct = new Cart(c, stk, qty);
                s.save(ct);
                responseData.put("response", "ok");
                responseData.put("msg", stk.getProduct().getName() + " has been addded to Cart");
            } else {
                responseData.put("response", "no");
                responseData.put("msg", "Tried to add more than available to Cart.");
            }

        } else {
            //already added

            Cart ct = (Cart) cr.uniqueResult();
            int newQty = ct.getQty() + qty;

            if (stk.getQty() >= newQty) {
                ct.setQty(newQty);
                s.update(ct);
                responseData.put("response", "ok");
                responseData.put("msg", "Added " + qty + " more. Total of " + newQty + " " + stk.getProduct().getName() + " " + stk.getProduct().getCategory().getName() + " are in the Cart");
            } else {
                responseData.put("response", "no");
                responseData.put("msg", "Can't add " + qty + " more because you already have " + ct.getQty() + " " + stk.getProduct().getName() + " " + stk.getProduct().getCategory().getName() + " in the cart. Only " + stk.getQty() + " available.");
            }

        }
        s.beginTransaction().commit();
        responseData.put("cartSize", String.valueOf(c.getCarts().size()));
        response.getWriter().write(new Gson().toJson(responseData));
        s.close();
    }

}
