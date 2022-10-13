<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek User Management</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body onload="loadUserTypes();">
        <jsp:include page="incl_msgbox.jsp" />
        <div id="delete-popup-bg" class="admin-popup-bg" style="display: none;" onclick="closeModalBG('delete-popup-bg');">
            <div class="d-flex flex-column pulse animated modal-pop" id="delete-popup"><i class="fa fa-close close-btn" onclick="closeModal('delete-popup-bg');"></i>
                <h5><i class="fas fa-cogs"></i>Confirm</h5>
                <p class="con">Are you sure you want to delete <span id="del-itm"></span> ?</p>
                <div class="d-flex justify-content-end align-items-center r-field"><button class="btn btn-primary" id="delete-confirm-btn" type="button"><i class="fas fa-check-circle"></i>Yes</button></div>
            </div>
        </div>
        <div id="usr-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="usr-popup"><i class="fa fa-close close-btn" onclick="closeModal('usr-popup-bg');"></i>
                <h5><i class=\"fas fa-user-plus\"></i>Add New User</h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>UserName</span><input type="text" oninput="hideToolTip('un-tt');" id="un"><span class="shake animated field-tooltip" id="un-tt"></span></div>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Password</span><input type="password" oninput="hideToolTip('pw-tt');" id="pw"><span class="shake animated field-tooltip" id="pw-tt"></span></div>
                <div class="field-row">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <label class="input-group-text">User Type</label>
                            <select class="shadow-lg search-select" id="usr-type-cmb">

                            </select>
                        </div>
                    </div>
                </div>
                <div class="d-flex justify-content-end align-items-center r-field"><button class="btn btn-primary" type="button" onclick="addNewUser();"><i class="fas fa-plus-circle"></i>Add</button></div>
            </div>
        </div>
        <div id="usr-type-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="usr-type-popup"><i class="fa fa-close close-btn" onclick="closeModal('usr-type-popup-bg');"></i>
                <h5 id="usr-type-popup-title"></h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>User Type</span><input type="text" id="ut" oninput="hideToolTip('ut-tt');"><span class="shake animated field-tooltip" id="ut-tt"></span></div>
                <div class="field-row priv-div">
                    <h5>Privileges</h5>
                    <label class="chklabel">DashBoard<input type="checkbox" id="prv-1"><span class="checkmark"></span></label>
                    <label class="chklabel">User Management<input type="checkbox" id="prv-2"><span class="checkmark"></span></label>
                    <label class="chklabel">Product Management<input type="checkbox" id="prv-3"><span class="checkmark"></span></label>
                    <label class="chklabel">Order Management<input type="checkbox" id="prv-4"><span class="checkmark"></span></label>
                    <label class="chklabel">Delivery Management<input type="checkbox" id="prv-5"><span class="checkmark"></span></label>
                    <label class="chklabel">Sales & Reports<input type="checkbox" id="prv-6"><span class="checkmark"></span></label>
                </div>
                <div class="d-flex justify-content-end align-items-center r-field"><button class="btn btn-primary" type="button" id="usr-type-popup-btn"><i class="fas fa-user-check"></i>Add</button></div>
            </div>
        </div>
        <div class="admin-container">
            <jsp:include page="incl_admin_navbar.jsp" />
            <div class="d-flex">
                <jsp:include page="incl_admin_pane.jsp" />
                <div id="dash-content">
                    <div>
                        <ul class="nav nav-tabs">
                            <li class="nav-item"><a class="nav-link active" role="tab" data-toggle="tab" href="#tab-1">Users</a></li>
                            <li class="nav-item"><a class="nav-link" role="tab" data-toggle="tab" href="#tab-2">User types &amp; Privileges</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane fade show active" role="tabpanel" id="tab-1">
                                <div class="d-flex justify-content-between align-items-center usr-table-control-div">
                                    <h4>All Users</h4>
                                    <div class="d-flex justify-content-xl-end btn-row"><button class="btn btn-primary" type="button" onclick="showModal('usr-popup-bg');"><i class="fas fa-plus-circle" style="padding-right: 10px;"></i>Add New User</button></div>
                                </div>
                                <div class="table-responsive">
                                    <table class="table usr-table">
                                        <thead>
                                            <tr>
                                                <th>UserName</th>
                                                <th>Password</th>
                                                <th>User Type</th>
                                                <th>Controls</th>
                                            </tr>
                                        </thead>
                                        <tbody id="usr-tbody">
                                            <jsp:include page="get_users.jsp"/>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="tab-pane fade" role="tabpanel" id="tab-2">
                                <div class="d-flex justify-content-between align-items-center usr-table-control-div">
                                    <h4>User Types</h4>
                                    <div class="d-flex justify-content-xl-end btn-row"><button class="btn btn-primary" type="button" onclick="showNewUserTypePopup();"><i class="fas fa-plus-circle" style="padding-right: 10px;"></i>New User Type</button></div>
                                </div>
                                <div class="table-responsive">
                                    <table class="table usr-type-table" id="usr-type-table">
                                        <thead>
                                            <tr>
                                                <th>User Type Name</th>
                                                <th>User Count</th>
                                                <th>Controls</th>
                                            </tr>
                                        </thead>
                                        <tbody id="usr-type-tbody">

                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/Product-Viewer-1.js"></script>
        <script src="assets/js/Product-Viewer.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/Swiper-Slider.js"></script>
        <script src="assets/js/usr_mng.js"></script>
        <script>
                                        document.getElementById("dash-link-2").className += " active";
                                        document.getElementById("nav-link-2").className += " active";
        </script>
    </body>
</html>
