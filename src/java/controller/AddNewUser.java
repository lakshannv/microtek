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

@WebServlet(name = "AddNewUser", urlPatterns = {"/AddNewUser"})
public class AddNewUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            String uname = request.getParameter("uname").trim();
            String pw = request.getParameter("pw");
            int uType = Integer.parseInt(request.getParameter("uType"));
            if (uname.isEmpty()) {
                response.getWriter().write("un");
            } else if (pw.isEmpty()) {
                response.getWriter().write("pw");
            } else if (uType == 1) {
                response.getWriter().write("err");
            } else {
                Criteria uCR = s.createCriteria(User.class);
                uCR.add(Restrictions.eq("username", uname));
                if (uCR.list().isEmpty()) {
                   UserType usrType = (UserType) s.load(UserType.class, uType);
                User u = new User(usrType, uname, pw, (byte) 1, null);
                s.save(u);
                s.beginTransaction().commit();
                response.getWriter().write("ok"); 
                } else {
                    response.getWriter().write("dup");
                }
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }
        s.close();
    }

}
