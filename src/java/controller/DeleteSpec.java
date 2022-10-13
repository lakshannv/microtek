package controller;

import hibernate.HiberUtil;
import hibernate.ProductHasSpec;
import hibernate.Spec;
import java.io.IOException;
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

@WebServlet(name = "DeleteSpec", urlPatterns = {"/DeleteSpec"})
public class DeleteSpec extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int specID = Integer.parseInt(request.getParameter("specID"));

            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria cr = s.createCriteria(Spec.class);
            cr.add(Restrictions.eq("id", specID));

            Spec existingSpec = (Spec) cr.uniqueResult();

            Criteria pSpecCR = s.createCriteria(ProductHasSpec.class);
            pSpecCR.add(Restrictions.eq("spec", existingSpec));
            for (ProductHasSpec sp : (List<ProductHasSpec>) pSpecCR.list()) {
                s.delete(sp);
            }
            s.delete(existingSpec);
            response.getWriter().write("ok");

            Transaction t = s.beginTransaction();
            t.commit();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
    }
}
