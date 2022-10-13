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

@WebServlet(name = "UpdateDeliveyProfit", urlPatterns = {"/UpdateDeliveyProfit"})
public class UpdateDeliveyProfit extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        DecimalFormat df = new DecimalFormat("#0.##");
        try {
            double profit = Double.parseDouble(request.getParameter("profit"));
            if (profit <= 0) {
                response.getWriter().write("err");
            } else {
                ApplicationSetting as = (ApplicationSetting) s.load(ApplicationSetting.class, "delivery_profit_margin");
                as.setValue(df.format(profit));
                s.save(as);
                s.beginTransaction().commit();
                response.getWriter().write("ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
        s.close();
    }
    
}
