package controller;

import hibernate.HiberUtil;
import hibernate.ShippingAddress;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "DeleteAddress", urlPatterns = {"/DeleteAddress"})
public class DeleteAddress extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int addrID = Integer.parseInt(request.getParameter("addrID"));
            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();
            ShippingAddress shipAddr = (ShippingAddress) s.load(ShippingAddress.class, addrID);
            
            s.delete(shipAddr);
            s.beginTransaction().commit();
            response.getWriter().write("ok");

            s.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
    }
}
