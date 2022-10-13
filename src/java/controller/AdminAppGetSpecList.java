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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author laksh
 */
@WebServlet(name = "AdminAppGetSpecList", urlPatterns = {"/AdminAppGetSpecList"})
public class AdminAppGetSpecList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        Gson g = new Gson();
        String catName = request.getParameter("catName");

        LinkedList<LinkedList<String>> specsList = new LinkedList();

        Criteria catCR = s.createCriteria(Category.class);
        catCR.add(Restrictions.eq("name", catName));
        Category c = (Category) catCR.uniqueResult();

        if (c != null) {
            Set<Spec> specSet = c.getSpecs();
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
