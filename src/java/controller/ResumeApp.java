package controller;

import hibernate.HiberUtil;
import hibernate.User;
import hibernate.UserPrivilege;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "ResumeApp", urlPatterns = {"/ResumeApp"})
public class ResumeApp extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            User sesUsr = (User) request.getSession().getAttribute("usr");
            if (sesUsr == null) {
                response.getWriter().write("err");
            } else {
                User usr = (User) s.load(User.class, sesUsr.getId());
                UserPrivilege up = (UserPrivilege) s.load(UserPrivilege.class, 1);
                if (usr.getUserType().getUserPrivileges().contains(up)) {
                    ServletContext sc = getServletContext();
                    sc.setAttribute("remainingTime", 0l);
                    response.getWriter().write("ok");
                } else {
                    response.getWriter().write("err");
                }
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }
        s.close();
    }

}
