package controller;

import com.google.gson.Gson;
import hibernate.ApplicationSetting;
import hibernate.Customer;
import hibernate.HiberUtil;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppGetInitData", urlPatterns = {"/AppGetInitData"})
public class AppGetInitData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HashMap<String, Object> initDataMap = new HashMap();

        String un = request.getParameter("un");
        String pw = request.getParameter("pw");
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        
        Gson g = new Gson();
        
        
        ServletContext sc = request.getServletContext();
        if (sc.getAttribute("remainingTime") == null) {
            initDataMap.put("isOnline", true);
        } else {
            long remainingTime = (long) sc.getAttribute("remainingTime");
            if (remainingTime > 0) {
                initDataMap.put("isOnline", false);
                initDataMap.put("remainingTime", remainingTime);
            } else if (remainingTime <= -99) {
                initDataMap.put("isOnline", false);
                initDataMap.put("remainingTime", -99);
            } else {
                initDataMap.put("isOnline", true);
            }
        }

        if (un != null && pw != null) {
            Criteria custCR = s.createCriteria(Customer.class);
            custCR.add(Restrictions.eq("username", un));
            if (custCR.list().size() == 0) {
                initDataMap.put("user", "reset");
            } else {
                custCR.add(Restrictions.eq("password", pw));
                Customer cust = (Customer) custCR.uniqueResult();
                if (cust == null) {
                    initDataMap.put("user", "reset");
                } else {
                    if (cust.getPassword().equals(pw)) {
                        initDataMap.put("user", g.toJson(cust, Customer.class));
                        initDataMap.put("cartCount", String.valueOf(cust.getCarts().size()));
                    } else {
                        response.getWriter().write("reset");
                    }
                }
            }
        }
        String fcmServerKey = ((ApplicationSetting) s.load(ApplicationSetting.class, "fcm_server_key")).getValue();
        String merchantSecret = ((ApplicationSetting) s.load(ApplicationSetting.class, "merchant_secret_app")).getValue();
        
        initDataMap.put("fcmServerKey", fcmServerKey);
        initDataMap.put("merchantSecret", merchantSecret);
        response.getWriter().write(g.toJson(initDataMap));

        s.close();
    }

}
