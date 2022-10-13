package controller;

import hibernate.ApplicationSetting;
import hibernate.HiberUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AdminAppUpdatePGDetails", urlPatterns = {"/AdminAppUpdatePGDetails"})
public class AdminAppUpdatePGDetails extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        String merchant_id = request.getParameter("merchant_id").trim();
        String merchant_secret = request.getParameter("merchant_secret").trim();;
        if (merchant_id.isEmpty() || merchant_secret.isEmpty()) {
            response.getWriter().write("emp");
        } else {
            ApplicationSetting mID = (ApplicationSetting) s.load(ApplicationSetting.class, "merchant_id");
            mID.setValue(merchant_id);
            s.update(mID);
            ApplicationSetting mSecret = (ApplicationSetting) s.load(ApplicationSetting.class, "merchant_secret_app");
            mSecret.setValue(merchant_secret);
            s.update(mSecret);
            s.beginTransaction().commit();
            response.getWriter().write("ok");
        }
        s.close();
    }

}
