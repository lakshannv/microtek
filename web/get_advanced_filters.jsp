<%@page import="hibernate.ProductHasSpec"%>
<%@page import="model.SpecComparator"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="hibernate.Spec"%>
<%@page import="java.util.List"%>
<%@page import="hibernate.Brand"%>
<%@page import="org.hibernate.criterion.Criterion"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.google.gson.reflect.TypeToken"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="hibernate.Category"%>
<%@page import="hibernate.Product"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();

    String catName = request.getParameter("catName");
    String brandList = request.getParameter("brandList");

    Criteria productCR = s.createCriteria(Product.class);

    Criteria catCR = s.createCriteria(Category.class);
    catCR.add(Restrictions.eq("name", catName));
    Category c = (Category) catCR.uniqueResult();
    productCR.add(Restrictions.eq("category", c));

    Gson g = new Gson();
    if (brandList != null) {
        TypeToken tt = new TypeToken<ArrayList<String>>() {
        };
        ArrayList<String> bList = g.fromJson(brandList, tt.getType());
        Criterion[] crl = new Criterion[bList.size()];
        for (int i = 0; i < bList.size(); i++) {
            Criteria brandCR = s.createCriteria(Brand.class);
            brandCR.add(Restrictions.eq("name", bList.get(i)));
            Brand b = (Brand) brandCR.uniqueResult();
            crl[i] = Restrictions.eq("brand", b);
        }
        productCR.add(Restrictions.or(crl));
    }

    List<Product> prodList = productCR.list();

    TreeMap<Spec, TreeMap<String, Integer>> specMap = new TreeMap(SpecComparator.getComparator());

    for (Iterator<Spec> iterator = c.getSpecs().iterator(); iterator.hasNext();) {
        Spec sp = iterator.next();
        TreeMap<String, Integer> spValMap = new TreeMap();
        for (Product p : prodList) {
            for (Iterator<ProductHasSpec> pIT = p.getProductHasSpecs().iterator(); pIT.hasNext();) {
                ProductHasSpec pSpec = pIT.next();
                if (pSpec.getSpec() == sp) {

                    String sVal = pSpec.getSpecValue();
                    if (pSpec.getSpec().getUnit() != null) {
                        sVal = sVal + " " + pSpec.getSpec().getUnit();
                    }
                    if (spValMap.get(sVal) == null) {
                        spValMap.put(sVal, 1);
                    } else {
                        spValMap.put(sVal, spValMap.get(sVal) + 1);
                    }

                }
            }
        }
        specMap.put(sp, spValMap);
    }

    for (Spec sp : specMap.keySet()) {
        if (!specMap.get(sp).isEmpty()) {
%>

<div class="search-filter adv-sf">
    <div class="d-flex justify-content-between search-filter-heading collapsed" id="spec-<%= sp.getId()%>-head" data-toggle="collapse" aria-expanded="false" aria-controls="spec-<%= sp.getId()%>-body" href="#spec-<%= sp.getId()%>-body" role="button" onclick="toggleResult('spec-<%= sp.getId()%>-head');" style="border-radius: 0 25px;">
        <h6><%= sp.getSpecName()%><br /></h6><i class="fa fa-chevron-circle-down"></i>
    </div>
    <div id="spec-<%= sp.getId()%>-body" class="collapse">
        <div class="search-filter-body">

            <%
                String chkAttribs = "";
                if (specMap.get(sp).keySet().size() == 1) {
                    chkAttribs = " checked=\"\" disabled=\"\"";
                }
                for (String sVal : specMap.get(sp).keySet()) {
            %>
            <div class="d-inline-flex flex-row check-box-div adv-sf-chk">
                <label class="chklabel"><%= sVal%><input onchange="applyAdvancedsearchFilters();" type="checkbox"<%= chkAttribs%>><span class="checkmark"></span></label>
                <span class="d-flex d-sm-flex d-md-flex d-lg-flex d-xl-flex justify-content-center align-items-center align-self-center justify-content-sm-center align-items-sm-center justify-content-md-center align-items-md-center justify-content-lg-center align-items-lg-center align-items-xl-center res-count"><%= specMap.get(sp).get(sVal)%></span>
            </div>
            <%
                }
            %>
        </div>
    </div>
</div>

<%
        }
    }
    s.close();
%>