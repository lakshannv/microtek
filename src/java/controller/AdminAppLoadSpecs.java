package controller;

import com.google.gson.Gson;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Spec;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdminAppLoadSpecs", urlPatterns = {"/AdminAppLoadSpecs"})
public class AdminAppLoadSpecs extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        Integer catID = Integer.parseInt(request.getParameter("catID"));
        Category c = (Category) s.load(Category.class, catID);

        HashMap<String, Object> responseData = new HashMap();
        LinkedList<HashMap<String, Object>> specList = new LinkedList();
        Gson g = new Gson();

        Criteria crSpecs = s.createCriteria(Spec.class);
        crSpecs.add(Restrictions.eq("category", c));
        crSpecs.addOrder(Order.asc("id"));

        List<Spec> spList = crSpecs.list();
        for (Spec spec : spList) {
            HashMap<String, Object> sp = new HashMap();
            sp.put("id", String.valueOf(spec.getId()));
            sp.put("name", spec.getSpecName());
            sp.put("unit", spec.getUnit());
            sp.put("isKeySpec", spec.getIsKey() == (byte) 1);
            specList.add(sp);
        }

        responseData.put("specList", specList);
        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
