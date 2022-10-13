package controller;

import com.google.gson.Gson;
import hibernate.City;
import hibernate.District;
import hibernate.HiberUtil;
import hibernate.Province;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppGetAddrData", urlPatterns = {"/AppGetAddrData"})
public class AppGetAddrData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Gson g = new Gson();
        LinkedList<Map<String, String>> addressData = new LinkedList();
        TreeMap<String, String> districtMap = new TreeMap();
        TreeMap<String, String> cityMap = new TreeMap();
        String provinceID = request.getParameter("provinceID");
        String districtID = request.getParameter("districtID");

        if (provinceID != null) {
            Province p = (Province) s.load(Province.class, Integer.parseInt(provinceID));

            Criteria cr = s.createCriteria(District.class);
            cr.add(Restrictions.eq("province", p));
            cr.addOrder(Order.asc("name"));
            List<District> distList = cr.list();
            for (District d : distList) {
                districtMap.put(d.getName(), String.valueOf(d.getId()));
            }

            cr = s.createCriteria(City.class);
            cr.add(Restrictions.eq("district", distList.get(0)));
            cr.addOrder(Order.asc("name"));
            List<City> ctyList = cr.list();
            for (City d : ctyList) {
                cityMap.put(d.getName(), String.valueOf(d.getId()));
            }
            addressData.add(districtMap);
            addressData.add(cityMap);
        }
        if (districtID != null) {
            District d = (District) s.load(District.class, Integer.parseInt(districtID));

            Criteria cr = s.createCriteria(City.class);
            cr.add(Restrictions.eq("district", d));
            cr.addOrder(Order.asc("name"));
            List<City> ctyList = cr.list();
            for (City c : ctyList) {
                cityMap.put(c.getName(), String.valueOf(c.getId()));
            }
            addressData.add(cityMap);
        }
        response.getWriter().write(g.toJson(addressData));
        s.close();
    }

}
