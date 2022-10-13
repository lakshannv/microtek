<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<div id="msg-popup-bg" class="popup-bg" style="display: none;">
    <div class="d-flex flex-column rubberBand animated" id="msg-popup"><i class="fa fa-close close-btn" onclick="closeMsg();" id="msg-close-btn"></i>
        <h5><i class="fas fa-info-circle"></i><span id="msg-title"></span></h5>
        <p id="msg-body"></p>
        <div class="d-flex d-sm-flex d-md-flex d-lg-flex d-xl-flex justify-content-center justify-content-sm-end justify-content-md-end justify-content-lg-end justify-content-xl-end"><button id="msg-ok-btn" class="btn btn-primary" type="button" onclick="closeMsg();">Okay</button></div>
    </div>
</div>