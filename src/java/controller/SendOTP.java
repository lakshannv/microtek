package controller;

import hibernate.Customer;
import hibernate.HiberUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Email;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SendOTP", urlPatterns = {"/SendOTP"})
public class SendOTP extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String eml = request.getParameter("eml");

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            if (eml.isEmpty()) {
                response.getWriter().write("emp");
            } else {
                Criteria custCR = s.createCriteria(Customer.class);
                custCR.add(Restrictions.eq("email", eml));
                Customer c = (Customer) custCR.uniqueResult();
                if (c == null) {
                    response.getWriter().write("inv");
                } else {
                    String otp = Validation.generateOTP();
                    Email.send(eml, "MicroTek Account Password Reset", "<h1 style='color: red;'>Password Reset Request</h1><h4>Your One Time Password for your Microtek account is:</h4><h2 style='color: green;'>" + otp + "</h2>");
                    request.getSession().setAttribute("otp", otp);
                    response.getWriter().write("ok-" + c.getUsername());
                }
            }
        } catch (Exception e) {
            response.getWriter().write("err");
            e.printStackTrace();
        }
        s.close();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String eml = request.getParameter("eml");
        String otp = request.getParameter("otp");

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            if (eml.isEmpty()) {
                response.getWriter().write("emp");
            } else {
                Criteria custCR = s.createCriteria(Customer.class);
                custCR.add(Restrictions.eq("email", eml));
                Customer c = (Customer) custCR.uniqueResult();
                if (c == null) {
                    response.getWriter().write("inv");
                } else {
                    Email.send(eml, "MicroTek Account Password Reset", "<h1 style='color: red;'>Password Reset Request</h1><h4>Your One Time Password for your Microtek account is:</h4><h2 style='color: green;'>" + otp + "</h2>");
                    response.getWriter().write("ok-" + c.getUsername());
                }
            }
        } catch (Exception e) {
            response.getWriter().write("err");
            e.printStackTrace();
        }
        s.close();

    }

}
