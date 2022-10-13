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

@WebServlet(name = "AdminAppUpdateNotice", urlPatterns = {"/AdminAppUpdateNotice"})
public class AdminAppUpdateNotice extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        String notice = request.getParameter("notice");
        if (notice.trim().isEmpty()) {
            notice = null;
        }
        ApplicationSetting as = (ApplicationSetting) s.load(ApplicationSetting.class, "notice_message");
        as.setValue(notice);
        s.update(as);
        s.beginTransaction().commit();
        response.getWriter().write("ok");
        s.close();
    }

}
