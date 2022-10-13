package controller;

import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
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

@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String un = request.getParameter("un");
        String pw = request.getParameter("pw");
        String rem = request.getParameter("rem");
        Cookie[] cookieSet = request.getCookies();
        if (cookieSet == null) {
            response.getWriter().write("cook");
        } else {
            if (un.isEmpty()) {
                response.getWriter().write("un");
            } else {
                if (pw.isEmpty()) {
                    response.getWriter().write("pw");
                } else {
                    SessionFactory sf = HiberUtil.getSessionFactory();
                    Session s = sf.openSession();
                    Criteria custCR = s.createCriteria(Customer.class);
                    custCR.add(Restrictions.eq("username", un));
                    if (custCR.list().size() == 0) {
                        response.getWriter().write("un");
                    } else {
                        custCR.add(Restrictions.eq("password", pw));
                        Customer cust = (Customer) custCR.uniqueResult();
                        if (cust == null) {
                            response.getWriter().write("pw");
                        } else {
                            if (cust.getPassword().equals(pw)) {
                                for (Cookie cooky : cookieSet) {
                                    if (cooky.getName().equals("JSESSIONID")) {
                                        if (rem.equals("true")) {
                                            cooky.setMaxAge(31536000);
                                        } else {
                                            cooky.setMaxAge(-1);
                                        }
                                        response.addCookie(cooky);
                                    }
                                }

                                Set<Cart> dbCart = cust.getCarts();
                                LinkedList<Cart> cartItemList = (LinkedList<Cart>) request.getSession().getAttribute("cartItemList");
                                Cart existingDBItem = null;
                                if (cartItemList != null) {
                                    for (Cart cartItem : cartItemList) {
                                        for (Cart dbItem : dbCart) {
                                            if (cartItem.getStock().getId() == dbItem.getStock().getId()) {
                                                existingDBItem = dbItem;
                                                break;
                                            }
                                        }
                                        if (existingDBItem == null) {
                                            cartItem.setCustomer(cust);
                                            s.save(cartItem);
                                        } else {
                                            int newQty = existingDBItem.getQty() + cartItem.getQty();
                                            if (existingDBItem.getStock().getQty() >= newQty) {
                                                existingDBItem.setQty(newQty);
                                                s.update(existingDBItem);
                                            } else {
                                                existingDBItem.setQty(existingDBItem.getStock().getQty());
                                                s.update(existingDBItem);
                                            }
                                        }
                                        existingDBItem = null;
                                    }
                                }
                                s.beginTransaction().commit();
                                request.getSession().setAttribute("cust", cust);
                                cartItemList = null;
                                response.getWriter().write("ok");
                            } else {
                                response.getWriter().write("pw");
                            }
                        }
                    }
                    s.close();
                }
            }
        }
    }

}
