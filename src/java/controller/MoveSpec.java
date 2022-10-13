package controller;

import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductHasSpec;
import hibernate.ProductHasSpecId;
import hibernate.Spec;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "MoveSpec", urlPatterns = {"/MoveSpec"})
public class MoveSpec extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            int specID = Integer.parseInt(request.getParameter("specID"));
            int previousSpecID = Integer.parseInt(request.getParameter("previousSpecID"));
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();
                Criteria cr1 = s.createCriteria(Spec.class);
                cr1.add(Restrictions.eq("id", specID));
                Spec ogSpec = (Spec) cr1.uniqueResult();

                Criteria prodSpecCR1 = s.createCriteria(ProductHasSpec.class);
                prodSpecCR1.add(Restrictions.eq("spec", ogSpec));
                List<ProductHasSpec> ogProdSpecList = prodSpecCR1.list();
                
                HashMap<Integer, String> ogSpecMap = new HashMap<>();
                for (ProductHasSpec prodSpec : ogProdSpecList) {
                    ogSpecMap.put(prodSpec.getProduct().getId(), prodSpec.getSpecValue());
                    s.delete(prodSpec);
                }

                Criteria cr2 = s.createCriteria(Spec.class);
                cr2.add(Restrictions.eq("id", previousSpecID));
                Spec prvSpec = (Spec) cr2.uniqueResult();

                Criteria prodSpecCR2 = s.createCriteria(ProductHasSpec.class);
                prodSpecCR2.add(Restrictions.eq("spec", prvSpec));
                List<ProductHasSpec> prvProdSpecList = prodSpecCR2.list();

                HashMap<Integer, String> prvSpecMap = new HashMap<>();
                for (ProductHasSpec prodSpec : prvProdSpecList) {
                    prvSpecMap.put(prodSpec.getProduct().getId(), prodSpec.getSpecValue());
                    s.delete(prodSpec);
                }
                Transaction t = s.beginTransaction();
                t.commit();
                s.close();

                Session s1 = sf.openSession();

                s1.createSQLQuery("DELETE FROM spec WHERE id = " + previousSpecID + ";").executeUpdate();
                String ogu = null;
                if (ogSpec.getUnit() != null) {
                    ogu = "'" + ogSpec.getUnit() + "'";
                }
                s1.createSQLQuery("INSERT INTO spec(id, category_id, spec_name, unit, is_key) VALUES(" + previousSpecID + ", " + ogSpec.getCategory().getId() + ", '" + ogSpec.getSpecName() + "', " + ogu + ", " + ogSpec.getIsKey() + ");").executeUpdate();

                s1.createSQLQuery("DELETE FROM spec WHERE id = " + specID + ";").executeUpdate();
                String prvu = null;
                if (prvSpec.getUnit() != null) {
                    prvu = "'" + prvSpec.getUnit() + "'";
                }
                s1.createSQLQuery("INSERT INTO spec(id, category_id, spec_name, unit, is_key) VALUES(" + specID + ", " + prvSpec.getCategory().getId() + ", '" + prvSpec.getSpecName() + "', " + prvu + ", " + prvSpec.getIsKey() + ");").executeUpdate();

                for (Integer productID : ogSpecMap.keySet()) {
                    s1.createSQLQuery("INSERT INTO product_has_spec(product_id, spec_id, spec_value) VALUES(" + productID + ", " + previousSpecID + ", '" + ogSpecMap.get(productID) + "');").executeUpdate();
                }
                for (Integer productID : prvSpecMap.keySet()) {
                    s1.createSQLQuery("INSERT INTO product_has_spec(product_id, spec_id, spec_value) VALUES(" + productID + ", " + specID + ", '" + prvSpecMap.get(productID) + "');").executeUpdate();
                }
                
                s1.beginTransaction().commit();
                s1.close();
                response.getWriter().write("ok");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }

    }

}
