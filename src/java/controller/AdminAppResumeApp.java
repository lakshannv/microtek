package controller;

import hibernate.HiberUtil;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AdminAppResumeApp", urlPatterns = {"/AdminAppResumeApp"})
public class AdminAppResumeApp extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        ServletContext sc = getServletContext();
        sc.setAttribute("remainingTime", 0l);
        response.getWriter().write("ok");
        s.close();
    }

}
