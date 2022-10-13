package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hibernate.Brand;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.InvoiceItem;
import hibernate.Product;
import hibernate.ProductHasSpec;
import hibernate.ProductHasSpecId;
import hibernate.ProductImage;
import hibernate.ProductImageId;
import hibernate.Stock;
import hibernate.Wishlist;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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

@WebServlet(name = "AdminAppUpdateProduct", urlPatterns = {"/AdminAppUpdateProduct"})
public class AdminAppUpdateProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HashMap<String, String> responeDataMap = new HashMap();
        Gson g = new Gson();

        ArrayList<String> errList = new ArrayList<>();
        ArrayList<FileItem> imgList = new ArrayList<>();

        DiskFileItemFactory dfif = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(dfif);
        int stockID = 0;
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
        String updateExt = null;
        boolean f1 = false;
        boolean f2 = false;
        boolean f3 = false;
        boolean f4 = false;

        try {
            List<FileItem> fiList = sfu.parseRequest(request);
            for (FileItem fi : fiList) {
                if (fi.isFormField()) {
                    switch (fi.getFieldName()) {
                        case "stockID":
                            stockID = Integer.parseInt(fi.getString());
                            break;
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
                        case "updateExt":
                            updateExt = fi.getString();
                            break;
                        case "f1-img":
                            if (fi.getString().equals("yes")) {
                                f1 = true;
                            }
                            break;
                        case "f2-img":
                            if (fi.getString().equals("yes")) {
                                f2 = true;
                            }
                            break;
                        case "f3-img":
                            if (fi.getString().equals("yes")) {
                                f3 = true;
                            }
                            break;
                        case "f4-img":
                            if (fi.getString().equals("yes")) {
                                f4 = true;
                            }
                            break;
                    }
                } else {
                    imgList.add(fi);
                }
            }
        } catch (Exception ex) {
            responeDataMap.put("response", "err");
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
        try {
            if (errList.isEmpty()) {

                Criteria catCR = s.createCriteria(Category.class);
                catCR.add(Restrictions.eq("name", catName));
                Category existingCat = (Category) catCR.uniqueResult();

                Criteria brandCR = s.createCriteria(Brand.class);
                brandCR.add(Restrictions.eq("name", brandName));
                Brand existingBrand = (Brand) brandCR.uniqueResult();

                Criteria stockCR = s.createCriteria(Stock.class);
                stockCR.add(Restrictions.eq("id", stockID));
                Stock stk = (Stock) stockCR.uniqueResult();

                int previousQty = stk.getQty();

                Product p = stk.getProduct();
                p.setCategory(existingCat);
                p.setBrand(existingBrand);
                p.setName(pName);
                p.setDescription(desc);
                s.update(p);

                TypeToken tt = new TypeToken<HashMap<Integer, String>>() {
                };
                HashMap<Integer, String> specMap = g.fromJson(specData, tt.getType());

                Criteria prodSpecCR = s.createCriteria(ProductHasSpec.class);
                prodSpecCR.add(Restrictions.eq("product", p));
                List<ProductHasSpec> pSpecList = prodSpecCR.list();
                Set<Integer> pSpecIDSet = specMap.keySet();
                for (ProductHasSpec ps : pSpecList) {
                    boolean isSpecExists = false;
                    for (Integer specID : pSpecIDSet) {
                        if (ps.getSpec().getId() == specID) {
                            isSpecExists = true;
                            break;
                        }
                    }
                    if (!isSpecExists) {
                        s.delete(ps);
                    }
                }
                for (Integer specID : pSpecIDSet) {
                    ProductHasSpec existingProductSpec = null;
                    for (ProductHasSpec ps : pSpecList) {
                        if (ps.getSpec().getId() == specID) {
                            existingProductSpec = ps;
                            break;
                        }
                    }
                    if (existingProductSpec == null) {
                        ProductHasSpec prodSpec = new ProductHasSpec();
                        prodSpec.setId(new ProductHasSpecId(p.getId(), specID));
                        prodSpec.setSpecValue(specMap.get(specID));
                        s.save(prodSpec);
                    } else {
                        existingProductSpec.setSpecValue(specMap.get(specID));
                        s.update(existingProductSpec);
                    }
                }

                if (stk.getSellingPrice() == sPrice) {
                    stk.setQty(pQty);
                    stk.setBuyingPrice(bPrice);
                    stk.setDiscount(discount);
                    stk.setWarranty(warranty);
                    s.update(stk);
                } else {
                    if (updateExt == null) {
                        stk = new Stock(stk.getProduct());
                        stk.setQty(pQty);
                        stk.setBuyingPrice(bPrice);
                        stk.setSellingPrice(sPrice);
                        stk.setDiscount(discount);
                        stk.setWarranty(warranty);
                        stk.setCreatedOn(new Date());
                        stk.setActive((byte) 1);
                        s.save(stk);
                    } else {
                        Criteria invCR = s.createCriteria(InvoiceItem.class);
                        invCR.add(Restrictions.eq("stock", stk));

                        stk.setQty(pQty);
                        stk.setBuyingPrice(bPrice);
                        stk.setDiscount(discount);
                        stk.setWarranty(warranty);
                        if (invCR.list().isEmpty()) {
                            stk.setSellingPrice(sPrice);
                        } else {
                            responeDataMap.put("response", "pricefail");
                        }
                        s.update(stk);
                    }
                }

                Criteria prodImgCR = s.createCriteria(ProductImage.class);
                prodImgCR.add(Restrictions.eq("product", p));
                List<ProductImage> pImgList = prodImgCR.list();

                for (ProductImage pi : pImgList) {
                    if (pi.getId().getImage().contains("f1") && (f1 == false)) {
                        File f = new File(getServletContext().getRealPath("") + "//assets//img/products//" + pi.getId().getImage());
                        if (f.exists()) {
                            f.delete();
                        }
                        s.delete(pi);
                    }
                    if (pi.getId().getImage().contains("f2") && (f2 == false)) {
                        File f = new File(getServletContext().getRealPath("") + "//assets//img/products//" + pi.getId().getImage());
                        if (f.exists()) {
                            f.delete();
                        }
                        s.delete(pi);
                    }
                    if (pi.getId().getImage().contains("f3") && (f3 == false)) {
                        File f = new File(getServletContext().getRealPath("") + "//assets//img/products//" + pi.getId().getImage());
                        if (f.exists()) {
                            f.delete();
                        }
                        s.delete(pi);
                    }
                    if (pi.getId().getImage().contains("f4") && (f4 == false)) {
                        File f = new File(getServletContext().getRealPath("") + "//assets//img/products//" + pi.getId().getImage());
                        if (f.exists()) {
                            f.delete();
                        }
                        s.delete(pi);
                    }
                }
                for (FileItem fi : imgList) {
                    for (ProductImage pi : pImgList) {
                        if (pi.getId().getImage().contains(fi.getFieldName())) {
                            File f = new File(getServletContext().getRealPath("") + "//assets//img/products//" + pi.getId().getImage());
                            if (f.exists()) {
                                f.delete();
                            }
                            s.delete(pi);
                        }
                    }

                    String fileName = p.getId() + "-" + fi.getFieldName() + "-" + System.currentTimeMillis();
                    File f = new File(getServletContext().getRealPath("") + "//assets//img/products//" + fileName);
                    fi.write(f);
                    ProductImage pImg = new ProductImage(new ProductImageId(p.getId(), fileName), p);
                    s.save(pImg);
                }

                Transaction t = s.beginTransaction();
                t.commit();

                ArrayList<String> imagList = new ArrayList();
                for (Iterator iterator2 = p.getProductImages().iterator(); iterator2.hasNext();) {
                    ProductImage pi = (ProductImage) iterator2.next();
                    imagList.add(pi.getId().getImage());
                }
                Collections.sort(imagList);

                if (previousQty == 0 && stk.getQty() != 0) {
                    TreeSet<String> fcmSet = new TreeSet();
                    TreeSet<String> custIDSet = new TreeSet();
                    Set<Wishlist> wishlistsSet = p.getWishlists();
                    for (Iterator<Wishlist> iterator = wishlistsSet.iterator(); iterator.hasNext();) {
                        Wishlist w = iterator.next();
                        String fcmToken = w.getCustomer().getFcmToken();
                        if (fcmToken != null) {
                            fcmSet.add(fcmToken);
                            custIDSet.add(String.valueOf(w.getCustomer().getId()));
                        }
                    }
                    responeDataMap.put("fcmSetJSON", g.toJson(fcmSet));
                    responeDataMap.put("custIDSet", g.toJson(custIDSet));
                }

                if (responeDataMap.get(response) == null) {
                    responeDataMap.put("response", "ok");
                } else {
                    responeDataMap.put("response", responeDataMap.get(response) + "ok");
                }
                responeDataMap.put("productImage", Validation.getNextProductImage(imagList, 1));

                s.close();
            } else {
                responeDataMap.put("response", "errList");
                responeDataMap.put("errList", g.toJson(errList));
            }

        } catch (Exception e) {
            responeDataMap.put("response", "err");
            s.close();
            e.printStackTrace();
        }

        response.getWriter().write(g.toJson(responeDataMap));
    }
}
