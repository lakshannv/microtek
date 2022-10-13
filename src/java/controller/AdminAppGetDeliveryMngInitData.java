package controller;

import com.google.gson.Gson;
import hibernate.ApplicationSetting;
import hibernate.District;
import hibernate.HiberUtil;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AdminAppGetDeliveryMngInitData", urlPatterns = {"/AdminAppGetDeliveryMngInitData"})
public class AdminAppGetDeliveryMngInitData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        LinkedList<LinkedHashMap<String, Object>> districtList = new LinkedList();
        Gson g = new Gson();

        int freeDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_within")).getValue());
        String freeDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_time_unit")).getValue();
        String expDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_time_unit")).getValue();
        int expDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_within")).getValue());
        double delProfitMargin = Double.parseDouble(((ApplicationSetting) s.load(ApplicationSetting.class, "delivery_profit_margin")).getValue());

        Criteria distCR = s.createCriteria(District.class);
        List<District> distList = distCR.list();
        for (District d : distList) {
            LinkedHashMap<String, Object> dist = new LinkedHashMap();
            dist.put("id", String.valueOf(d.getId()));
            dist.put("title", d.getName());
            dist.put("delcost", d.getDeliveryFee() * (100 - delProfitMargin) / 100);
            dist.put("delfee", d.getDeliveryFee());
            dist.put("status", d.getActive() == 1);
            districtList.add(dist);
        }

        responseData.put("freeDelWithin", String.valueOf(freeDelWithin));
        responseData.put("expDelWithin", String.valueOf(expDelWithin));
        responseData.put("freeDelTimeUnit", freeDelTimeUnit);
        responseData.put("expDelTimeUnit", expDelTimeUnit);
        responseData.put("delProfitMargin", delProfitMargin);

        responseData.put("districtList", districtList);
        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
