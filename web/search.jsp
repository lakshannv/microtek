<%@page import="java.util.Iterator"%>
<%@page import="hibernate.Stock"%>
<%@page import="hibernate.Product"%>
<%@page import="java.util.TreeSet"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="hibernate.Brand"%>
<%@page import="java.util.List"%>
<%@page import="hibernate.Category"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();

    Criteria catCR = s.createCriteria(Category.class);
    catCR.add(Restrictions.eq("active", (byte) 1));
    catCR.addOrder(Order.asc("name"));

    String cat = request.getParameter("cat");
    String brand = request.getParameter("brand");

    TreeSet<String> brandSet = new TreeSet();
    Criteria prodCR = s.createCriteria(Product.class);
    if (cat != null) {
        if (!cat.equalsIgnoreCase("Any")) {
            Criteria selCatCR = s.createCriteria(Category.class);
            selCatCR.add(Restrictions.eq("name", cat));
            Category c = (Category) selCatCR.uniqueResult();
            prodCR.add(Restrictions.eq("category", c));
        }
    }
    for (Product p : (List<Product>) prodCR.list()) {
        boolean hasStock = false;
        for (Iterator<Stock> it = p.getStocks().iterator(); it.hasNext();) {
            Stock stk = it.next();
            if (stk.getActive() == 1) {
                hasStock = true;
                break;
            }
        }

        if (p.getCategory().getActive() == 1 && p.getBrand().getActive() == 1 && hasStock == true) {
            brandSet.add(p.getBrand().getName());
        }
    }

    String keyWord = request.getParameter("keyWord");
    if (keyWord == null) {
        keyWord = "";
    }
    String pLimit = (String) session.getAttribute("pageLimit");
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Search</title>
        <jsp:include page="incl_header.jsp" />
    </head>

    <body onload="setPageLimit(<%= pLimit%>); loadResults(); loadSearchFilters(); loadCart(); loadWishlist(); initSearchBox();" style="background-image: url('assets/img/bgs/search.jpg');">
        <jsp:include page="incl_msgbox.jsp" />

        <jsp:include page="incl_navbar.jsp" />

        <div class="container" style="margin-top: 80px;">
            <div data-aos="slide-right" data-aos-delay="100" class="search-panel">
                <div class="row">
                    <div class="col-sm-12 col-md-6 col-lg-4 col-xl-4 my-sm-auto my-md-auto my-lg-auto mx-xl-auto comp">
                        <div class="input-group">
                            <div class="input-group-prepend"><label class="input-group-text">Category</label>
                                <select onchange="loadBrandComboBox(); loadResults(); loadSearchFilters();" id="cat-combo-box" class="shadow-lg search-select">
                                    <optgroup label="Select Category">
                                        <option>Any</option>
                                        <%
                                            for (Category c : (List<Category>) catCR.list()) {
                                                if (c.getName().equalsIgnoreCase(cat)) {
                                        %>
                                        <option selected=""><%= c.getName()%></option>
                                        <%
                                        } else {
                                        %>
                                        <option><%= c.getName()%></option>
                                        <%
                                                }
                                            }
                                        %>
                                    </optgroup>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-12 col-md-6 col-lg-4 col-xl-4 comp">
                        <div class="input-group">
                            <div class="input-group-prepend"><label class="input-group-text">Brand</label>
                                <select onchange="loadResults(); loadSearchFilters();" id="brand-combo-box" class="shadow-lg search-select">
                                    <optgroup label="Select Brand">
                                        <option>Any</option>
                                        <%
                                            for (String b : brandSet) {
                                                if (b.equalsIgnoreCase(brand)) {
                                        %>
                                        <option selected=""><%= b%></option>
                                        <%
                                        } else {
                                        %>
                                        <option><%= b%></option>
                                        <%
                                                }
                                            }
                                        %>
                                    </optgroup>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 comp">
                        <div class="d-flex align-items-center search-box autocomplete"><input id="search-box" type="search" placeholder="Search by Product, Brand or Category" value="<%=keyWord%>"><i onclick="loadResults(); loadSearchFilters();" class="fas fa-search"></i></div>
                    </div>
                </div>
            </div>

            <div class="row search-content">
                <div data-aos="slide-up" data-aos-delay="500" id="advanced-search-panel" class="col advanced-search-panel">

                    <div class="search-filter">
                        <div class="search-filter-heading">
                            <h6>Sort By</h6>
                        </div>
                        <div class="search-filter-body">
                            <div class="price-div">
                                <div class="input-group-prepend"><label class="input-group-text">Sort</label>
                                    <select onchange="applysearchFilters();" id="sort-by" class="shadow-lg search-select">
                                        <option selected="">Newly Added</option>
                                        <option>Price Low to High</option>
                                        <option>Price High to Low</option>
                                        <option>Low Stocks</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="search-filter">
                        <div class="search-filter-heading">
                            <h6>Price Range</h6>
                        </div>
                        <div class="search-filter-body">
                            <div class="price-div"><span>From (Rs.) :</span><input id="price-from" type="number" /></div>
                            <div class="price-div"><span>To (Rs.) :</span><input id="price-to" type="number" /></div><button onclick="loadResults(); loadSearchFilters();" class="btn btn-primary" type="button"><i class="fas fa-check-circle"></i>Apply</button></div>
                    </div>

                    <div id="res-cat" class="search-filter">
                        <div class="d-flex justify-content-between search-filter-heading" id="res-cat-head" data-toggle="collapse" aria-expanded="false" aria-controls="cat-body" href="#cat-body" role="button" onclick="toggleResult('res-cat-head');">
                            <h6>Category<br /></h6><i class="fa fa-chevron-circle-up"></i>
                        </div>
                        <div id="cat-body" class="show">
                            <div id="cat-filter" class="search-filter-body">

                            </div>
                        </div>
                    </div>

                    <div id="res-brand" class="search-filter">
                        <div class="d-flex justify-content-between search-filter-heading" id="res-brand-head" data-toggle="collapse" aria-expanded="false" aria-controls="brand-body" href="#brand-body" role="button" onclick="toggleResult('res-brand-head');">
                            <h6>Brand<br /></h6><i class="fa fa-chevron-circle-up"></i>
                        </div>
                        <div id="brand-body" class="show">
                            <div id="brand-filter" class="search-filter-body">

                            </div>
                        </div>
                    </div>

                    <div id="advanced-filter-div" style="margin-top: 20px;">

                    </div>
                </div>

                <div class="col">
                    <div data-aos="slide-left" data-aos-delay="300" class="d-sm-flex d-md-flex d-lg-inline-flex d-xl-inline-flex flex-row justify-content-sm-center align-items-sm-center align-items-md-center align-items-lg-center align-items-xl-center res-info-div"><span><span id="res-count">0</span> Results found.</span>
                        <div class="input-group-prepend">
                            <label class="input-group-text">Results Per Page</label>
                            <select id="page-limit" onchange="applyAdvancedsearchFilters();" class="shadow-lg search-select">
                                <option>4</option>
                                <option>8</option>
                                <option>12</option>
                            </select>
                        </div>
                    </div>

                    <div id="product-list-div">

                    </div>

                </div>


            </div>
        </div>
    </div>
    <jsp:include page="incl_footer.jsp"/>
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/bootstrap/js/bootstrap.min.js"></script>
    <script src="assets/js/chart.min.js"></script>
    <script src="assets/js/bs-init.js"></script>
    <script src="assets/js/aos.js"></script>
    <script src="assets/js/Product-Viewer-1.js"></script>
    <script src="assets/js/Product-Viewer.js"></script>
    <script src="assets/js/srcipt.js"></script>
    <script src="assets/js/Swiper-Slider.js"></script>
    <script src="assets/js/autocomplete.js"></script>
    <script src="assets/js/search.js"></script>
</body>

</html>
<%
    s.close();
%>