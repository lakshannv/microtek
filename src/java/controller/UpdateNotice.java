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

@WebServlet(name = "UpdateNotice", urlPatterns = {"/UpdateNotice"})
public class UpdateNotice extends HttpServlet {

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
                    String notice = request.getParameter("notice");
                    if (notice.trim().isEmpty()) {
                        notice = null;
                    }
                    ApplicationSetting as = (ApplicationSetting) s.load(ApplicationSetting.class, "notice_message");
                    as.setValue(notice);
                    s.update(as);
                    s.beginTransaction().commit();
                    response.getWriter().write("ok");
                }
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }
        s.close();
    }

}
