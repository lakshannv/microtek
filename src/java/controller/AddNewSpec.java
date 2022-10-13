package controller;

import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Spec;
import java.io.IOException;
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

@WebServlet(name = "AddNewSpec", urlPatterns = {"/AddNewSpec"})
public class AddNewSpec extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int catID = Integer.parseInt(request.getParameter("catID"));
            String specName = request.getParameter("specName").trim();
            String specUnit = request.getParameter("specUnit").trim();
            byte isKey = 0;
            if (request.getParameter("isKey").equals("true")) {
                isKey = 1;
            }

            if (Validation.isValidName(specName)) {
                specName = Validation.getValidatedName(specName);
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria cr = s.createCriteria(Category.class);
                List<Category> catList = (List<Category>) cr.list();

                Category existingCat = null;
                for (Category cat : catList) {
                    if (cat.getId() == catID) {
                        existingCat = cat;
                        break;
                    }
                }
                if (existingCat == null) {
                    response.getWriter().write("err");
                } else {
                    Criteria crSpecs = s.createCriteria(Spec.class);
                    List<Spec> specList = (List<Spec>) crSpecs.list();

                    boolean exists = false;
                    for (Spec sp : specList) {
                        if (sp.getSpecName().equalsIgnoreCase(specName) && sp.getCategory() == existingCat) {
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        response.getWriter().write("dup");
                    } else {
                        Spec sp = new Spec();
                        sp.setCategory(existingCat);
                        sp.setSpecName(specName);
                        if (!specUnit.isEmpty()) {
                            sp.setUnit(specUnit);
                        }
                        sp.setIsKey(isKey);
                        s.save(sp);
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
            e.printStackTrace();
        }
    }
}
