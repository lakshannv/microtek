package controller;

import hibernate.HiberUtil;
import hibernate.User;
import hibernate.UserType;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "UpdateUser", urlPatterns = {"/UpdateUser"})
public class UpdateUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            int uID = Integer.parseInt(request.getParameter("uID"));
            String uname = request.getParameter("uname");
            String pw = request.getParameter("pw");
            int uType = Integer.parseInt(request.getParameter("uType"));
            if (uname.isEmpty()) {
                response.getWriter().write("un");
            } else if (pw.isEmpty()) {
                response.getWriter().write("pw");
            } else {
                User u = (User) s.load(User.class, uID);
                boolean proceed = false;

                Criteria uCR = s.createCriteria(User.class);
                uCR.add(Restrictions.eq("username", uname));
                if (uCR.list().isEmpty()) {
                    proceed = true;
                } else {
                    if (u.getUsername().equals(uname)) {
                        proceed = true;
                    }
                }

                if (proceed) {
                    UserType usrType = (UserType) s.load(UserType.class, uType);
                    u.setUsername(uname);
                    u.setPassword(pw);
                    if (u.getUserType().getId() != 1 && uType != 1) {
                        u.setUserType(usrType);
                    }
                    s.update(u);
                    s.beginTransaction().commit();
                    response.getWriter().write("ok");
                } else {
                    response.getWriter().write("dup");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
        s.close();
    }

}
