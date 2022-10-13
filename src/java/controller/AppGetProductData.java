package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import hibernate.ProductHasSpec;
import hibernate.ProductImage;
import hibernate.ProductReview;
import hibernate.Stock;
import hibernate.Wishlist;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderStatus;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppGetProductData", urlPatterns = {"/AppGetProductData"})
public class AppGetProductData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");
    DecimalFormat dfRating = new DecimalFormat("0.#");
    SimpleDateFormat sdf = new SimpleDateFormat("yyy/MM/dd");
    SimpleDateFormat sdft = new SimpleDateFormat("yyy/MM/dd hh:mm a");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HashMap<String, Object> productDataMap = new HashMap();

        LinkedTreeMap<String, Object> myReview = new LinkedTreeMap();
        LinkedList<LinkedTreeMap<String, Object>> reviewList = new LinkedList();

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Gson g = new Gson();

        int stkID = Integer.parseInt(request.getParameter("id"));
        int custID = Integer.parseInt(request.getParameter("custID"));

        Customer cust = (Customer) s.load(Customer.class, custID);
        Stock stk = (Stock) s.load(Stock.class, stkID);
        ArrayList<String> imgList = new ArrayList();
        for (Iterator iterator = stk.getProduct().getProductImages().iterator(); iterator.hasNext();) {
            ProductImage pi = (ProductImage) iterator.next();
            imgList.add(pi.getId().getImage());
        }
        Collections.sort(imgList);

        Criteria revCR = s.createCriteria(ProductReview.class);
        revCR.add(Restrictions.eq("product", stk.getProduct()));
        revCR.addOrder(Order.desc("datetimestamp"));
        List<ProductReview> revList = revCR.list();

        double percent5 = 0;
        double percent4 = 0;
        double percent3 = 0;
        double percent2 = 0;
        double percent1 = 0;

        double percent5Count = 0;
        double percent4Count = 0;
        double percent3Count = 0;
        double percent2Count = 0;
        double percent1Count = 0;

        double avgRating = 0;
        int ratingCount = revList.size();
        double tot = 0;
        if (ratingCount > 0) {
            for (ProductReview pr : revList) {
                tot += pr.getRating();
                if (pr.getRating() == 5) {
                    percent5Count++;
                } else if (pr.getRating() == 4) {
                    percent4Count++;
                } else if (pr.getRating() == 3) {
                    percent3Count++;
                } else if (pr.getRating() == 2) {
                    percent2Count++;
                } else if (pr.getRating() == 1) {
                    percent1Count++;
                }

            }
            avgRating = tot / ratingCount;

            percent5 = percent5Count / ratingCount * 100;
            percent4 = percent4Count / ratingCount * 100;
            percent3 = percent3Count / ratingCount * 100;
            percent2 = percent2Count / ratingCount * 100;
            percent1 = percent1Count / ratingCount * 100;
        }

        boolean allowReview = false;
        ProductReview myRev = null;
        for (ProductReview pr : revList) {
            if (pr.getCustomer().getId() == cust.getId()) {
                myRev = pr;
                break;
            }
        }

        Criteria invCR = s.createCriteria(Invoice.class);
        invCR.add(Restrictions.eq("customer", cust));
        invCR.add(Restrictions.eq("orderStatus", OrderStatus.COMPLETED));
        List<Invoice> invList = invCR.list();

        outerLoop:
        for (Invoice inv : invList) {
            for (Iterator<InvoiceItem> it = inv.getInvoiceItems().iterator(); it.hasNext();) {
                InvoiceItem invItem = it.next();
                if (invItem.getStock().getId() == stk.getId()) {
                    allowReview = true;
                    break outerLoop;
                }
            }
        }
        boolean isWishlisted = false;
        if (cust != null) {
            Criteria wishCR = s.createCriteria(Wishlist.class);
            wishCR.add(Restrictions.eq("product", stk.getProduct()));
            wishCR.add(Restrictions.eq("customer", cust));
            if (wishCR.uniqueResult() != null) {
                isWishlisted = true;
            }
        }

        TreeMap<Integer, LinkedList<String>> specMap = new TreeMap();
        for (Iterator iterator = stk.getProduct().getProductHasSpecs().iterator(); iterator.hasNext();) {
            ProductHasSpec ps = (ProductHasSpec) iterator.next();
            String spValue = ps.getSpecValue();
            if (ps.getSpec().getUnit() != null) {
                spValue = spValue + " " + ps.getSpec().getUnit();
            }
            LinkedList<String> specData = new LinkedList();
            specData.add(ps.getSpec().getSpecName());
            specData.add(spValue);
            specMap.put(ps.getSpec().getId(), specData);
        }

        for (ProductReview pr : revList) {
            if (pr != myRev) {
                LinkedTreeMap<String, Object> rev = new LinkedTreeMap();
                rev.put("customerName", pr.getCustomer().getFname() + " " + pr.getCustomer().getLname());
                rev.put("customerImage", pr.getCustomer().getImage());
                rev.put("rating", pr.getRating());
                rev.put("timeStamp", sdft.format(pr.getDatetimestamp()));
                rev.put("review", pr.getContent());
                reviewList.add(rev);
            }
        }
        
        boolean isActive = false;
        if (stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1 && stk.getActive() == 1) {
            isActive = true;
        }

        productDataMap.put("id", stkID);
        productDataMap.put("productID", String.valueOf(stk.getProduct().getId()));
        productDataMap.put("isActive", isActive);
        productDataMap.put("imgList", imgList);
        productDataMap.put("name", stk.getProduct().getBrand().getName() + " " + stk.getProduct().getName());
        productDataMap.put("brandID", String.valueOf(stk.getProduct().getBrand().getId()));
        productDataMap.put("addedOn", sdf.format(stk.getCreatedOn()));
        productDataMap.put("stocksLeft", String.valueOf(stk.getQty()));
        productDataMap.put("price", stk.getSellingPrice());
        productDataMap.put("discount", stk.getDiscount());
        productDataMap.put("discountedPrice", (stk.getSellingPrice() * (100 - stk.getDiscount()) / 100));
        productDataMap.put("desc", stk.getProduct().getDescription());
        productDataMap.put("warranty", stk.getWarranty());
        productDataMap.put("mainWeb", stk.getProduct().getBrand().getMainWebsite());
        productDataMap.put("supportWeb", stk.getProduct().getBrand().getSupportWebsite());
        productDataMap.put("isWishlisted", isWishlisted);
        productDataMap.put("rating", avgRating);
        productDataMap.put("ratingCount", ratingCount);
        productDataMap.put("percent5", percent5);
        productDataMap.put("percent4", percent4);
        productDataMap.put("percent3", percent3);
        productDataMap.put("percent2", percent2);
        productDataMap.put("percent1", percent1);
        productDataMap.put("allowReview", allowReview);

        productDataMap.put("specMap", specMap);

        if (myRev != null) {
            myReview.put("customerName", myRev.getCustomer().getFname() + " " + myRev.getCustomer().getLname());
            myReview.put("customerImage", myRev.getCustomer().getImage());
            myReview.put("rating", myRev.getRating());
            myReview.put("timeStamp", sdft.format(myRev.getDatetimestamp()));
            myReview.put("review", myRev.getContent());
            productDataMap.put("myReview", myReview);
        }
        productDataMap.put("reviewList", reviewList);

        response.getWriter().write(g.toJson(productDataMap));
        s.close();
    }

}
