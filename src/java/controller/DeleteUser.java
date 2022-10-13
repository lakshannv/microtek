package controller;

import hibernate.HiberUtil;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "DeleteUser", urlPatterns = {"/DeleteUser"})
public class DeleteUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            int uID = Integer.parseInt(request.getParameter("uID"));
            String blockParam = request.getParameter("block");
            User u = (User) s.load(User.class, uID);
            if (u.getUserType().getId() == 1) {
                response.getWriter().write("err");
            } else {
                if (blockParam == null) {
                    s.delete(u);
                } else {
                    Boolean isBlocked = Boolean.parseBoolean(blockParam);
                    if (isBlocked) {
                        u.setActive((byte) 0);
                    } else {
                        u.setActive((byte) 1);
                    }
                    s.update(u);
                }
                s.beginTransaction().commit();
                response.getWriter().write("ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
        s.close();
    }

}
