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

@WebServlet(name = "AppResetPassword", urlPatterns = {"/AppResetPassword"})
public class AppResetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ArrayList<String> errList = new ArrayList<>();

        String oldPw = request.getParameter("oldPw");
        String pw = request.getParameter("pw");
        String pwCon = request.getParameter("pwCon");

        String np = request.getParameter("np");
        String usr = request.getParameter("usr");

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        if (usr == null) {
            int custID = Integer.parseInt(request.getParameter("custID"));
            Customer c = (Customer) s.load(Customer.class, custID);
            if (!c.getPassword().equals(oldPw)) {
                errList.add("wrong-pw");
            }
            if (pw.trim().isEmpty()) {
                errList.add("inv-pw");
            } else {
                if (!pw.equals(pwCon)) {
                    errList.add("pwmis");
                }
            }

            if (errList.isEmpty()) {
                c.setPassword(pw);
                s.update(c);

                response.getWriter().write("ok");
            }

            if (!errList.isEmpty()) {
                Gson g = new Gson();
                response.getWriter().write(g.toJson(errList));
            }
        } else {
            Criteria custCR = s.createCriteria(Customer.class);
            custCR.add(Restrictions.eq("username", usr));
            Customer c = (Customer) custCR.uniqueResult();
            c.setPassword(np);
            s.update(c);
            response.getWriter().write("ok");
        }

        s.beginTransaction().commit();
        s.close();
    }

}
