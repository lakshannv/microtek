package controller;

import hibernate.Customer;
import hibernate.HiberUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;

@WebServlet(name = "AppUpdateFCM", urlPatterns = {"/AppUpdateFCM"})
public class AppUpdateFCM extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Session s = HiberUtil.getSessionFactory().openSession();
        int custID = Integer.parseInt(request.getParameter("custID"));
        String fcmToken = request.getParameter("fcmToken");

        Customer c = (Customer) s.load(Customer.class, custID);
        
        c.setFcmToken(fcmToken);
        s.update(c);
        s.beginTransaction().commit();
        s.close();
    }

}
