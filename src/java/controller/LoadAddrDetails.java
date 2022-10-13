package controller;

import com.google.gson.Gson;
import hibernate.HiberUtil;
import hibernate.ShippingAddress;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadAddrDetails", urlPatterns = {"/LoadAddrDetails"})
public class LoadAddrDetails extends HttpServlet {
    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int addrID = Integer.parseInt(request.getParameter("id"));
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria addrCR = s.createCriteria(ShippingAddress.class);
        addrCR.add(Restrictions.eq("id", addrID));
        ShippingAddress addr = (ShippingAddress) addrCR.uniqueResult();
        
        HashMap<String, String> hm = new HashMap();
        hm.put("province", addr.getProvince().getName());
        hm.put("district", addr.getDistrict().getName());
        hm.put("city", addr.getCity().getName());
        hm.put("name", addr.getName());
        hm.put("postCode", addr.getCity().getPostcode());
        hm.put("fee", df.format(addr.getDistrict().getDeliveryFee()));
        
        Gson g = new Gson();
        response.getWriter().write(g.toJson(hm));
    }

}
