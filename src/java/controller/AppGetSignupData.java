package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.ApplicationSetting;
import hibernate.Cart;
import hibernate.City;
import hibernate.Customer;
import hibernate.District;
import hibernate.HiberUtil;
import hibernate.Province;
import hibernate.ShippingAddress;
import hibernate.Stock;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.list.TreeList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppGetSignupData", urlPatterns = {"/AppGetSignupData"})
public class AppGetSignupData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Gson g = new Gson();

        HashMap<String, Object> responeDataMap = new HashMap();

        LinkedList<Map<String, String>> addressData = new LinkedList();
        LinkedTreeMap<String, String> provinceMap = new LinkedTreeMap();
        TreeMap<String, String> districtMap = new TreeMap();
        TreeMap<String, String> cityMap = new TreeMap();       

        Criteria cr = s.createCriteria(Province.class);
        cr.addOrder(Order.asc("id"));
        List<Province> proviList = cr.list();
        for (Province p : proviList) {
            provinceMap.put(p.getName(), String.valueOf(p.getId()));
        }
                
        cr = s.createCriteria(District.class);
        cr.add(Restrictions.eq("province", proviList.get(0)));
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
        addressData.add(provinceMap);
        addressData.add(districtMap);
        addressData.add(cityMap);
        responeDataMap.put("addressData", addressData);

        response.getWriter().write(g.toJson(responeDataMap));

        s.close();
    }

}
