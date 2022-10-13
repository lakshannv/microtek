package controller;

import hibernate.HiberUtil;
import hibernate.UserType;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "DeleteUserType", urlPatterns = {"/DeleteUserType"})
public class DeleteUserType extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            int uTypeID = Integer.parseInt(request.getParameter("uTypeID"));
            UserType ut = (UserType) s.load(UserType.class, uTypeID);
            s.delete(ut);
            s.beginTransaction().commit();
            response.getWriter().write("ok");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
        s.close();
    }

}
