package controller;

import hibernate.HiberUtil;
import hibernate.User;
import hibernate.UserPrivilege;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;

@WebServlet(name = "AdminRedirect", urlPatterns = {"/AdminRedirect"})
public class AdminRedirect extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session s = HiberUtil.getSessionFactory().openSession();
        User u = (User) request.getSession().getAttribute("usr");

        if (u == null) {
            response.sendRedirect("admin_login.jsp");
        } else {
            String url = "admin_login.jsp";
            u = (User) s.load(User.class, u.getId());

            int accessLevel = 10;

            for (Iterator<UserPrivilege> iterator = u.getUserType().getUserPrivileges().iterator(); iterator.hasNext();) {
                UserPrivilege p = iterator.next();
                if (accessLevel > p.getId()) {
                    accessLevel = p.getId();
                    if (p.getName().equals("Dashboard")) {
                        url = "dashboard.jsp";
                    } else if (p.getName().equals("User Management")) {
                        url = "usr_mng.jsp";
                    } else if (p.getName().equals("Product Management")) {
                        url = "prod_mng.jsp";
                    } else if (p.getName().equals("Order Management")) {
                        url = "odr_mng.jsp";
                    } else if (p.getName().equals("Delivery Management")) {
                        url = "del_mng.jsp";
                    } else if (p.getName().equals("Sales & Reports")) {
                        url = "reports.jsp";
                    }
                }
            }
            response.sendRedirect(url);
        }
        s.close();
    }
}
