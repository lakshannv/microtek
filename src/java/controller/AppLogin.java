package controller;

import com.google.gson.Gson;
import hibernate.Customer;
import hibernate.HiberUtil;
import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppLogin", urlPatterns = {"/AppLogin"})
public class AppLogin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String un = request.getParameter("un");
        String pw = request.getParameter("pw");
        String googleSignIn = request.getParameter("googleSignIn");
        String eml = request.getParameter("eml");

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        if (googleSignIn == null) {
            if (un.isEmpty()) {
                response.getWriter().write("un_emp");
            } else {
                if (pw.isEmpty()) {
                    response.getWriter().write("pw_emp");
                } else {

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
                                response.getWriter().write("ok");
                            } else {
                                response.getWriter().write("pw");
                            }
                        }
                    }

                }
            }
        } else {

            Criteria custCR = s.createCriteria(Customer.class);
            custCR.add(Restrictions.eq("email", eml));
            if (custCR.list().size() == 0) {
                response.getWriter().write("nouser");
            } else {
                Customer cust = (Customer) custCR.uniqueResult();
                LinkedList<String> userData = new LinkedList();
                userData.add(cust.getUsername());
                userData.add(cust.getPassword());
                response.getWriter().write(new Gson().toJson(userData));
            }
        }

        s.close();
    }

}
