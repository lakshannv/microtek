<%@page import="hibernate.Wishlist"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.Set"%>
<%@page import="hibernate.Product"%>
<%@page import="model.OrderStatus"%>
<%@page import="hibernate.Invoice"%>
<%@page import="hibernate.InvoiceItem"%>
<%@page import="hibernate.Customer"%>
<%@page import="java.util.List"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="hibernate.ProductReview"%>
<%@page import="hibernate.ProductHasSpec"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Collections"%>
<%@page import="model.Validation"%>
<%@page import="hibernate.ProductImage"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="hibernate.Stock"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DecimalFormat df = new DecimalFormat("#,##0.##");
    DecimalFormat dfRating = new DecimalFormat("0.#");
    SimpleDateFormat sdf = new SimpleDateFormat("yyy/MM/dd");
    SimpleDateFormat sdft = new SimpleDateFormat("yyy/MM/dd hh:mm a");
    Customer cust = null;
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    try {
        Customer sesCust = (Customer) session.getAttribute("cust");
        if (sesCust != null) {
            cust = (Customer) s.load(Customer.class, sesCust.getId());
        }
        int stkID = Integer.parseInt(request.getParameter("id"));
        Stock stk = (Stock) s.load(Stock.class, stkID);
        if (stk.getActive() == 1 && stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1) {
            ArrayList<String> imgList = new ArrayList();
            for (Iterator iterator = stk.getProduct().getProductImages().iterator(); iterator.hasNext();) {
                ProductImage pi = (ProductImage) iterator.next();
                imgList.add(pi.getId().getImage());
            }
            Collections.sort(imgList);
            
            TreeMap<Integer, ArrayList<String>> specMap = new TreeMap();
            for (Iterator iterator = stk.getProduct().getProductHasSpecs().iterator(); iterator.hasNext();) {
                ProductHasSpec ps = (ProductHasSpec) iterator.next();
                String spValue = ps.getSpecValue();
                if (ps.getSpec().getUnit() != null) {
                    spValue = spValue + " " + ps.getSpec().getUnit();
                }
                ArrayList<String> specData = new ArrayList();
                specData.add(ps.getSpec().getSpecName());
                specData.add(spValue);
                specMap.put(ps.getSpec().getId(), specData);
            }
            
            String wishICO = "-o";
            if (cust != null) {
                Criteria wishCR = s.createCriteria(Wishlist.class);
                wishCR.add(Restrictions.eq("product", stk.getProduct()));
                wishCR.add(Restrictions.eq("customer", cust));
                if (wishCR.uniqueResult() != null) {
                    wishICO = "";
                }
            }
            
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
            if (cust != null) {
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
            }
            String loadMethods = "";
            if (myRev != null) {
                loadMethods = " setRating(" + myRev.getRating() + ");";
            }
            String action = request.getParameter("action");
            if (action != null) {
                if (action.equals("rev")) {
                    loadMethods += " document.getElementById('rev-tab').click();";
                }
            }

%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Product Details</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: linear-gradient(rgba(0,0,0,0.1), rgba(0,0,0,0.6)), url('assets/img/bgs/product.jpg');" onload="loadCart(); loadWishlist();<%= loadMethods%>">
        <jsp:include page="incl_msgbox.jsp" />

        <jsp:include page="incl_navbar.jsp" />
        <div class="container" style="margin-top: 80px;">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="search.jsp"><span>Home</span></a></li>
                <li class="breadcrumb-item"><a href="search.jsp?cat=<%= stk.getProduct().getCategory().getName()%>"><span><%= stk.getProduct().getCategory().getName()%></span></a></li>
                <li class="breadcrumb-item"><a href="search.jsp?brand=<%= stk.getProduct().getBrand().getName()%>"><span><%= stk.getProduct().getBrand().getName()%></span></a></li>
                <li class="breadcrumb-item active"><a href="#"><span><strong><%= stk.getProduct().getName()%></strong><br></span></a></li>
            </ol>
            <div data-aos="zoom-in-up" class="product-view-div">
                <div class="row">
                    <div class="col-sm-12 col-md-12 col-lg-5 col-xl-5">
                        <div class="product-image">
                            <div class="image img-fluid" style="background-image: url('<%= Validation.getNextProductImage(imgList, 1)%>');"></div>
                        </div>
                        <div class="d-flex justify-content-center product-thumbnails">
                            <%
                                if (imgList.isEmpty()) {
                            %>
                            <img src="<%= Validation.getNextProductImage(imgList, 1)%>">
                            <%
                            } else {
                                for (int i = 1; i <= imgList.size(); i++) {
                            %>
                            <img src="<%= Validation.getNextProductImage(imgList, i)%>">
                            <%
                                    }
                                }
                            %>
                        </div>
                    </div>
                    <div class="col product-details">
                        <h3 data-aos="zoom-in-up" data-aos-delay="100"><%= stk.getProduct().getBrand().getName()%> <%= stk.getProduct().getName()%></h3>
                        <a href="search.jsp?brand=<%= stk.getProduct().getBrand().getName()%>"><img data-aos="zoom-in-up" data-aos-delay="150" class="brand-img" src="assets/img/brands/<%= stk.getProduct().getBrand().getId()%>"></a>
                        <div class="prod-desc">
                            <div class="d-flex flex-column prod-det">
                                <span data-aos="zoom-in-up" data-aos-delay="200" class="adddate"><i class="fas fa-box-open"></i>Added on <%= sdf.format(stk.getCreatedOn())%></span>
                                <span data-aos="zoom-in-up" data-aos-delay="250" class="rating">Ratings
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
                                </span>
                                <span data-aos="zoom-in-up" data-aos-delay="300" class="qty"><i class="fas fa-dolly-flatbed"></i><%= stk.getQty()%> left in stock.</span>
                                <%
                                    if (stk.getDiscount() == 0) {
                                %>
                                <span data-aos="zoom-in-up" data-aos-delay="350" class="price-tag"><i class="fas fa-money-check-alt"></i>LKR <%= df.format(stk.getSellingPrice())%></span>
                                <%
                                } else {
                                %>
                                <span data-aos="zoom-in-up" data-aos-delay="350" class="price-tag"><i class="fas fa-money-check-alt"></i><del>LKR <%= df.format(stk.getSellingPrice())%> </del>&nbsp;&nbsp;LKR <%= df.format(stk.getSellingPrice() * (100 - stk.getDiscount()) / 100)%> ( <%= df.format(stk.getDiscount())%> % off )</span>
                                        <%
                                            }
                                            String disBtn = "";
                                            if (stk.getQty() == 0) {
                                                disBtn = " disabled=\"\"";
                                            }
                                        %>
                            </div>
                            <p data-aos="zoom-in-up" data-aos-delay="400"><%= stk.getProduct().getDescription()%><br></p>
                            <div class="d-flex flex-column align-items-center align-content-center flex-lg-row prod-btn-div">

                                <div data-aos="zoom-in-up" data-aos-delay="450" class="num-box" id="num-<%= stk.getId()%>" max="<%= stk.getQty()%>">
                                    <span class="next" onclick="numberBoxNext('num-<%= stk.getId()%>');"></span>
                                    <span class="prev" onclick="numberBoxPrev('num-<%= stk.getId()%>');"></span>
                                    <div class="d-flex justify-content-center num-box-val">
                                        <span id="val-num-<%= stk.getId()%>">1</span>
                                        <span class="sel">Selected</span>
                                    </div>
                                </div>
                                <div class="d-flex slider-btn-div">
                                    <button data-aos="zoom-in-up" data-aos-delay="500" class="btn btn-outline-primary" type="button" onclick="addToCart(<%= stk.getId()%>);"<%=disBtn%>><i class="fa fa-cart-plus nav-ico"></i>Add to cart</button>
                                    <button data-aos="zoom-in-up" data-aos-delay="600" class="btn btn-outline-primary" type="button" onclick="buyNow(<%= stk.getId()%>);"<%=disBtn%>><i class="fa fa-flash nav-ico"></i>Buy Now</button></a>
                                    <%
                                        if (cust != null) {
                                    %>
                                    <i data-aos="zoom-in-up" data-aos-delay="700" class="fa fa-heart<%=wishICO %> wish-btn btn wish-p-<%= stk.getProduct().getId()%>" data-toggle="tooltip" data-bs-tooltip="" title="Add to Wishlist" onclick="addToWishlist(<%= stk.getProduct().getId()%>);"></i>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="prod-overview">
                <div>
                    <ul class="nav nav-tabs">
                        <li class="nav-item"><a class="nav-link active" role="tab" data-toggle="tab" href="#tab-1">Specifications</a></li>
                        <li class="nav-item"><a class="nav-link" role="tab" data-toggle="tab" href="#tab-2" id="rev-tab">Ratings &amp; Reviews</a></li>
                        <li class="nav-item"><a class="nav-link" role="tab" data-toggle="tab" href="#tab-3">Warrranty &amp; Support</a></li>
                    </ul>
                    <div class="tab-content">

                        <div class="tab-pane show fade active" role="tabpanel" id="tab-1">
                            <div class="prod-specs">
                                <table class="table">
                                    <tbody>
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
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="tab-pane fade review-pane" role="tabpanel" id="tab-2">
                            <div>
                                <div class="p-review">
                                    <div class="row">
                                        <div class="col-auto">
                                            <div class="d-flex flex-column justify-content-center align-items-center rating-tot">
                                                <h1><%= dfRating.format(avgRating)%></h1><span class="rtot"><%= ratingCount%></span><span class="r-span">Total Ratings</span></div>
                                        </div>
                                        <div class="col">
                                            <div style="margin-left: -12px;">
                                                <div class="row">
                                                    <div class="col-auto star-col"><span class="rating-sum"><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i></span></div>
                                                    <div class="col justify-content-xl-center">
                                                        <div class="progress">
                                                            <div class="progress-bar" aria-valuenow="<%=percent5%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=percent5%>%;"><span class="sr-only"><%=percent5%>%</span></div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-auto"><span class="rating-sum"><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="far fa-star"></i></span></div>
                                                    <div class="col">
                                                        <div class="progress">
                                                            <div class="progress-bar" aria-valuenow="<%=percent4%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=percent4%>%;"><span class="sr-only"><%=percent4%>%</span></div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-auto"><span class="rating-sum"><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i></span></div>
                                                    <div class="col">
                                                        <div class="progress">
                                                            <div class="progress-bar" aria-valuenow="<%=percent3%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=percent3%>%;"><span class="sr-only"><%=percent3%>%</span></div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-auto"><span class="rating-sum"><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i></span></div>
                                                    <div class="col">
                                                        <div class="progress">
                                                            <div class="progress-bar" aria-valuenow="<%=percent2%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=percent2%>%;"><span class="sr-only"><%=percent2%>%</span></div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-auto"><span class="rating-sum"><i class="fas fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i></span></div>
                                                    <div class="col">
                                                        <div class="progress">
                                                            <div class="progress-bar" aria-valuenow="<%=percent1%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=percent1%>%;"><span class="sr-only"><%=percent1%>%</span></div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <%
                                if (cust == null) {
                                    for (ProductReview pr : revList) {
                            %>
                            <div class="p-review">
                                <div class="d-flex flex-column justify-content-between flex-sm-row">
                                    <h6 class="rating"><%= pr.getCustomer().getFname()%> <%= pr.getCustomer().getLname()%>
                                        <%
                                            for (int i = 0; i < pr.getRating(); i++) {
                                        %>
                                        <i class="fas fa-star"></i>
                                        <%        
                                            }
                                        %>
                                        <%    
                                            for (int i = 0; i < (5 - pr.getRating()); i++) {
                                        %>
                                        <i class="far fa-star"></i>
                                        <%        
                                            }
                                        %>
                                    </h6>
                                    <span class="rev-date"><%= sdft.format(pr.getDatetimestamp())%></span>
                                </div>
                                <div class="d-flex justify-content-between review-txt"><img src="<%= Validation.getCustomertImage(pr.getCustomer())%>">
                                    <p><%= pr.getContent()%></p>
                                </div>
                            </div>
                            <%
                                }
                            } else {
                                if (myRev == null) {
                                    if (allowReview) {
                            %>
                            <div class="p-review">
                                <div class="d-flex flex-column justify-content-between flex-sm-row">
                                    <h6 class="rating">Your Rating
                                        <i class="far fa-star" id="st1" onclick="setRating(1);" onmouseover="starHover(1);" onmouseout="starOut();"></i>
                                        <i class="far fa-star" id="st2" onclick="setRating(2);" onmouseover="starHover(2);" onmouseout="starOut();"></i>
                                        <i class="far fa-star" id="st3" onclick="setRating(3);" onmouseover="starHover(3);" onmouseout="starOut();"></i>
                                        <i class="far fa-star" id="st4" onclick="setRating(4);" onmouseover="starHover(4);" onmouseout="starOut();"></i>
                                        <i class="far fa-star" id="st5" onclick="setRating(5);" onmouseover="starHover(5);" onmouseout="starOut();"></i>
                                    </h6>
                                    <span class="rev-date"></span>
                                </div>

                                <div class="review-txt">
                                    <div class="d-flex justify-content-between"><img src="<%= Validation.getCustomertImage(cust)%>"><textarea placeholder="Write your review here..." id="rev-desc"></textarea></div>
                                    <div class="d-flex d-sm-flex d-md-flex d-lg-flex d-xl-flex flex-column justify-content-end flex-sm-row justify-content-sm-end justify-content-md-end justify-content-lg-end justify-content-xl-end" style="margin-top: 10px;">
                                        <button class="btn btn-primary" type="button" onclick="addReview(<%= stk.getProduct().getId()%>);"><i class="fas fa-edit" style="padding-right: 10px;"></i>Post Review</button>
                                    </div>
                                </div>
                            </div>
                            <%
                                }
                            } else {
                            %>
                            <div class="p-review">
                                <div class="d-flex flex-column justify-content-between flex-sm-row">
                                    <h6 class="rating">Your Rating

                                        <%                
                                            for (int i = 1; i <= myRev.getRating(); i++) {
                                        %>
                                        <i class="fas fa-star" id="st<%=i%>" onclick="setRating(<%=i%>);" onmouseover="starHover(<%=i%>);" onmouseout="starOut();"></i>
                                        <%
                                            }
                                        %>
                                        <%    
                                            for (int i = 1; i <= (5 - myRev.getRating()); i++) {
                                                int j = myRev.getRating() + i;
                                        %>
                                        <i class="far fa-star" id="st<%=j%>" onclick="setRating(<%=j%>);" onmouseover="starHover(<%=j%>);" onmouseout="starOut();"></i>
                                        <%
                                            }
                                        %>

                                    </h6>
                                    <span class="rev-date"><%= sdft.format(myRev.getDatetimestamp())%></span>
                                </div>

                                <div class="review-txt">
                                    <div class="d-flex justify-content-between"><img src="<%= Validation.getCustomertImage(myRev.getCustomer())%>"><textarea placeholder="Write your review here..." id="rev-desc"><%= myRev.getContent()%></textarea></div>
                                    <div class="d-flex d-sm-flex d-md-flex d-lg-flex d-xl-flex flex-column justify-content-end flex-sm-row justify-content-sm-end justify-content-md-end justify-content-lg-end justify-content-xl-end" style="margin-top: 10px;">
                                        <button class="btn btn-primary" type="button" onclick="deleteReview(<%= stk.getProduct().getId()%>);"><i class="fas fa-minus-circle" style="padding-right: 10px;"></i>Delete Review</button>
                                        <button class="btn btn-primary" type="button" onclick="updateReview(<%= stk.getProduct().getId()%>);"><i class="fas fa-edit" style="padding-right: 10px;"></i>Update Review</button>
                                    </div>
                                </div>
                            </div>
                            <%
                                }
                                for (ProductReview pr : revList) {
                                    if (pr != myRev) {
                            %>
                            <div class="p-review">
                                <div class="d-flex flex-column justify-content-between flex-sm-row">
                                    <h6 class="rating"><%= pr.getCustomer().getFname()%> <%= pr.getCustomer().getLname()%>
                                        <%
                                            for (int i = 0; i < pr.getRating(); i++) {
                                        %>
                                        <i class="fas fa-star"></i>
                                        <%        
                                            }
                                        %>
                                        <%    
                                            for (int i = 0; i < (5 - pr.getRating()); i++) {
                                        %>
                                        <i class="far fa-star"></i>
                                        <%        
                                            }
                                        %>
                                    </h6>
                                    <span class="rev-date"><%= sdft.format(pr.getDatetimestamp())%></span>
                                </div>
                                <div class="d-flex justify-content-between review-txt"><img src="<%= Validation.getCustomertImage(pr.getCustomer())%>">
                                    <p><%= pr.getContent()%></p>
                                </div>
                            </div>
                            <%
                                        }
                                    }
                                }
                            %>


                        </div>

                        <div class="tab-pane fade" role="tabpanel" id="tab-3">
                            <div class="warranty">
                                <h5><strong>Vendor Warranty</strong><br></h5>
                                <p><%=stk.getWarranty()%><br></p>
                                <h5><strong>Vendor References</strong><br></h5>
                                <p>Main Website :&nbsp;<a href="<%=stk.getProduct().getBrand().getMainWebsite()%>"><%=stk.getProduct().getBrand().getMainWebsite()%></a><br>Support Website :&nbsp;<a href="<%=stk.getProduct().getBrand().getSupportWebsite()%>"><%=stk.getProduct().getBrand().getSupportWebsite()%></a><br></p>
                                <h5><strong>MicroTek Return Policy</strong><br></h5>
                                <p>We guarantee your satisfaction on every product we sell with a full refund — and you won’t even need a receipt. We want you to be satisfied with your MicroTek purchase. However, if you need help or need to return an item, we’re
                                    here for you!<br><br>If an item you have purchased from us is not working as expected, please visit one of our in-store Knowledge Experts for free help, where they can solve your problem or even exchange the item for a
                                    product that better suits your needs.<br><br>If you need to return an item, simply bring it back to any MicroTek store for a full refund or exchange. If you are a MicroTek Insider or if you have provided us with validated
                                    contact information (name, address, email address), you won’t even need your receipt.<br><br></p>
                                <h5><strong>General Return Policy</strong><br></h5>
                                <p>Processors, motherboards, digital cameras, camcorders and projectors, 3D printers and 3D scanners, may be returned within 15 days of purchase. All other products may be returned within 30 days of purchase. Merchandise must
                                    be in new condition, with original carton / UPC, and all packaging / accessories / materials.<br><br>Refunds will be credited to the credit card or debit card account used for the original purchase.<br></p>
                                <h5><strong>Support for MicroTek purchases</strong><br></h5>
                                <p>f you need help with products purchased from MicroTek, please contact one of our knowledgeable tech support reps by calling&nbsp;<a href="Tel:+94772351295">+94772351295</a>, or&nbsp;<a href="https://www.microcenter.com/site/content/tech-support.aspx">visit our Tech Support page</a>&nbsp;for
                                    additional options and helpful information.<br></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%
                Criteria prodCR = s.createCriteria(Product.class);
                prodCR.add(Restrictions.eq("category", stk.getProduct().getCategory()));
                prodCR.add(Restrictions.ne("id", stk.getProduct().getId()));
                
                LinkedList<Stock> relatedProdList = new LinkedList();
                
                for (Product p : (List<Product>) prodCR.list()) {
                    if (p.getCategory().getActive() == 1 && p.getBrand().getActive() == 1) {
                        Stock relstk = null;
                        for (Iterator iterator = p.getStocks().iterator(); iterator.hasNext();) {
                            Stock st = (Stock) iterator.next();
                            if (st.getActive() == 1) {
                                if (relstk == null) {
                                    relstk = st;
                                } else if ((st.getSellingPrice() * (100 - st.getDiscount()) / 100) < (relstk.getSellingPrice() * (100 - relstk.getDiscount()) / 100)) {
                                    relstk = st;
                                }
                            }
                        }
                        if (relstk != null) {
                            relatedProdList.add(relstk);
                        }
                    }
                    if (relatedProdList.size() >= 4) {
                        break;
                    }
                }
                if (!relatedProdList.isEmpty()) {
            %>
            <br>
            <h3>Related Products</h3>
            <div class="row row-cols-2 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 row-cols-xl-4 home-card-group">
                <%        
                    for (Stock st : relatedProdList) {
                        Product p = st.getProduct();
                        imgList = new ArrayList();
                        for (Iterator iterator = p.getProductImages().iterator(); iterator.hasNext();) {
                            ProductImage pi = (ProductImage) iterator.next();
                            imgList.add(pi.getId().getImage());
                        }
                        Collections.sort(imgList);
                        
                        double totRating = 0;
                        double avRating = 0;
                        Set<ProductReview> prSet = p.getProductReviews();
                        for (Iterator<ProductReview> revIT = prSet.iterator(); revIT.hasNext();) {
                            ProductReview pr = revIT.next();
                            totRating += pr.getRating();
                        }
                        if (!prSet.isEmpty()) {
                            avRating = totRating / prSet.size();
                        }
                %>
                <div class="col">
                    <div class="card">
                        <div class="card-body">
                            <div><a href="product.jsp?id=<%=st.getId()%>"><img class="prod-img" src="<%= Validation.getNextProductImage(imgList, 1)%>"></a></div>
                            <a class="card-link" href="product.jsp?id=<%=st.getId()%>">
                                <div class="d-flex flex-column justify-content-center related-det">
                                    <h5 class="text-center"><%=p.getBrand().getName() + " " + p.getName()%></h5>
                                    <span class="text-center rating">
                                        <%
                                            int roundedAvgRating = (int) avRating;
                                            for (int i = 0; i < roundedAvgRating; i++) {
                                        %>
                                        <i class="fas fa-star"></i>
                                        <%        
                                            }
                                            
                                            double remainderRating = avRating - roundedAvgRating;
                                            if (remainderRating == 0) {
                                                for (int i = 0; i < (5 - roundedAvgRating); i++) {
                                        %>
                                        <i class="far fa-star"></i>
                                        <%        
                                            }
                                        } else {
                                            if (remainderRating <= 0.5) {
                                        %>
                                        <i class="fas fa-star-half-alt"></i>
                                        <%    
                                        } else {
                                        %>
                                        <i class="fas fa-star"></i>
                                        <%        
                                            }
                                            for (int i = 0; i < (4 - roundedAvgRating); i++) {
                                        %>
                                        <i class="far fa-star"></i>
                                        <%            
                                                }
                                            }
                                        %>
                                        <span class="rate-val">( <%= dfRating.format(avRating)%> / 5 )</span>
                                    </span>
                                    <span class="text-center price-tag"><i class="fas fa-money-check-alt"></i>Rs. <%= df.format(st.getSellingPrice() * (100 - st.getDiscount()) / 100)%></span>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>
            </div>

            <%    
                }
            %>
        </div>

        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/autocomplete.js"></script>
        <script src="assets/js/Product-Viewer-1.js"></script>
        <script src="assets/js/Product-Viewer.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/Swiper-Slider.js"></script>
        <script src="assets/js/product.js"></script>
    </body>
</html>
<%        } else {
            request.getRequestDispatcher("404.jsp").forward(request, response);
        }
    } catch (Exception e) {
        request.getRequestDispatcher("404.jsp").forward(request, response);
    }
    s.close();
%>