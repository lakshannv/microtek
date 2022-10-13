package controller;

import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Spec;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@WebServlet(name = "UpdateSpec", urlPatterns = {"/UpdateSpec"})
public class UpdateSpec extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int specID = Integer.parseInt(request.getParameter("specID"));
            String specName = request.getParameter("specName");
            String specUnit = request.getParameter("specUnit").trim();
            byte isKey = 0;
            if (request.getParameter("isKey").equals("true")) {
                isKey = 1;
            }

            if (Validation.isValidName(specName)) {
                specName = Validation.getValidatedName(specName);
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria cr = s.createCriteria(Spec.class);
                List<Spec> specList = (List<Spec>) cr.list();

                Spec existingSpec = null;
                for (Spec sp : specList) {
                    if (sp.getId() == specID) {
                        existingSpec = sp;
                        break;
                    }
                }

                if (existingSpec == null) {
                    response.getWriter().write("err");
                } else {
                    if (specUnit.isEmpty()) {
                        specUnit = null;
                    }
                    if ((existingSpec.getSpecName() == specName) && (existingSpec.getUnit() == specUnit) && (existingSpec.getIsKey() == isKey)) {
                        response.getWriter().write("dup");
                    } else {
                        existingSpec.setSpecName(specName);
                        existingSpec.setUnit(specUnit);
                        existingSpec.setIsKey(isKey);
                        s.update(existingSpec);
                        response.getWriter().write("ok");
                    }
                }

                Transaction t = s.beginTransaction();
                t.commit();
                s.close();
            } else {
                response.getWriter().write("inv");
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }
    }
}