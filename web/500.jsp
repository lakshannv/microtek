<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Oops !</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: url(&quot;assets/img/bgs/500.jpg&quot;);">
        <div class="container" style="height: 100vh;">
            <div class="d-flex flex-column justify-content-center align-items-center m-auto" style="min-height: 100vh;">
                <h1 class="text-center" style="font-weight: 800;color: rgb(255,114,114);">Uh-oh, Somthing went wrong !</h1>
                <h2 class="text-center" style="color: rgb(255,195,195);">We're looking into it...</h2>
                <div class="d-inline-flex wobble animated">
                    <div class="eye"></div>
                    <div class="eye" style="margin-left: 10px;"></div>
                </div><img src="assets/img/500.png" style="width: 200px;margin-bottom: 10px;">
                <h5 class="text-center">You can try again later. Meanwhile, Let's take you home.</h5><a href="index.jsp"><button class="btn btn-primary err-btn" type="button"><i class="fa fa-home" style="padding-right: 6px;"></i>Take me Home</button></a></div>
        </div>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script>
            $("body").mousemove(function (event) {
                var eye = $(".eye");
                var x = (eye.offset().left) + (eye.width() / 2);
                var y = (eye.offset().top) + (eye.height() / 2);
                var rad = Math.atan2(event.pageX - x, event.pageY - y);
                var rot = (rad * (180 / Math.PI) * -1) + 180;
                eye.css({
                    '-webkit-transform': 'rotate(' + rot + 'deg)',
                    '-moz-transform': 'rotate(' + rot + 'deg)',
                    '-ms-transform': 'rotate(' + rot + 'deg)',
                    'transform': 'rotate(' + rot + 'deg)'
                });
            });
        </script>
    </body>
</html>
