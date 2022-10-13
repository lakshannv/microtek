<%@page import="hibernate.ApplicationSetting"%>
<%@page import="hibernate.Wishlist"%>
<%@page import="hibernate.Customer"%>
<%@page import="hibernate.Product"%>
<%@page import="org.hibernate.criterion.Projections"%>
<%@page import="org.hibernate.criterion.ProjectionList"%>
<%@page import="hibernate.Category"%>
<%@page import="java.util.Collections"%>
<%@page import="hibernate.ProductImage"%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.Validation"%>
<%@page import="java.util.Iterator"%>
<%@page import="hibernate.ProductReview"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="hibernate.Stock"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DecimalFormat df = new DecimalFormat("#,##0.##");
    DecimalFormat dfRating = new DecimalFormat("0.#");

    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();

    Customer cust = (Customer) request.getSession().getAttribute("cust");
    List<Product> wishList = null;
    if (cust != null) {
        Criteria wishCR = s.createCriteria(Wishlist.class);
        wishCR.add(Restrictions.eq("customer", cust));
        wishList = wishCR.list();
        for (Wishlist w : (List<Wishlist>) wishCR.list()) {
            wishList.add(w.getProduct());
        }
    }

    Criteria stocktCR1 = s.createCriteria(Stock.class);
    stocktCR1.add(Restrictions.eq("active", (byte) 1));
    stocktCR1.addOrder(Order.desc("createdOn"));
    stocktCR1.setMaxResults(6);
    List<Stock> featuredList = stocktCR1.list();

    Criteria stocktCR2 = s.createCriteria(Stock.class);
    stocktCR2.add(Restrictions.eq("active", (byte) 1));
    stocktCR2.add(Restrictions.ne("discount", 0d));
    stocktCR2.addOrder(Order.desc("discount"));
    stocktCR2.setMaxResults(6);
    List<Stock> discountedList = stocktCR2.list();

    Criteria stockCR = s.createCriteria(Stock.class, "StockTable");
    stockCR.add(Restrictions.eq("active", (byte) 1));
    stockCR.createCriteria("StockTable.product", "ProductTable");
    ProjectionList projectionList = Projections.projectionList();
    projectionList.add(Projections.groupProperty("ProductTable.category"));
    projectionList.add(Projections.alias(Projections.count("ProductTable.category"), "CatCount"));
    stockCR.setProjection(projectionList);
    stockCR.addOrder(Order.desc("CatCount"));

    List<Object[]> result = stockCR.list();

    String noticeContent = "";
    String hideNotice = " style=\"display: none;\"";
    ApplicationSetting as = (ApplicationSetting) s.load(ApplicationSetting.class, "notice_message");
    if (as.getValue() != null) {
        if (!as.getValue().isEmpty()) {
            noticeContent = as.getValue();
            hideNotice = "";
        }
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="incl_header.jsp" />
        <title>MicroTek</title>
    </head>
    <body onload="loadCart(); loadWishlist();" style="background-image: url('assets/img/bgs/search.jpg');">
        <jsp:include page="incl_msgbox.jsp" />
        <jsp:include page="incl_navbar.jsp" />

        <div class="container" style="margin-top: 80px;">

            <div class="flash animated" id="notice"<%=hideNotice%>>
                <div class="d-flex justify-content-center align-items-center align-content-center align-self-center m-auto"><span class="text-center"><i class="fas fa-info-circle" style="padding-right: 6px;"></i><%=noticeContent%></span></div>
            </div>

            <div class="slideshow">
                <div class="slider">
                    <div class="item" style="background-image: linear-gradient(to top, rgba(0,0,0,0.7), rgba(0,0,0,0)), url('assets/img/slidebgs/slidebg1.jpg');background-position: center;background-size: cover;">
                        <div class="row justify-content-center align-items-center">
                            <div class="col-4 col-md-auto col-lg-auto"><img src="assets/img/Logo.png"></div>
                            <div class="col-auto d-flex flex-column align-self-center">
                                <h1 class="slider-title">MicroTek Computer Solutions</h1>
                                <h3 class="text-left slider-sub-title">Computer Hardware, Accessories &amp; Solutions</h3>
                            </div>
                        </div>
                        <div class="slider-para-div">
                            <p>Here for Your Essential Communication and Information Technology Needs...<br></p>
                            <p>MicroTek is built specially for IT professionals, small businesses, local governments, students, engineers, programmers, makers, tech enthusiasts, gamers, computer product and electronic device customers in Sri Lanka to have access to to the latest and greatest technology the field has to offer.<br></p>
                            <div class="d-flex search-box"><input onkeydown="triggerSearch();" id="search-box" type="search" placeholder="What are you looking for?"><i class="fas fa-search" onclick="searchProducts();"></i></div>
                        </div>
                    </div>
                    <div class="item" style="background-image: linear-gradient(to top, rgba(0,0,0,0.88), rgba(101,0,0,0.47)), url('assets/img/slidebgs/slidebg2.png');background-size: cover;background-repeat: no-repeat;background-position: center;">
                        <h1 class="del-title">Enjoy Free Delivery Island-Wide !</h1>
                        <p>It doesn’t get any better than this – FREE and FAST delivery island-wide !<br>We use a trusted and exceptional courier service to make sure your order is received safe and on time.</p><img class="justify-content-xl-center align-items-xl-center del-logo" src="assets/img/delivery-logo.png">
                        <p>However, if you choose not to get held up in the delivery queue, you can always choose Expedited Delivery Option.<br>That way, your order will be our highest priority.</p>
                    </div>
                    <div class="item" style="background-image: url('assets/img/slidebgs/slidebg3.jpg');background-size: cover;background-repeat: no-repeat;background-position: center;">
                        <div class="vcenter">
                            <h1 class="del-title">Lowest Prices for Newest Tech !</h1>
                            <p>We take pride in our vast collection of items that let's you choose the exact Product you want.<br>Here at MicroTek, the latest international releases of PC hardware &amp; other&nbsp;<strong>accessories will be available for you to choose from, as soon as possible.</strong></p>
                            <a href="search.jsp"><button class="btn btn-outline-primary" type="button"><i class="fab fa-searchengin nav-ico"></i>Start Browsing ...</button></a>
                        </div>
                    </div>
                </div>
            </div>
            <h2 class="home-heading" data-aos="slide-up"><i class="fas fa-fire-alt"></i>Top Categories</h2>
            <div class="row row-cols-2 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 row-cols-xl-4 home-card-group">
                <%
                    for (Object[] o : result) {
                        Category c = (Category) o[0];
                        if (c.getActive() == 1) {
                            Product p = null;
                            Criteria prodCR = s.createCriteria(Product.class);
                            prodCR.add(Restrictions.eq("category", c));
                            prodCR.addOrder(Order.desc("id"));
                            for (Product prod : (List<Product>) prodCR.list()) {
                                if (prod.getBrand().getActive() == 1) {
                                    p = prod;
                                    break;
                                }
                            }
                            if (p != null) {
                                ArrayList<String> imgList = new ArrayList();
                                for (Iterator iterator = p.getProductImages().iterator(); iterator.hasNext();) {
                                    ProductImage pi = (ProductImage) iterator.next();
                                    imgList.add(pi.getId().getImage());
                                }
                                Collections.sort(imgList);
                                String catURL = response.encodeURL("search.jsp?cat=" + c.getName());
                %>
                <div class="col" data-aos="zoom-in-up" data-aos-duration="600">
                    <div class="card">
                        <div class="card-body">
                            <div><a href="<%=catURL%>"><img class="prod-img" src="<%= Validation.getNextProductImage(imgList, 1)%>"></a></div>
                            <a class="card-link" href="<%=catURL%>">
                                <div class="d-flex justify-content-center home-cat"><span class="text-center"><%=c.getName()%></span></div>
                            </a>
                        </div>
                    </div>
                </div>
                <%
                            }
                        }
                    }
                %>
            </div>

            <h2 class="home-heading" data-aos="slide-up"><i class="fab fa-hotjar"></i>New Arrivals</h2>
            <div class="row row-cols-1 row-cols-sm-1 row-cols-md-2 row-cols-lg-2 row-cols-xl-3 home-card-group">
                <%                    for (Stock stk : featuredList) {
                        if (stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1) {
                            String isOutOfStock = "";
                            if (stk.getQty() == 0) {
                                isOutOfStock = " disabled=\"\"";
                            }

                            String wishICO = "-o";
                            if (wishList != null) {
                                if (wishList.contains(stk.getProduct())) {
                                    wishICO = "";
                                }
                            }

                            String catURL = response.encodeURL("search.jsp?cat=" + stk.getProduct().getCategory().getName());
                            String brandURL = response.encodeURL("search.jsp?brand=" + stk.getProduct().getBrand().getName());
                            String prodURL = response.encodeURL("product.jsp?id=" + stk.getId());

                            ArrayList<String> imgList = new ArrayList();

                            for (Iterator iterator = stk.getProduct().getProductImages().iterator(); iterator.hasNext();) {
                                ProductImage pi = (ProductImage) iterator.next();
                                imgList.add(pi.getId().getImage());
                            }
                            Collections.sort(imgList);

                            double avgRating = 0;
                            double tot = 0;
                            Set<ProductReview> prSet = stk.getProduct().getProductReviews();
                            for (Iterator<ProductReview> revIT = prSet.iterator(); revIT.hasNext();) {
                                ProductReview pr = revIT.next();
                                tot += pr.getRating();
                            }
                            if (!prSet.isEmpty()) {
                                avgRating = tot / prSet.size();
                            }
                %>
                <div class="col" data-aos="zoom-in-up" data-aos-duration="600">
                    <div class="card">
                        <div class="card-body">
                            <h4 class="text-center card-title"><a href="<%=prodURL%>"><%=stk.getProduct().getBrand().getName()%> <%=stk.getProduct().getName()%><br></a></h4>
                            <div class="d-flex align-items-center">
                                <a class="brand-img-link" href="<%=brandURL%>"><img class="brand-img" src="assets/img/brands/<%= stk.getProduct().getBrand().getId()%>"></a>
                                <a class="d-flex justify-content-center align-items-center prod-cat" href="<%=catURL%>"><%=stk.getProduct().getCategory().getName()%></a>
                            </div>
                            <div class="img-div"><a href="<%=prodURL%>"><img class="prod-img" src="<%= Validation.getNextProductImage(imgList, 1)%>"></a></div>
                            <div class="prod-info">
                                <div class="d-flex flex-column justify-content-center align-items-center">

                                    <span class="rating">
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
                                    <%
                                        if (stk.getQty() == 0) {
                                    %>
                                    <span class="qty"><i class="fas fa-dolly-flatbed"></i>Out of Stock.</span>
                                    <%
                                    } else {
                                    %>
                                    <span class="qty"><i class="fas fa-dolly-flatbed"></i><%= stk.getQty()%> left in stock.</span>
                                    <%
                                        }
                                    %>
                                    <span class="price-tag"><i class="fas fa-money-check-alt"></i>

                                        <%
                                            if (stk.getDiscount() == 0) {
                                        %>
                                        Rs. <%= df.format(stk.getSellingPrice())%>
                                        <%
                                        } else {
                                        %>
                                        <del>Rs. <%= df.format(stk.getSellingPrice())%> </del>&nbsp;&nbsp;Rs. <%= df.format(stk.getSellingPrice() * (100 - stk.getDiscount()) / 100)%> (<%= df.format(stk.getDiscount())%> % off)
                                        <%
                                            }
                                        %>

                                    </span>
                                </div>

                                <div class="d-flex justify-content-center">
                                    <div class="num-box" id="num-<%= stk.getId()%>" max="<%= stk.getQty()%>">
                                        <span class="next" onclick="numberBoxNext('num-<%= stk.getId()%>');"></span>
                                        <span class="prev" onclick="numberBoxPrev('num-<%= stk.getId()%>');"></span>
                                        <div class="d-flex justify-content-center num-box-val">
                                            <span id="val-num-<%= stk.getId()%>">1</span>
                                            <span class="sel">Selected</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="d-flex justify-content-between btn-div">
                                <button class="btn btn-outline-primary flex-fill" type="button" onclick="addToCart(<%= stk.getId()%>)"<%=isOutOfStock%>><i class="fa fa-cart-plus nav-ico"></i>Add to cart</button>
                                <button class="btn btn-outline-primary flex-fill" type="button" onclick="buyNow(<%= stk.getId()%>);"<%=isOutOfStock%>><i class="fa fa-flash nav-ico"></i>Buy Now</button>
                                <%
                                    if (cust != null) {
                                %>
                                <i class="fa fa-heart<%=wishICO%> wish-btn btn wish-p-<%= stk.getProduct().getId()%>" data-toggle="tooltip" data-bs-tooltip="" title="Add to Wishlist" onclick="addToWishlist(<%= stk.getProduct().getId()%>);"></i>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                        }
                    }
                %>
            </div>


            <h2 class="home-heading" data-aos="slide-up"><i class="fas fa-fire-alt"></i>Hot Deals</h2>
            <div class="row row-cols-1 row-cols-sm-1 row-cols-md-2 row-cols-lg-2 row-cols-xl-3 home-card-group">

                <%
                    for (Stock stk : discountedList) {
                        if (stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1) {
                            String isOutOfStock = "";
                            if (stk.getQty() == 0) {
                                isOutOfStock = " disabled=\"\"";
                            }

                            String wishICO = "-o";
                            if (wishList != null) {
                                if (wishList.contains(stk.getProduct())) {
                                    wishICO = "";
                                }
                            }

                            String catURL = response.encodeURL("search.jsp?cat=" + stk.getProduct().getCategory().getName());
                            String brandURL = response.encodeURL("search.jsp?brand=" + stk.getProduct().getBrand().getName());
                            String prodURL = response.encodeURL("product.jsp?id=" + stk.getId());

                            ArrayList<String> imgList = new ArrayList();

                            for (Iterator iterator = stk.getProduct().getProductImages().iterator(); iterator.hasNext();) {
                                ProductImage pi = (ProductImage) iterator.next();
                                imgList.add(pi.getId().getImage());
                            }
                            Collections.sort(imgList);

                            double avgRating = 0;
                            double tot = 0;
                            Set<ProductReview> prSet = stk.getProduct().getProductReviews();
                            for (Iterator<ProductReview> revIT = prSet.iterator(); revIT.hasNext();) {
                                ProductReview pr = revIT.next();
                                tot += pr.getRating();
                            }
                            if (!prSet.isEmpty()) {
                                avgRating = tot / prSet.size();
                            }
                            double discountedPrice = stk.getSellingPrice() * (100 - stk.getDiscount()) / 100;
                %>
                <div class="col" data-aos="zoom-in-up" data-aos-duration="600">
                    <div class="card">
                        <div class="card-body">
                            <h4 class="text-center card-title"><a href="<%=prodURL%>"><%=stk.getProduct().getBrand().getName()%> <%=stk.getProduct().getName()%><br></a></h4>
                            <div class="d-flex align-items-center">
                                <a class="brand-img-link" href="<%=brandURL%>"><img class="brand-img" src="assets/img/brands/<%= stk.getProduct().getBrand().getId()%>"></a>
                                <a class="d-flex justify-content-center align-items-center prod-cat" href="<%=catURL%>"><%=stk.getProduct().getCategory().getName()%></a>
                            </div>
                            <div class="img-div"><a href="<%=prodURL%>"><img class="prod-img" src="<%= Validation.getNextProductImage(imgList, 1)%>"><span class="disc-label"><%=df.format(stk.getDiscount())%> % off</span><span class="save-label">You Save Rs. <%=df.format(stk.getSellingPrice() - discountedPrice)%></span></a></div>
                            <div class="prod-info">
                                <div class="d-flex flex-column justify-content-center align-items-center">
                                    <span class="rating">
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
                                    <%
                                        if (stk.getQty() == 0) {
                                    %>
                                    <span class="qty"><i class="fas fa-dolly-flatbed"></i>Out of Stock.</span>
                                    <%
                                    } else {
                                    %>
                                    <span class="qty"><i class="fas fa-dolly-flatbed"></i><%= stk.getQty()%> left in stock.</span>
                                    <%
                                        }
                                    %>
                                    <span class="price-tag"><i class="fas fa-money-check-alt"></i>

                                        <%
                                            if (stk.getDiscount() == 0) {
                                        %>
                                        Rs. <%= df.format(stk.getSellingPrice())%>
                                        <%
                                        } else {
                                        %>
                                        <del>Rs. <%= df.format(stk.getSellingPrice())%> </del>&nbsp;&nbsp;Rs. <%= df.format(discountedPrice)%> (<%= df.format(stk.getDiscount())%> % off)
                                        <%
                                            }
                                        %>

                                    </span>
                                </div>

                                <div class="d-flex justify-content-center">
                                    <div class="num-box" id="dis-num-<%= stk.getId()%>" max="<%= stk.getQty()%>">
                                        <span class="next" onclick="numberBoxNext('dis-num-<%= stk.getId()%>');"></span>
                                        <span class="prev" onclick="numberBoxPrev('dis-num-<%= stk.getId()%>');"></span>
                                        <div class="d-flex justify-content-center num-box-val">
                                            <span id="val-dis-num-<%= stk.getId()%>">1</span>
                                            <span class="sel">Selected</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="d-flex justify-content-between btn-div">
                                <button class="btn btn-outline-primary flex-fill" type="button" onclick="addDisToCart(<%= stk.getId()%>)"<%=isOutOfStock%>><i class="fa fa-cart-plus nav-ico"></i>Add to cart</button>
                                <button class="btn btn-outline-primary flex-fill" type="button" onclick="buyDisNow(<%= stk.getId()%>);"<%=isOutOfStock%>><i class="fa fa-flash nav-ico"></i>Buy Now</button>
                                <%
                                    if (cust != null) {
                                %>
                                <i class="fa fa-heart<%=wishICO%> wish-btn btn wish-p-<%= stk.getProduct().getId()%>" data-toggle="tooltip" data-bs-tooltip="" title="Add to Wishlist" onclick="addToWishlist(<%= stk.getProduct().getId()%>);"></i>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                        }
                    }
                %>

            </div>
        </div>

        <jsp:include page="incl_footer.jsp"/>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/autocomplete.js"></script>
        <script src="assets/js/Product-Viewer-1.js"></script>
        <script src="assets/js/Product-Viewer.js"></script>
        <script src="assets/js/Slick-Slider.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/Swiper-Slider.js"></script>
        <script src="assets/js/index.js"></script>
    </body>
</html>
<%
    s.close();
%>