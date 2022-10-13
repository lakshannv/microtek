package controller;

import com.google.gson.Gson;
import hibernate.Brand;
import hibernate.HiberUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
import org.hibernate.criterion.Order;


@WebServlet(name = "AdminAppLoadBrands", urlPatterns = {"/AdminAppLoadBrands"})
public class AdminAppLoadBrands extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        
        HashMap<String, Object> responseData = new HashMap();
        LinkedList<HashMap<String, Object>> brandList = new LinkedList();
        Gson g = new Gson();
        
        Criteria c = s.createCriteria(Brand.class);
        c.addOrder(Order.asc("name"));
        
        List<Brand> brList = c.list();
        for (Brand b : brList) {
            HashMap<String, Object> br = new HashMap();
            br.put("id", String.valueOf(b.getId()));
            br.put("name", b.getName());
            br.put("main", b.getMainWebsite());
            br.put("supp", b.getSupportWebsite());
            br.put("isActive", b.getActive() == (byte) 1);
            File f = new File(getServletContext().getRealPath("") + "//assets//img/brands//" + b.getId());
            br.put("hasImage", f.exists());
            brandList.add(br);
        }
        
        responseData.put("brandList", brandList);
        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
