package controller;

import hibernate.Brand;
import hibernate.HiberUtil;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AddNewBrand", urlPatterns = {"/AddNewBrand"})
public class AddNewBrand extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String brandName = request.getParameter("brandName");
            String returnBrandID = request.getParameter("returnBrandID");
            if (Validation.isValidName(brandName)) {
                brandName = Validation.getValidatedName(brandName);
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria cr = s.createCriteria(Brand.class);
                List<Brand> brandList = (List<Brand>) cr.list();
                boolean exists = false;
                for (Brand b : brandList) {
                    if (b.getName().equalsIgnoreCase(brandName)) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    response.getWriter().write("dup");
                } else {
                    Brand b = new Brand();
                    b.setName(brandName);
                    b.setActive((byte) 1);
                    s.save(b);
                    s.beginTransaction().commit();
                    
                    if(Objects.equals(returnBrandID, "true")) {
                        response.getWriter().write("ok-" + b.getId());
                    } else {
                        response.getWriter().write("ok");
                    }
                    
                }
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