<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="hibernate.Wishlist"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="hibernate.Customer"%>
<%@page import="java.util.Set"%>
<%@page import="hibernate.ProductReview"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="hibernate.ProductHasSpec"%>
<%@page import="java.util.Collections"%>
<%@page import="model.Validation"%>
<%@page import="hibernate.ProductImage"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashSet"%>
<%@page import="hibernate.Product"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="hibernate.Stock"%>
<%@page import="java.util.List"%>
<%@page contentType="application/json" pageEncoding="UTF-8"%>

<%
    Session s = HiberUtil.getSessionFactory().openSession();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    DecimalFormat df = new DecimalFormat("#,##0.##");
    DecimalFormat dfRating = new DecimalFormat("0.#");
    List<Product> prodList = (List<Product>) request.getAttribute("prodList");
    Customer cust = (Customer) request.getSession().getAttribute("cust");
    List<Integer> wishList = null;
    if (cust != null) {
        Criteria wishCR = s.createCriteria(Wishlist.class);
        wishCR.add(Restrictions.eq("customer", cust));
        wishList = wishCR.list();
        for (Wishlist w : (List<Wishlist>) wishCR.list()) {
            wishList.add(w.getProduct().getId());
        }
    }

    LinkedHashMap<Product, Stock> refinedProdMap = new LinkedHashMap();
    for (Product p : prodList) {
        if (p.getCategory().getActive() == 1 && p.getBrand().getActive() == 1) {
            Stock stk = null;
            for (Iterator iterator = p.getStocks().iterator(); iterator.hasNext();) {
                Stock st = (Stock) iterator.next();
                if (st.getActive() == 1) {
                    if (stk == null) {
                        stk = st;
                    } else if ((st.getSellingPrice() * (100 - st.getDiscount()) / 100) < (stk.getSellingPrice() * (100 - stk.getDiscount()) / 100)) {
                        stk = st;
                    }
                }
            }
            if (stk != null) {
                double sellPrice = stk.getSellingPrice() * (100 - stk.getDiscount()) / 100;
                boolean isWithinPriceRange = true;
                if (request.getAttribute("fromPrice") != null) {
                    double fromPrice = Double.parseDouble((String) request.getAttribute("fromPrice"));
                    if (sellPrice < fromPrice) {
                        isWithinPriceRange = false;
                    }
                }
                if (request.getAttribute("toPrice") != null) {
                    double toPrice = Double.parseDouble((String) request.getAttribute("toPrice"));
                    if (sellPrice > toPrice) {
                        isWithinPriceRange = false;
                    }
                }
                if (isWithinPriceRange) {
                    refinedProdMap.put(p, stk);
                }
            }
        }
    }

    LinkedList<Product> refinedprodList = new LinkedList();
    for (Iterator<Product> it = refinedProdMap.keySet().iterator(); it.hasNext();) {
        Product p = it.next();
        refinedprodList.add(p);
    }

    int pageLimit = Integer.parseInt((String) session.getAttribute("pageLimit"));
    int prodCount = refinedprodList.size();
    int numOfPages;
    if (prodCount % pageLimit == 0) {
        numOfPages = prodCount / pageLimit;
    } else {
        numOfPages = prodCount / pageLimit + 1;
    }
    int pageID = Integer.parseInt((String) request.getAttribute("pageID"));

    int to = pageID + pageLimit;
    if (prodCount < to) {
        to = prodCount;
    }

    List<Product> pSubList = refinedprodList.subList(pageID, to);
    refinedProdMap.keySet().retainAll(pSubList);

    for (Iterator<Product> it = refinedProdMap.keySet().iterator(); it.hasNext();) {
        Product p = it.next();

        ArrayList<String> imgList = new ArrayList();
        TreeMap<Integer, ArrayList<String>> specMap = new TreeMap();

        for (Iterator iterator = p.getProductImages().iterator(); iterator.hasNext();) {
            ProductImage pi = (ProductImage) iterator.next();
            imgList.add(pi.getId().getImage());
        }
        Collections.sort(imgList);
        for (Iterator iterator = p.getProductHasSpecs().iterator(); iterator.hasNext();) {
            ProductHasSpec ps = (ProductHasSpec) iterator.next();
            String spValue = ps.getSpecValue();
            if (ps.getSpec().getIsKey() == 1) {
                if (ps.getSpec().getUnit() != null) {
                    spValue = spValue + " " + ps.getSpec().getUnit();
                }
                ArrayList<String> specData = new ArrayList();
                specData.add(ps.getSpec().getSpecName());
                specData.add(spValue);
                specMap.put(ps.getSpec().getId(), specData);
            }
        }
        String searchURL = response.encodeURL("search.jsp?cat=" + p.getCategory().getName() + "&brand=" + p.getBrand().getName());
        String brandURL = response.encodeURL("search.jsp?brand=" + p.getBrand().getName());
        String prodURL = response.encodeURL("product.jsp?id=" + refinedProdMap.get(p).getId());

        double tot = 0;
        double avgRating = 0;
        Set<ProductReview> prSet = p.getProductReviews();
        for (Iterator<ProductReview> revIT = prSet.iterator(); revIT.hasNext();) {
            ProductReview pr = revIT.next();
            tot += pr.getRating();
        }
        if (!prSet.isEmpty()) {
            avgRating = tot / prSet.size();
        }

        String wishICO = "-o";
        if (wishList != null) {
            if (wishList.contains(p.getId())) {
                wishICO = "";
            }
        }
%>


<div class="blog-slider">
    <div class="blog-slider__wrp swiper-wrapper">
        <div class="blog-slider__item swiper-slide">
            <div class="blog-slider__img" onclick="goToURL('<%= prodURL%>');">
                <img src="<%= Validation.getNextProductImage(imgList, 1)%>">
            </div>
            <div class="blog-slider__content">
                <div class="blog-slider__title"><a href="<%= prodURL%>"><%= p.getBrand().getName()%> <%= p.getName()%></a></div>
                <img class="brand-img" src="assets/img/brands/<%= p.getBrand().getId()%>" onclick="goToURL('<%= brandURL%>');"/>
                <span class="blog-slider__code" style="margin-top: 10px;"><a href="<%= searchURL%>"><%= p.getBrand().getName()%> <%= p.getCategory().getName()%></a></span>
                <div class="blog-slider__text">
                    <table>
                        <tr><td><i class="fas fa-box-open"></i></i>Date Added</td><td><%= sdf.format(refinedProdMap.get(p).getCreatedOn())%></td></tr>
                        <tr><td><i class="fas fa-dolly-flatbed"></i>Available Qty</td><td><%= refinedProdMap.get(p).getQty()%></td></tr>

                        <%
                            String disBtn = "";
                            if (refinedProdMap.get(p).getQty() == 0) {
                                disBtn = " disabled=\"\"";
                            }
                            if (refinedProdMap.get(p).getDiscount() == 0) {
                        %>
                        <tr><td><i class="fas fa-money-check-alt"></i>Price</td><td>Rs. <%= df.format(refinedProdMap.get(p).getSellingPrice())%></td></tr>
                        <%
                        } else {
                        %>
                        <tr><td><i class="fas fa-money-check-alt"></i>Price (<%= df.format(refinedProdMap.get(p).getDiscount())%> % off)</td><td><del>Rs. <%= df.format(refinedProdMap.get(p).getSellingPrice())%> </del>&nbsp;&nbsp;Rs. <%= df.format(refinedProdMap.get(p).getSellingPrice() * (100 - refinedProdMap.get(p).getDiscount()) / 100)%></td></tr>
                        <%
                            }
                        %>
                    </table>
                </div>

                <div class="d-flex flex-column align-items-center align-content-center flex-lg-row" style="margin-top: 15px;">

                    <div class="num-box" id="num-<%= refinedProdMap.get(p).getId()%>" max="<%= refinedProdMap.get(p).getQty()%>">
                        <span class="next" onclick="numberBoxNext('num-<%= refinedProdMap.get(p).getId()%>');"></span>
                        <span class="prev" onclick="numberBoxPrev('num-<%= refinedProdMap.get(p).getId()%>');"></span>
                        <div class="d-flex justify-content-center num-box-val">
                            <span id="val-num-<%= refinedProdMap.get(p).getId()%>">1</span>
                            <span class="sel">Selected</span>
                        </div>
                    </div>

                    <div class="d-flex slider-btn-div">
                        <button class="btn btn-outline-primary" type="button" onclick="addToCart(<%= refinedProdMap.get(p).getId()%>);"<%=disBtn%>><i class="fa fa-cart-plus nav-ico"></i>Add to cart</button>
                        <button class="btn btn-outline-primary" type="button" onclick="buyNow(<%= refinedProdMap.get(p).getId()%>);"<%=disBtn%>><i class="fa fa-flash nav-ico"></i>Buy Now</button></a>
                        <%
                            if (cust != null) {
                        %>
                        <i class="fa fa-heart<%=wishICO%> wish-btn btn wish-p-<%= p.getId()%>" data-toggle="tooltip" data-bs-tooltip="" title="Add to Wishlist" onclick="addToWishlist(<%= p.getId()%>);"></i>
                        <%
                            }
                        %>

                    </div>
                </div>

            </div>
        </div>

        <div class="blog-slider__item swiper-slide">
            <div class="blog-slider__img" onclick="goToURL('<%= prodURL%>');">
                <img src="<%= Validation.getNextProductImage(imgList, 2)%>">
            </div>
            <div class="blog-slider__content">
                <div class="blog-slider__title"><a href="<%= prodURL%>"><%= p.getBrand().getName()%> <%= p.getName()%></a></div>
                <span class="blog-slider__code">About this product</span>
                <span class="rating" style="margin-top: -50px;">
                    <a href="<%= prodURL + "&action=rev"%>">Ratings
                        <%
                            int roundedAvg = (int) avgRating;
                            for (int i = 0; i < roundedAvg; i++) {
                        %>
                        <i class="fas fa-star"></i>
                        <%
                            }

                            double remainder = avgRating - roundedAvg;
                            if (remainder == 0) {
                                for (int i = 0; i < (5 - roundedAvg); i++) {
                        %>
                        <i class="far fa-star"></i>
                        <%
                            }
                        } else {
                            if (remainder <= 0.5) {
                        %>
                        <i class="fas fa-star-half-alt"></i>
                        <%
                        } else {
                        %>
                        <i class="fas fa-star"></i>
                        <%
                            }
                            for (int i = 0; i < (4 - roundedAvg); i++) {
                        %>
                        <i class="far fa-star"></i>
                        <%
                                }
                            }
                        %>
                        <span class="rate-val">( <%= dfRating.format(avgRating)%> / 5 )</span>
                    </a>
                </span>
                <div class="blog-slider__text" style="margin-top: 8px;"><%= p.getDescription()%></div>
            </div>
        </div>

        <div class="blog-slider__item swiper-slide">
            <div class="blog-slider__img" onclick="goToURL('<%= prodURL%>');">
                <img src="<%= Validation.getNextProductImage(imgList, 3)%>">
            </div>
            <div class="blog-slider__content">
                <div class="blog-slider__title"><a href="<%= prodURL%>"><%= p.getBrand().getName()%> <%= p.getName()%></a></div>
                <span class="blog-slider__code">Key Specifications</span>
                <div class="blog-slider__text">
                    <table>
                        <%
                            for (Integer specID : specMap.keySet()) {
                        %>
                        <tr>
                            <td><%= specMap.get(specID).get(0)%></td>
                            <td><%= specMap.get(specID).get(1)%></td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </div>
            </div>
        </div>

        <div class="blog-slider__pagination"></div>
    </div>
</div>

<%
    }
%>
<div class="d-flex justify-content-center align-items-center pagination" resultCount="<%= refinedprodList.size()%>">
    <ul class="list-group list-group-horizontal justify-content-center flex-wrap">
        <%
            int prev = pageID - pageLimit;
            if (prev >= 0) {
        %>
        <li class="list-group-item" onclick="applyAdvancedsearchFilters(<%= prev%>);"><i class="fas fa-backward"></i></li>
            <%
                }
                int v = 0;
                for (int i = 1; i <= numOfPages; i++) {
                    v = pageLimit * (i - 1);
                    String act = "";
                    if (v == pageID) {
                        act = " active";
                    }
            %>
        <li class="list-group-item<%= act%>" onclick="applyAdvancedsearchFilters(<%= v%>);"><span><%= i%></span></li>
                <%
                    }
                    int next = pageID + pageLimit;
                    if (next <= v) {
                %>
        <li class="list-group-item" onclick="applyAdvancedsearchFilters(<%= next%>);"><i class="fa fa-forward"></i></li>
            <%
                }
            %>
    </ul>
</div>
