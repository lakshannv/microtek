<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    String p = request.getParameter("remainingTime");
%>
<html>
    <head>
        <title>Under Maintenance</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <%
        if (p == null) {
    %>
    <body>
        <%
        } else {
        %>
    <body onload="startTimeOut(<%=request.getParameter("remainingTime")%>);">
        <%
            }
        %>
        <div class="container d-flex flex-column justify-content-center align-items-center align-content-center" style="min-height: 100%;">
            <h1 class="text-center rubberBand animated" style="font-weight: 800;color: rgb(255,93,93);">MicroTek is under maintenance right now.</h1>
            <%
                if (p == null) {
            %>
            <h3 data-aos="zoom-in-up" data-aos-duration="500" data-aos-delay="500" class="text-center">We'll be back online shortly.</h3>
            <%
            } else {
            %>
            <h3 data-aos="zoom-in-up" data-aos-duration="500" data-aos-delay="500" class="text-center">We'll be back in <span id="rem"></span>.</h3>
            <%
                }
            %>

            <img src="assets/img/bgs/um.gif" style="opacity: 0.80;max-width: 80%;">
            <p data-aos="zoom-in-up" data-aos-delay="1000">Thank you for your patience.</p>
        </div>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/um.js"></script>
    </body>
</html>
