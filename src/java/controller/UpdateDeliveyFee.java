package controller;

import hibernate.District;
import hibernate.HiberUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "UpdateDeliveyFee", urlPatterns = {"/UpdateDeliveyFee"})
public class UpdateDeliveyFee extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            int distID = Integer.parseInt(request.getParameter("distID"));
            double fee = Double.parseDouble(request.getParameter("fee"));
            Boolean deliver = Boolean.parseBoolean(request.getParameter("deliver"));
            if (fee < 0) {
                response.getWriter().write("err");
            } else {
                District d = (District) s.load(District.class, distID);
                d.setDeliveryFee(fee);
                if (deliver) {
                    d.setActive((byte) 1);
                } else {
                    d.setActive((byte) 0);
                }
                s.update(d);
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
