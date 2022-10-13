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

@WebServlet(name = "AdminAppUpdateFCM", urlPatterns = {"/AdminAppUpdateFCM"})
public class AdminAppUpdateFCM extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Session s = HiberUtil.getSessionFactory().openSession();
        int uID = Integer.parseInt(request.getParameter("uID"));
        String fcmToken = request.getParameter("fcmToken");

        User c = (User) s.load(User.class, uID);
        
        c.setFcmToken(fcmToken);
        s.update(c);
        s.beginTransaction().commit();
        s.close();
    }

}
