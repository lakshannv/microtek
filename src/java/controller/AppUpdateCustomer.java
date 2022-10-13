package controller;

import com.google.gson.Gson;
import hibernate.Customer;
import hibernate.HiberUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppUpdateCustomer", urlPatterns = {"/AppUpdateCustomer"})
public class AppUpdateCustomer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HashMap<String, Object> responeDataMap = new HashMap();
        Gson g = new Gson();

        ArrayList<String> errList = new ArrayList<>();

        DiskFileItemFactory dfif = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(dfif);
        String fn = null;
        String ln = null;
        String mob = null;
        String eml = null;
        String custID = null;
        String hasImage = "false";
        FileItem img = null;
        try {
            List<FileItem> fiList = sfu.parseRequest(request);
            for (FileItem fi : fiList) {
                if (fi.isFormField()) {
                    switch (fi.getFieldName()) {
                        case "fn":
                            fn = fi.getString();
                            break;
                        case "ln":
                            ln = fi.getString();
                            break;
                        case "mob":
                            mob = fi.getString().trim();
                            break;
                        case "eml":
                            eml = fi.getString();
                            break;
                        case "custID":
                            custID = fi.getString();
                            break;
                        case "hasImage":
                            hasImage = fi.getString();
                            break;
                    }
                } else {
                    img = fi;
                }
            }
        } catch (Exception ex) {
            responeDataMap.put("result", "err");
            return;
        }

        if (!Validation.isValidCustomerName(fn)) {
            errList.add("fn");
        }
        if (!Validation.isValidCustomerName(ln)) {
            errList.add("ln");
        }
        try {
            mob = Validation.getValidatedMobile(mob);
        } catch (Exception e) {
            errList.add("mob");
        }
        if (!Validation.isValidEmail(eml)) {
            errList.add("eml");
        }

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        Customer c = (Customer) s.load(Customer.class, Integer.parseInt(custID));

        Criteria custCR2 = s.createCriteria(Customer.class);
        custCR2.add(Restrictions.ne("id", c.getId()));
        custCR2.add(Restrictions.eq("email", eml));
        if (custCR2.list().size() != 0) {
            errList.add("eml-dup");
        }

        if (errList.isEmpty()) {
            try {

                c.setFname(fn);
                c.setLname(ln);
                c.setMobile(mob);
                c.setEmail(eml);

                if (img == null) {
                    if (hasImage.equals("false") && c.getImage() != null) {
                        File f = new File(getServletContext().getRealPath("") + "//assets//img/avatars//" + c.getImage());
                        if (f.exists()) {
                            f.delete();
                        }
                        c.setImage(null);
                    }
                } else {
                    if (c.getImage() != null) {
                        File f = new File(getServletContext().getRealPath("") + "//assets//img/avatars//" + c.getImage());
                        if (f.exists()) {
                            f.delete();
                        }
                    }
                    String fileName = c.getUsername() + "-" + System.currentTimeMillis();
                    c.setImage(fileName);
                    File f = new File(getServletContext().getRealPath("") + "//assets//img/avatars//" + fileName);
                    img.write(f);
                    responeDataMap.put("image", fileName);
                }

                s.update(c);
                s.beginTransaction().commit();
                s.close();
                responeDataMap.put("result", "ok");
            } catch (Exception e) {
                responeDataMap.put("result", "err");
                e.printStackTrace();
            }
        }

        if (!errList.isEmpty()) {
            
            responeDataMap.put("result", "errList");
            responeDataMap.put("errList", errList);

        }
        
        response.getWriter().write(g.toJson(responeDataMap));
    }

}
