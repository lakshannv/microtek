package controller;

import hibernate.ApplicationSetting;
import hibernate.HiberUtil;
import hibernate.User;
import hibernate.UserPrivilege;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "UpdatePGDetails", urlPatterns = {"/UpdatePGDetails"})
public class UpdatePGDetails extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            User sesUsr = (User) request.getSession().getAttribute("usr");
            if (sesUsr == null) {
                response.getWriter().write("err");
            } else {
                User usr = (User) s.load(User.class, sesUsr.getId());
                UserPrivilege up = (UserPrivilege) s.load(UserPrivilege.class, 1);
                if (usr.getUserType().getUserPrivileges().contains(up)) {
                    String merchant_id = request.getParameter("merchant_id").trim();
                    String merchant_secret = request.getParameter("merchant_secret").trim();;
                    if (merchant_id.isEmpty() || merchant_secret.isEmpty()) {
                        response.getWriter().write("emp");
                    } else {
                        ApplicationSetting mID = (ApplicationSetting) s.load(ApplicationSetting.class, "merchant_id");
                        mID.setValue(merchant_id);
                        s.update(mID);
                        ApplicationSetting mSecret = (ApplicationSetting) s.load(ApplicationSetting.class, "merchant_secret");
                        mSecret.setValue(merchant_secret);
                        s.update(mSecret);
                        s.beginTransaction().commit();
                        response.getWriter().write("ok");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
        s.close();
    }

}
