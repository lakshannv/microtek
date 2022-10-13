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
import model.Email;
import model.Validation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "UpdateSMTPDetails", urlPatterns = {"/UpdateSMTPDetails"})
public class UpdateSMTPDetails extends HttpServlet {

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
                    String host = request.getParameter("host").trim();
                    String port = request.getParameter("port").trim();
                    String sender = request.getParameter("sender").trim();
                    String pw = request.getParameter("pw").trim();
                    if (host.isEmpty() || port.isEmpty() || sender.isEmpty() || pw.isEmpty()) {
                        response.getWriter().write("emp");
                    } else {
                        if (Validation.isValidEmail(sender)) {
                            ApplicationSetting smtp_host = (ApplicationSetting) s.load(ApplicationSetting.class, "smtp_host");
                            smtp_host.setValue(host);
                            s.update(smtp_host);
                            ApplicationSetting smtp_port = (ApplicationSetting) s.load(ApplicationSetting.class, "smtp_port");
                            smtp_port.setValue(port);
                            s.update(smtp_port);
                            ApplicationSetting smtp_sender = (ApplicationSetting) s.load(ApplicationSetting.class, "smtp_sender");
                            smtp_sender.setValue(sender);
                            s.update(smtp_sender);
                            ApplicationSetting smtp_password = (ApplicationSetting) s.load(ApplicationSetting.class, "smtp_password");
                            smtp_password.setValue(pw);
                            s.update(smtp_password);
                            s.beginTransaction().commit();
                            Email.init();
                            response.getWriter().write("ok");
                        } else {
                            response.getWriter().write("eml");
                        }
                    }
                }
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }
        s.close();
    }

}
