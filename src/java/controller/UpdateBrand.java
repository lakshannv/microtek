package controller;

import hibernate.Brand;
import hibernate.HiberUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Validation;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@WebServlet(name = "UpdateBrand", urlPatterns = {"/UpdateBrand"})
public class UpdateBrand extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int brandID = Integer.parseInt(request.getParameter("brandID"));
            String brandName = request.getParameter("brandName");

            if (Validation.isValidName(brandName)) {
                brandName = Validation.getValidatedName(brandName);
                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria cr = s.createCriteria(Brand.class);
                List<Brand> brandList = (List<Brand>) cr.list();

                Brand existingBrand = null;
                for (Brand br : brandList) {
                    if (br.getId() == brandID) {
                        existingBrand = br;
                        break;
                    }
                }

                if (existingBrand == null) {
                    response.getWriter().write("err");
                } else {
                    byte isActive;
                    if (request.getParameter("isActive").equals("true")) {
                        isActive = 1;
                    } else {
                        isActive = 0;
                    }

                    boolean exists = false;
                    for (Brand br : brandList) {
                        if (br.getName().equalsIgnoreCase(brandName) && br.getId() != brandID) {
                            exists = true;
                            break;
                        }
                    }

                    if (exists && (existingBrand.getActive() == isActive)) {
                        response.getWriter().write("dup");
                    } else {
                        existingBrand.setName(brandName);;
                        existingBrand.setActive(isActive);
                        s.update(existingBrand);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DiskFileItemFactory dfif = new DiskFileItemFactory();
            ServletFileUpload sfu = new ServletFileUpload(dfif);
            List<FileItem> fiList = sfu.parseRequest(request);

            int brandID = Integer.parseInt(fiList.get(0).getString());
            String mainWeb = fiList.get(1).getString();
            String supWeb = fiList.get(2).getString();

            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria cr = s.createCriteria(Brand.class);
            List<Brand> brandList = (List<Brand>) cr.list();

            Brand existingBrand = null;
            for (Brand br : brandList) {
                if (br.getId() == brandID) {
                    existingBrand = br;
                    break;
                }
            }
            if (existingBrand == null) {
                response.getWriter().write("err");
            } else {
                existingBrand.setMainWebsite(mainWeb);
                existingBrand.setSupportWebsite(supWeb);
                s.update(existingBrand);
                File f = new File(getServletContext().getRealPath("") + "//assets//img/brands//" + brandID);
                if (fiList.get(3).getString().equals("no")) {
                    if (f.exists()) {
                        f.delete();
                    }
                }
                if (fiList.size() == 5) {
                    FileItem img = fiList.get(4);
                    if (f.exists()) {
                        f.delete();
                    }
                    img.write(f);
                }
                response.getWriter().write("ok");
            }
            Transaction t = s.beginTransaction();
            t.commit();
            s.close();
        } catch (Exception e) {
            response.getWriter().write("err");
            e.printStackTrace();
        }
    }

}
