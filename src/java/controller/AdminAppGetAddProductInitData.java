package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.Brand;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Spec;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

/**
 *
 * @author laksh
 */
@WebServlet(name = "AdminAppGetAddProductInitData", urlPatterns = {"/AdminAppGetAddProductInitData"})
public class AdminAppGetAddProductInitData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        Gson g = new Gson();

        LinkedTreeMap<String, String> catMap = new LinkedTreeMap();
        Criteria crCategory = s.createCriteria(Category.class);
        crCategory.addOrder(Order.asc("name"));
        List<Category> l = (List<Category>) crCategory.list();
        for (Category c : l) {
            catMap.put(String.valueOf(c.getName()), String.valueOf(c.getId()));
        }

        LinkedTreeMap<String, String> brandMap = new LinkedTreeMap();
        Criteria crBrand = s.createCriteria(Brand.class);
        crBrand.addOrder(Order.asc("name"));
        List<Brand> lb = (List<Brand>) crBrand.list();
        for (Brand c : lb) {
            brandMap.put(String.valueOf(c.getName()), String.valueOf(c.getId()));
        }

        responseData.put("catMap", catMap);
        responseData.put("brandMap", brandMap);

        LinkedList<LinkedList<String>> specsList = new LinkedList();
        if (catMap.size() > 0) {
            Map.Entry<String, String> firstCatEntry = catMap.entrySet().iterator().next();
            Category category = (Category) s.load(Category.class, Integer.parseInt(firstCatEntry.getValue()));
            Set<Spec> specSet = category.getSpecs();
            for (Spec spec : specSet) {
                LinkedList<String> pSpecs = new LinkedList();
                pSpecs.add(String.valueOf(spec.getId()));
                if (spec.getUnit() == null) {
                    pSpecs.add(spec.getSpecName());
                } else {
                    pSpecs.add(spec.getSpecName() + " (" + spec.getUnit() + ")");
                }
                specsList.add(pSpecs);
            }
        }

        responseData.put("specList", specsList);

        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
