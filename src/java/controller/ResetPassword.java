package controller;

import com.google.gson.Gson;
import hibernate.Customer;
import hibernate.HiberUtil;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ResetPassword", urlPatterns = {"/ResetPassword"})
public class ResetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ArrayList<String> errList = new ArrayList<>();

        String oldPw = request.getParameter("oldPw");
        String otp = request.getParameter("otp");
        String usr = request.getParameter("usr");
        String pw = request.getParameter("pw");
        String pwCon = request.getParameter("pwCon");

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Customer c = null;

        try {
            if (otp == null) {
                Customer sesCust = (Customer) request.getSession().getAttribute("cust");
                c = (Customer) s.load(Customer.class, sesCust.getId());
                if (!c.getPassword().equals(oldPw)) {
                    errList.add("wrong-pw");
                }
            } else {
                Criteria custCR = s.createCriteria(Customer.class);
                custCR.add(Restrictions.eq("username", usr));
                c = (Customer) custCR.uniqueResult();
                String sesOTP = (String) request.getSession().getAttribute("otp");
                if (sesOTP == null) {
                    errList.add("otp");
                } else {
                    if (!sesOTP.equals(otp)) {
                        errList.add("otp");
                    }
                }
            }
            if (pw.isEmpty()) {
                errList.add("inv-pw");
            } else {
                if (!pw.equals(pwCon)) {
                    errList.add("pwmis");
                }
            }

            if (errList.isEmpty()) {
                c.setPassword(pw);
                s.update(c);
                s.beginTransaction().commit();
                s.close();
                response.getWriter().write("ok");
            }

            if (!errList.isEmpty()) {
                Gson g = new Gson();
                response.getWriter().write(g.toJson(errList));
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }

    }

}
