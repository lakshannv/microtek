package controller;

import com.google.gson.Gson;
import hibernate.City;
import hibernate.Customer;
import hibernate.District;
import hibernate.HiberUtil;
import hibernate.Province;
import hibernate.ShippingAddress;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddNewCustomer", urlPatterns = {"/AddNewCustomer"})
public class AddNewCustomer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ArrayList<String> errList = new ArrayList<>();

        DiskFileItemFactory dfif = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(dfif);
        String fn = null;
        String ln = null;
        String mob = null;
        String eml = null;
        String addr = null;
        int provinceID = 0;
        int districtID = 0;
        int cityID = 0;
        String un = null;
        String pw = null;
        String pwCon = null;
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
                        case "addr":
                            addr = fi.getString();
                            break;
                        case "provinceID":
                            provinceID = Integer.parseInt(fi.getString());
                            break;
                        case "districtID":
                            districtID = Integer.parseInt(fi.getString());
                            break;
                        case "cityID":
                            cityID = Integer.parseInt(fi.getString());
                            break;
                        case "un":
                            un = fi.getString().trim();
                            break;
                        case "pw":
                            pw = fi.getString();
                            break;
                        case "pwCon":
                            pwCon = fi.getString();
                            break;
                    }
                } else {
                    img = fi;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
        if (addr.isEmpty()) {
            errList.add("addr");
        }
        if (un.isEmpty()) {
            errList.add("un");
        }
        if (pw.isEmpty()) {
            errList.add("pw");
        }
        if (pwCon.isEmpty()) {
            errList.add("pwCon");
        }
        if (!(pw.isEmpty() || pwCon.isEmpty())) {
            if (!pw.equals(pwCon)) {
                errList.add("pwmis");
            }
        }

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria custCR1 = s.createCriteria(Customer.class);
        custCR1.add(Restrictions.eq("username", un));
        if (custCR1.list().size() != 0) {
            errList.add("un-dup");
        }
        Criteria custCR2 = s.createCriteria(Customer.class);
        custCR2.add(Restrictions.eq("email", eml));
        if (custCR2.list().size() != 0) {
            errList.add("eml-dup");
        }

        if (errList.isEmpty()) {
            try {

                Criteria provinceCR = s.createCriteria(Province.class);
                provinceCR.add(Restrictions.eq("id", provinceID));
                Province prv = (Province) provinceCR.uniqueResult();

                Criteria distCR = s.createCriteria(District.class);
                distCR.add(Restrictions.eq("id", districtID));
                District dist = (District) distCR.uniqueResult();

                Criteria cityCR = s.createCriteria(City.class);
                cityCR.add(Restrictions.eq("id", cityID));
                City cty = (City) cityCR.uniqueResult();

                Customer c = new Customer();
                c.setFname(fn);
                c.setLname(ln);
                c.setMobile(mob);
                c.setEmail(eml);
                c.setCreatedOn(new Date());
                ShippingAddress shipAddr = new ShippingAddress(cty, dist, prv);
                shipAddr.setName(addr);
                s.save(shipAddr);
                Set<ShippingAddress> addrSet = new LinkedHashSet();
                addrSet.add(shipAddr);
                c.setShippingAddresses(addrSet);
                c.setUsername(un);
                c.setPassword(pw);

                if (img != null) {
                    String fileName = un + "-" + System.currentTimeMillis();
                    c.setImage(fileName);
                    File f = new File(getServletContext().getRealPath("") + "//assets//img/avatars//" + fileName);
                    img.write(f);
                }

                
                s.save(c);
                Transaction t = s.beginTransaction();
                t.commit();
                s.close();
                response.getWriter().write("ok");
            } catch (Exception e) {
                response.getWriter().write("err");
                e.printStackTrace();
            }
        }

        if (!errList.isEmpty()) {
            Gson g = new Gson();
            response.getWriter().write(g.toJson(errList));
        }

    }

}
