package controller;

import hibernate.HiberUtil;
import hibernate.User;
import java.io.IOException;
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

@WebServlet(name = "AdminLogin", urlPatterns = {"/AdminLogin"})
public class AdminLogin extends HttpServlet {

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
                    Criteria userCR = s.createCriteria(User.class);
                    userCR.add(Restrictions.eq("username", un));
                    userCR.add(Restrictions.eq("active", (byte) 1));
                    if (userCR.list().size() == 0) {
                        response.getWriter().write("un");
                    } else {
                        userCR.add(Restrictions.eq("password", pw));
                        User usr = (User) userCR.uniqueResult();
                        if (usr == null) {
                            response.getWriter().write("pw");
                        } else {
                            if (usr.getPassword().equals(pw)) {
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

                                s.beginTransaction().commit();
                                request.getSession().setAttribute("usr", usr);
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
