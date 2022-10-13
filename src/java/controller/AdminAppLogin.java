package controller;

import com.google.gson.Gson;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.User;
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

@WebServlet(name = "AdminAppLogin", urlPatterns = {"/AdminAppLogin"})
public class AdminAppLogin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String un = request.getParameter("un");
        String pw = request.getParameter("pw");

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        if (un.isEmpty()) {
            response.getWriter().write("un_emp");
        } else {
            if (pw.isEmpty()) {
                response.getWriter().write("pw_emp");
            } else {

                Criteria custCR = s.createCriteria(User.class);
                custCR.add(Restrictions.eq("username", un));
                if (custCR.list().size() == 0) {
                    response.getWriter().write("un");
                } else {
                    custCR.add(Restrictions.eq("password", pw));
                    User u = (User) custCR.uniqueResult();
                    if (u == null) {
                        response.getWriter().write("pw");
                    } else {
                        if (u.getPassword().equals(pw)) {
                            if (u.getActive() == (byte) 1) {
                                response.getWriter().write("ok");
                            } else {
                                response.getWriter().write("blocked");
                            }
                        } else {
                            response.getWriter().write("pw");
                        }
                    }
                }

            }
        }

        s.close();
    }

}
