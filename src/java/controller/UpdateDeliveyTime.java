package controller;

import hibernate.ApplicationSetting;
import hibernate.HiberUtil;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "UpdateDeliveyTime", urlPatterns = {"/UpdateDeliveyTime"})
public class UpdateDeliveyTime extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            String freeTimeUnit = request.getParameter("freeTimeUnit");
            String expTimeUnit = request.getParameter("expTimeUnit");
            if ((freeTimeUnit.equals("Days") || freeTimeUnit.equals("Weeks") || freeTimeUnit.equals("Months")) && (expTimeUnit.equals("Days") || expTimeUnit.equals("Weeks") || expTimeUnit.equals("Months"))) {
                int freeTime = Integer.parseInt(request.getParameter("freeTime"));
                int expTime = Integer.parseInt(request.getParameter("expTime"));
                if (freeTime <= 0 && expTime <= 0) {
                    response.getWriter().write("err");
                } else {
                    ApplicationSetting free_del_within = (ApplicationSetting) s.load(ApplicationSetting.class, "free_del_within");
                    ApplicationSetting free_del_time_unit = (ApplicationSetting) s.load(ApplicationSetting.class, "free_del_time_unit");
                    ApplicationSetting exp_del_within = (ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_within");
                    ApplicationSetting exp_del_time_unit = (ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_time_unit");
                    free_del_within.setValue(String.valueOf(freeTime));
                    exp_del_within.setValue(String.valueOf(expTime));
                    free_del_time_unit.setValue(freeTimeUnit);
                    exp_del_time_unit.setValue(expTimeUnit);
                    s.save(free_del_within);
                    s.save(exp_del_within);
                    s.save(free_del_time_unit);
                    s.save(exp_del_time_unit);
                    s.beginTransaction().commit();
                    response.getWriter().write("ok");
                }
            } else {
                response.getWriter().write("err");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
        s.close();
    }

}
