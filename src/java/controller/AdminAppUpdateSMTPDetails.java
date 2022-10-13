package controller;

import hibernate.ApplicationSetting;
import hibernate.HiberUtil;
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

@WebServlet(name = "AdminAppUpdateSMTPDetails", urlPatterns = {"/AdminAppUpdateSMTPDetails"})
public class AdminAppUpdateSMTPDetails extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
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
        s.close();
    }

}
