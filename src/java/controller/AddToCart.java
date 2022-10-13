package controller;

import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Stock;
import java.io.IOException;
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

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        int stockID = Integer.parseInt(request.getParameter("stockID"));
        int qty = Integer.parseInt(request.getParameter("qty"));

        Customer c = (Customer) request.getSession().getAttribute("cust");
        Stock stk = (Stock) s.load(Stock.class, stockID);

        if (c == null) {
            Cookie[] cookieSet = request.getCookies();
            if (cookieSet == null) {
                response.getWriter().write("cook");
            } else {
                if (request.getSession().getAttribute("cartItemList") == null) {
                    if (stk.getQty() >= qty) {
                        LinkedList<Cart> cartItemList = new LinkedList();

                        Cart ct = new Cart(null, stk, qty);
                        cartItemList.add(ct);
                        request.getSession().setAttribute("cartItemList", cartItemList);
                        response.getWriter().write("ok-" + stk.getProduct().getName() + " has been addded to Cart");
                    } else {
                        response.getWriter().write("Tried to add more than available to Cart.");
                    }
                } else {
                    Cart existingCartItem = null;
                    LinkedList<Cart> cartItemList = (LinkedList<Cart>) request.getSession().getAttribute("cartItemList");
                    for (Cart crt : cartItemList) {
                        if (crt.getStock().getId() == stockID) {
                            existingCartItem = crt;
                            break;
                        }
                    }

                    if (existingCartItem == null) {
                        if (stk.getQty() >= qty) {
                            Cart ct = new Cart(null, stk, qty);
                            cartItemList.add(ct);
                            response.getWriter().write("ok-" + stk.getProduct().getName() + " has been addded to Cart");
                        } else {
                            response.getWriter().write("Tried to add more than available to Cart.");
                        }
                    } else {
                        int newQty = existingCartItem.getQty() + qty;
                        if (stk.getQty() >= newQty) {
                            existingCartItem.setQty(newQty);
                            response.getWriter().write("ok-Added " + qty + " more. Total of " + newQty + " " + stk.getProduct().getName() + " " + stk.getProduct().getCategory().getName() + " are in the Cart");
                        } else {
                            response.getWriter().write("Can't add " + qty + " more because you already have " + existingCartItem.getQty() + " " + stk.getProduct().getName() + " " + stk.getProduct().getCategory().getName() + " in the cart.<br>Only " + stk.getQty() + " available in the stocks right now.");
                        }
                    }
                }
            }
        } else {
            Criteria cr = s.createCriteria(Cart.class);
            cr.add(Restrictions.eq("customer", c));
            cr.add(Restrictions.eq("stock", stk));

            if (cr.list().isEmpty()) {
                //new item

                if (stk.getQty() >= qty) {
                    Cart ct = new Cart(c, stk, qty);
                    s.save(ct);
                    response.getWriter().write("ok-" + stk.getProduct().getName() + " has been addded to Cart");
                } else {
                    response.getWriter().write("Tried to add more than available to Cart.");
                }

            } else {
                //already added

                Cart ct = (Cart) cr.uniqueResult();
                int newQty = ct.getQty() + qty;

                if (stk.getQty() >= newQty) {
                    ct.setQty(newQty);
                    s.update(ct);
                    response.getWriter().write("ok-Added " + qty + " more. Total of " + newQty + " " + stk.getProduct().getName() + " " + stk.getProduct().getCategory().getName() + " are in the Cart");
                } else {
                    response.getWriter().write("Can't add " + qty + " more because you already have " + ct.getQty() + " " + stk.getProduct().getName() + " " + stk.getProduct().getCategory().getName() + " in the cart.<br>Only " + stk.getQty() + " available in the stocks right now.");
                }

            }
            s.beginTransaction().commit();
            s.close();
        }
    }

}
