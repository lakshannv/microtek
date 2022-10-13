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

@WebServlet(name = "AdminAppSuspendApp", urlPatterns = {"/AdminAppSuspendApp"})
public class AdminAppSuspendApp extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            long multiplier = Long.parseLong(request.getParameter("multiplier"));
            ServletContext sc = getServletContext();
            sc.setAttribute("multiplier", multiplier);
            if (multiplier == 0) {
                sc.setAttribute("remainingTime", -99l);
            } else {
                long time = Long.parseLong(request.getParameter("time"));
                sc.setAttribute("remainingTime", time * multiplier);
                sc.setAttribute("initSusTime", time);
                if (sc.getAttribute("suspendThread") == null) {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            while (((long) sc.getAttribute("remainingTime")) > 0) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                }
                                sc.setAttribute("remainingTime", ((long) sc.getAttribute("remainingTime")) - 1);
                            }
                            sc.setAttribute("suspendThread", null);
                        }
                    };
                    t.start();
                    sc.setAttribute("suspendThread", t);
                }
            }
            response.getWriter().write("ok");
        } catch (Exception e) {
            response.getWriter().write("err");
        }
        s.close();
    }

}
