package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hibernate.Brand;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductHasSpec;
import hibernate.ProductHasSpecId;
import hibernate.ProductImage;
import hibernate.ProductImageId;
import hibernate.Spec;
import hibernate.Stock;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddNewProduct", urlPatterns = {"/AddNewProduct"})
public class AddNewProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ArrayList<String> errList = new ArrayList<>();
        ArrayList<FileItem> imgList = new ArrayList<>();

        DiskFileItemFactory dfif = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(dfif);
        String catName = null;
        String brandName = null;
        String pName = null;
        String qty = null;
        String buyPrice = null;
        String sellPrice = null;
        String dscnt = null;
        String warranty = null;
        String desc = null;
        String specData = null;
        try {
            List<FileItem> fiList = sfu.parseRequest(request);
            for (FileItem fi : fiList) {
                if (fi.isFormField()) {
                    switch (fi.getFieldName()) {
                        case "catName":
                            catName = fi.getString();
                            break;
                        case "brandName":
                            brandName = fi.getString();
                            break;
                        case "pName":
                            pName = fi.getString().trim();
                            break;
                        case "pQty":
                            qty = fi.getString();
                            break;
                        case "bPrice":
                            buyPrice = fi.getString();
                            break;
                        case "sPrice":
                            sellPrice = fi.getString();
                            break;
                        case "discount":
                            dscnt = fi.getString();
                            break;
                        case "warranty":
                            warranty = fi.getString().trim();
                            break;
                        case "desc":
                            desc = fi.getString().trim();
                            break;
                        case "specList":
                            specData = fi.getString();
                            break;
                    }
                } else {
                    imgList.add(fi);
                }
            }
        } catch (Exception ex) {
            response.getWriter().write("err");
            ex.printStackTrace();
        }

        int pQty = 0;
        double bPrice = 0;
        double sPrice = 0;
        double discount = 0;
        try {
            pQty = Integer.parseInt(qty);
        } catch (Exception e) {
            errList.add("qty");
        }
        try {
            sPrice = Double.parseDouble(sellPrice);
        } catch (Exception e) {
            errList.add("sp");
        }
        try {
            bPrice = Double.parseDouble(buyPrice);
        } catch (Exception e) {
            errList.add("bp");
        }
        if (!(errList.contains("sp") || errList.contains("bp"))) {
            if (bPrice > sPrice) {
                errList.add("bpmax");
            }
        }
        try {
            if (!dscnt.isEmpty()) {
                discount = Double.parseDouble(dscnt);
                if (discount > 100) {
                    errList.add("disc");
                }
            }
        } catch (Exception e) {
            errList.add("disc");
        }

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        ArrayList<String> imageNameList = new ArrayList<>();

        Criteria catCR = s.createCriteria(Category.class);
        catCR.add(Restrictions.eq("name", catName));
        Category existingCat = (Category) catCR.uniqueResult();

        Criteria brandCR = s.createCriteria(Brand.class);
        brandCR.add(Restrictions.eq("name", brandName));
        Brand existingBrand = (Brand) brandCR.uniqueResult();

        Criteria productCR = s.createCriteria(Product.class);
        productCR.add(Restrictions.eq("category", existingCat));
        productCR.add(Restrictions.eq("brand", existingBrand));
        productCR.add(Restrictions.eq("name", pName));

        Gson g = new Gson();
        if (productCR.list().size() == 0) {
            if (errList.isEmpty()) {
                try {
                    Product p = new Product(existingBrand, existingCat);
                    p.setName(pName);
                    p.setDescription(desc);
                    s.save(p);

                    Stock stk = new Stock(p);
                    stk.setQty(pQty);
                    stk.setBuyingPrice(bPrice);
                    stk.setSellingPrice(sPrice);
                    stk.setDiscount(discount);
                    stk.setWarranty(warranty);
                    stk.setCreatedOn(new Date());
                    stk.setActive((byte) 1);
                    s.save(stk);

                    TypeToken tt = new TypeToken<HashMap<Integer, String>>() {
                    };
                    HashMap<Integer, String> specMap = g.fromJson(specData, tt.getType());

                    for (Integer specID : specMap.keySet()) {
                        Criteria specCR = s.createCriteria(Spec.class);
                        specCR.add(Restrictions.eq("id", specID));
                        Spec sp = (Spec) specCR.uniqueResult();

                        ProductHasSpec prodSpec = new ProductHasSpec();
                        prodSpec.setId(new ProductHasSpecId(p.getId(), specID));
                        prodSpec.setProduct(p);
                        prodSpec.setSpec(sp);
                        prodSpec.setSpecValue(specMap.get(specID));
                        s.save(prodSpec);
                    }
                    
                    
                    for (FileItem fi : imgList) {
                        String fileName = p.getId() + "-" + fi.getFieldName() + "-" + System.currentTimeMillis();
                        imageNameList.add(fileName);
                        File f = new File(getServletContext().getRealPath("") + "//assets//img/products//" + fileName);
                        fi.write(f);
                        ProductImage pi = new ProductImage(new ProductImageId(p.getId(), fileName), p);
                        s.save(pi);
                    }
                    String resp = "ok-";
                    if (!imageNameList.isEmpty()) {
                        Collections.sort(imageNameList);
                        resp = resp + imageNameList.get(0);
                    }
                    response.getWriter().write(resp);

                    Transaction t = s.beginTransaction();
                    t.commit();
                    s.close();
                } catch (Exception e) {
                    response.getWriter().write("err");
                    e.printStackTrace();
                }
            }
        } else {
            errList.add("dup");
        }

        if (!errList.isEmpty()) {
            response.getWriter().write(g.toJson(errList));
        }

    }

}
