<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Product Management</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body onload="loadCategoryTable(); loadBrandTable(); loadStockTable();">
        <jsp:include page="incl_msgbox.jsp" />
        <div id="delete-popup-bg" class="admin-popup-bg" style="display: none;" onclick="closeModalBG('delete-popup-bg');">
            <div class="d-flex flex-column pulse animated modal-pop" id="delete-popup"><i class="fa fa-close close-btn" onclick="closeModal('delete-popup-bg');"></i>
                <h5><i class="fas fa-cogs"></i>Confirm</h5>
                <p class="con">Are you sure you want delete<br><span id="del-itm"></span> ?</p>
                <div class="d-flex justify-content-end align-items-center r-field"><button class="btn btn-primary" id="delete-confirm-btn" type="button"><i class="fas fa-check-circle"></i>Yes</button></div>
            </div>
        </div>
        <div id="spec-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="spec-popup"><i class="fa fa-close close-btn" onclick="closeModal('spec-popup-bg');"></i>
                <h5><i class="fas fa-cogs"></i>Specification Details</h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Spec Name</span><input id="newSpecName" type="text" oninput="hideToolTip('spec-name-tt');"><span class="shake animated field-tooltip" id="spec-name-tt"></span></div>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Spec Unit</span><input id="newSpecUnit" type="text" placeholder="Leave empty if N/A" oninput="hideToolTip('spec-unit-tt');"><span class="shake animated field-tooltip" id="spec-unit-tt"></span></div>
                <div class="d-flex justify-content-end field-row">
                    <div class="d-inline-flex flex-row check-box-div"><label class="chklabel">Key Spec<input id="newSpecIsKey" type="checkbox"><span class="checkmark"></span></label></div>
                </div>
                <div class="d-flex justify-content-end align-items-center r-field"><button id="spec-add-btn" class="btn btn-primary" type="button"><i class="fas fa-plus-circle"></i>Add</button></div>
            </div>
        </div>
        <div id="spec-update-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="spec-update-popup"><i class="fa fa-close close-btn" onclick="closeModal('spec-update-popup-bg');"></i>
                <h5><i class="fas fa-cogs"></i>Updating : <span id="oldSpecName"></span></h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Spec Name</span><input type="text" id="updateSpecName" oninput="hideToolTip('spec-name-update-tt');"><span class="shake animated field-tooltip" id="spec-name-update-tt"></span></div>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Spec Unit</span><input type="text" id="updateSpecUnit" placeholder="Leave empty if N/A" oninput="hideToolTip('spec-unit-update-tt');"><span class="shake animated field-tooltip" id="spec-unit-update-tt"></span></div>
                <div class="d-flex justify-content-end field-row">
                    <div class="d-inline-flex flex-row check-box-div"><label class="chklabel">Key Spec<input type="checkbox" id="updateIsKey"><span class="checkmark"></span></label></div>
                </div>
                <div class="d-flex justify-content-end align-items-center r-field"><button id="spec-update-btn" class="btn btn-primary" type="button"><i class="fas fa-edit"></i>Update</button></div>
            </div>
        </div>
        <div id="cat-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="cat-popup"><i class="fa fa-close close-btn" onclick="closeModal('cat-popup-bg');"></i>
                <h5><i class="fas fa-cogs"></i>Add New Category</h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Category Name</span><input id="newCatName" type="text" oninput="hideToolTip('cat-tt');"><span class="shake animated field-tooltip" id="cat-tt"></span></div>
                <div class="d-flex justify-content-end align-items-center r-field"><button onclick="addNewCategory();" class="btn btn-primary" type="button"><i class="fas fa-plus-circle"></i>Add</button></div>
            </div>
        </div>

        <div id="price-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="price-popup"><i class="fa fa-close close-btn" onclick="closeModal('price-popup-bg');"></i>
                <h5><i class="fas fa-dollar-sign"></i>New Selling Price Detected !</h5>
                <span style="padding: 0 10px;">You have changed the Selling Price from Rs. <span id="price-change-from"></span> to Rs. <span id="price-change-to"></span>.<br>Do you want to add the new price as a new stock or update the price of the existing stock?<br>(Note that you can update the price of the existing stock only if it has no associated invoices to it.)<br></span>
                <div class="d-flex flex-column justify-content-end align-items-center flex-sm-row r-field">
                    <button class="btn btn-primary" type="button" id="stk-up-ext"><i class="fas fa-dolly-flatbed"></i>Try to update the existing stock</button>
                    <button class="btn btn-primary" type="button" id="stk-up-add"><i class="fas fa-plus-circle"></i>Add as a new Stock</button>
                </div>
            </div>
        </div>

        <div id="cat-update-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="cat-update-popup"><i class="fa fa-close close-btn" onclick="closeModal('cat-update-popup-bg');"></i>
                <h5><i class="fas fa-cogs"></i>Updating : <span id="oldCatName"></span></h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Category Name</span><input id="updateCatName" type="text" oninput="hideToolTip('cat-update-tt');"><span class="shake animated field-tooltip" id="cat-update-tt"></span></div>
                <div class="d-flex justify-content-end field-row">
                    <div class="d-inline-flex flex-row check-box-div"><label class="chklabel">Active<input id="isActiveCat" type="checkbox"><span class="checkmark"></span></label></div>
                </div>
                <div class="d-flex justify-content-end align-items-center r-field"><button id="cat-update-btn" class="btn btn-primary" type="button"><i class="fas fa-edit"></i>Update</button></div>
            </div>
        </div>
        <div id="brand-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="brand-popup"><i class="fa fa-close close-btn" onclick="closeModal('brand-popup-bg');"></i>
                <h5><i class="fas fa-cogs"></i>Add New Brand</h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Brand Name</span><input id="newBrandName" type="text" oninput="hideToolTip('brand-tt');"><span class="shake animated field-tooltip" id="brand-tt"></span></div>
                <div class="d-flex justify-content-end align-items-center r-field"><button onclick="addNewBrand();" class="btn btn-primary" type="button"><i class="fas fa-plus-circle"></i>Add</button></div>
            </div>
        </div>
        <div id="brand-update-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="brand-update-popup"><i class="fa fa-close close-btn" onclick="closeModal('brand-update-popup-bg');"></i>
                <h5><i class="fas fa-cogs"></i>Updating : <span id="oldBrandName"></span></h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Brand Name</span><input id="updateBrandName" type="text" oninput="hideToolTip('brand-update-tt');"><span class="shake animated field-tooltip" id="brand-update-tt"></span></div>
                <div class="d-flex justify-content-end field-row">
                    <div class="d-inline-flex flex-row check-box-div"><label class="chklabel">Active<input id="isActiveBrand" type="checkbox"><span class="checkmark"></span></label></div>
                </div>
                <div class="d-flex justify-content-end align-items-center r-field"><button id="brand-update-btn" class="btn btn-primary" type="button"><i class="fas fa-edit"></i>Update</button></div>
            </div>
        </div>
        <div class="admin-container">
            <jsp:include page="incl_admin_navbar.jsp" />
            <div class="d-flex">
                <jsp:include page="incl_admin_pane.jsp" />
                <div id="dash-content">
                    <div>
                        <div>
                            <ul class="nav nav-tabs">
                                <li class="nav-item"><a class="nav-link active" role="tab" data-toggle="tab" href="#tab-1">Product Categories<br></a></li>
                                <li class="nav-item"><a class="nav-link" role="tab" data-toggle="tab" href="#tab-2">Product Brands</a></li>
                                <li class="nav-item"><a class="nav-link" role="tab" data-toggle="tab" href="#tab-3">Stocks &amp; Products</a></li>
                            </ul>
                            <div class="tab-content" style="margin-top: 20px;">
                                <div class="tab-pane show fade active" role="tabpanel" id="tab-1">
                                    <div class="row">
                                        <div class="col-md-12 col-lg-6 cat-col">
                                            <div class="row justify-content-between table-control-div">
                                                <div class="col">
                                                    <h4>Category</h4>
                                                </div>
                                                <div class="col-auto">
                                                    <div class="d-flex justify-content-xl-end btn-row">
                                                        <button id="cat-del-btn" class="btn btn-primary" type="button" style="display: none;"><i class="fas fa-trash-alt"></i></button>
                                                        <button id="cat-edit-btn" onclick="showModal('cat-update-popup-bg');" class="btn btn-primary" type="button" style="display: none;"><i class="fas fa-edit"></i></button>
                                                        <button class="btn btn-primary" type="button" onclick="showModal('cat-popup-bg');"><i class="fas fa-plus-circle"></i></button>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="table-responsive table-bordered prod-table ap-first-table">
                                                <table id="cat-table" class="table table-bordered table-hover table-sm">
                                                    <thead class="text-center">
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Category</th>
                                                            <th>Active</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody id="tbody-cat">

                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                        <div class="col">
                                            <div class="row justify-content-between table-control-div">
                                                <div class="col-sm-12 col-md-12 col-lg-12 col-xl-auto">
                                                    <h4 id="selected-cat-spec">Specifications</h4>
                                                </div>
                                                <div class="col-auto">
                                                    <div class="d-flex justify-content-xl-end btn-row">
                                                        <button id="spec-moveup-btn" class="btn btn-primary" type="button" onclick="moveSpecUp();" style="display: none;"><i class="fas fa-chevron-circle-up"></i></button>
                                                        <button id="spec-movedown-btn" class="btn btn-primary" type="button" onclick="moveSpecDown();" style="display: none;"><i class="fas fa-chevron-circle-down"></i></button>
                                                        <button id="spec-del-btn" class="btn btn-primary" type="button" onclick="showModal('spec-update-popup-bg');" style="display: none;"><i class="fas fa-trash-alt"></i></button>
                                                        <button id="spec-edit-btn" class="btn btn-primary" type="button" onclick="showModal('spec-update-popup-bg');" style="display: none;"><i class="fas fa-edit"></i></button>
                                                        <button id="spec-ad-btn" class="btn btn-primary" type="button" onclick="showModal('spec-popup-bg');" style="display: none; margin-left: 0px;"><i class="fas fa-plus-circle"></i></button>
                                                    </div>
                                                </div>
                                            </div>
                                            <div>
                                                <div class="table-responsive table-bordered prod-table">
                                                    <table id="spec-table" class="table table-bordered table-hover table-sm">
                                                        <thead class="text-center">
                                                            <tr>
                                                                <th>ID</th>
                                                                <th>Specification</th>
                                                                <th>Spec Unit</th>
                                                                <th>Key Spec</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="tbody-spec">

                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="tab-pane fade" role="tabpanel" id="tab-2">
                                    <div class="row">
                                        <div class="col-sm-12 col-md-12 col-lg-6 col-xl-6 cat-col">
                                            <div class="d-flex justify-content-between align-items-center table-control-div">
                                                <h4>Brands</h4>
                                                <div class="d-flex justify-content-xl-end btn-row">
                                                    <button id="brand-del-btn" class="btn btn-primary" type="button" style="display: none;"><i class="fas fa-trash-alt"></i></button>
                                                    <button id="brand-edit-btn" onclick="showModal('brand-update-popup-bg');" class="btn btn-primary" type="button" style="display: none;"><i class="fas fa-edit"></i></button>
                                                    <button class="btn btn-primary" type="button" onclick="showModal('brand-popup-bg');"><i class="fas fa-plus-circle"></i></button>
                                                </div>
                                            </div>
                                            <div class="table-responsive table-bordered prod-table ap-first-table">
                                                <table id="brand-table" class="table table-bordered table-hover table-sm">
                                                    <thead class="text-center">
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Category</th>
                                                            <th>Active</th>
                                                            <th style="display: none;">web</th>
                                                            <th style="display: none;">sup</th>
                                                            <th style="display: none;">img</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody id="tbody-brand">

                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                        <div class="col">
                                            <div>
                                                <h4 id="selected-brand-name">Brand Details</h4>
                                                <input class="d-none" type="file" id="f" accept="image/*" onchange="viewIMG('f', 'brand-img', 'img-rmv-btn', 'assets/img/brands/brand.png');">
                                                <div class="d-flex justify-content-between">
                                                    <div>
                                                        <div class="brand-img-div">
                                                            <img id="brand-img" class="brand-img" src="assets/img/brands/brand.png">
                                                            <button class="btn btn-primary" type="button" onclick="openFC('f');"><i class="fas fa-camera"></i></button>
                                                            <button class="btn btn-primary" id="img-rmv-btn" type="button" onclick="removeIMG('f', 'brand-img', 'img-rmv-btn', 'assets/img/brands/brand.png');" style="display: none;"><i class="fas fa-trash-alt"></i></button>
                                                        </div>
                                                    </div><button id="brand-detail-save-btn" class="btn btn-primary" type="button" style="display: none;">Save</button></div>
                                                <div class="d-flex justify-content-between align-items-center r-field"><span style="min-width: 100px;">Main Website</span><input id="main-web" type="text" style="width: 100%;"></div>
                                                <div class="d-flex justify-content-between align-items-center r-field"><span style="min-width: 120px;">Support Website</span><input id="sup-web" type="text" style="width: 100%;"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="tab-pane fade" role="tabpanel" id="tab-3">
                                    <div class="search-panel">
                                        <div class="row">
                                            <div class="col-sm-12 col-md-6 col-lg-4 col-xl-4 my-sm-auto my-md-auto my-lg-auto mx-xl-auto comp">
                                                <div class="input-group">
                                                    <div class="input-group-prepend">
                                                        <label class="input-group-text">Category</label>
                                                        <select onchange="loadStockTable();" class="shadow-lg search-select" id="cat-combo-box-search">

                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-sm-12 col-md-6 col-lg-4 col-xl-4 comp">
                                                <div class="input-group">
                                                    <div class="input-group-prepend">
                                                        <label class="input-group-text">Brand</label>
                                                        <select onchange="loadStockTable();" class="shadow-lg search-select" id="brand-combo-box-search">

                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 comp">
                                                <div class="input-group">
                                                    <div class="input-group-prepend">
                                                        <label class="input-group-text">Sort By</label>
                                                        <select onchange="loadStockTable();" id="product-sort-by" class="shadow-lg search-select">
                                                            <option selected="">Stock ID Asc.</option>
                                                            <option>Stock ID Desc.</option>
                                                            <option>Product Name Asc.</option>
                                                            <option>Product Name Desc.</option>
                                                            <option>Buying Price Asc.</option>
                                                            <option>Buying Price Desc.</option>
                                                            <option>Selling Price Asc.</option>
                                                            <option>Selling Price Desc.</option>
                                                            <option>Stock Qty Asc.</option>
                                                            <option>Stock Qty Desc.</option>
                                                            <option>Discount Asc.</option>
                                                            <option>Discount Desc.</option>
                                                            <option>Warranty Asc.</option>
                                                            <option>Warranty Desc.</option>
                                                            <option>Date Cerated Asc.</option>
                                                            <option>Date Cerated Desc.</option>
                                                            <option>Active Status</option>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 10px;">
                                            <div class="col"><input type="search" class="search-box" placeholder="Filter Product List by.." id="search-box" oninput="loadStockTable();"></div>
                                        </div>
                                    </div>
                                    <h4 class="d-flex justify-content-between align-items-center" style="margin-top: 20px;">Stock Details
                                        <button id="stock-deactive-btn" style="display: none;" class="btn btn-primary" type="button"><i id="dac-ico" class="fas fa-ban" style="padding-right: 5px;"></i>
                                            <strong id="dac-sts">Deactivate</strong>
                                        </button>
                                    </h4>
                                    <div class="table-responsive table-bordered prod-table" style="max-height: 400px;">
                                        <table id="stock-table" class="table table-bordered table-hover table-sm">
                                            <thead class="text-center">
                                                <tr>
                                                    <th class="align-middle">ID</th>
                                                    <th class="align-middle">Category</th>
                                                    <th class="align-middle">Brand</th>
                                                    <th class="align-middle">Product</th>
                                                    <th class="align-middle">Buying Price</th>
                                                    <th class="align-middle">Selling Price</th>
                                                    <th class="align-middle">Qty</th>
                                                    <th class="align-middle">Discount (%)</th>
                                                    <th class="align-middle">Warranty</th>
                                                    <th class="align-middle">Date Added</th>
                                                    <th class="align-middle">Acive</th>
                                                    <th style="display: none;">desc</th>
                                                    <th style="display: none;">specs</th>
                                                    <th style="display: none;">imgs</th>
                                                </tr>
                                            </thead>
                                            <tbody id="tbody-stock">

                                            </tbody>
                                        </table>
                                    </div>
                                    <div>
                                        <h4 class="d-flex justify-content-between align-items-center" style="margin-top: 20px;">
                                            Product Details
                                            <button onclick="setActiveStockRow(-1);" class="btn btn-primary" type="button">
                                                <i class="fas fa-undo" style="padding-right: 10px;"></i>Reset Fields
                                            </button>
                                        </h4>
                                        <div class="d-flex flex-column justify-content-between flex-sm-row r-field">
                                            <div class="input-group">
                                                <div class="input-group-prepend">
                                                    <label class="input-group-text">Category</label>
                                                    <select onchange="setActiveStockRow(-1); loadProductSpecTable();" id="cat-combo-box" class="shadow-lg search-select">

                                                    </select>
                                                </div>
                                            </div>
                                            <div class="input-group">
                                                <div class="input-group-prepend">
                                                    <label class="input-group-text">Brand</label>
                                                    <select class="shadow-lg search-select" id="brand-combo-box">

                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col">
                                                <div class="d-flex justify-content-between align-items-center r-field"><span>Name</span><input id="product-name" type="text" oninput="hideToolTip('product-name-tt');"><span class="shake animated field-tooltip" id="product-name-tt"></span></div>
                                            </div>
                                            <div class="col-sm-12 col-md-5 col-lg-4 col-xl-3">
                                                <div class="d-flex justify-content-between align-items-center r-field"><span>Available Qty</span><input id="product-qty" type="number" min="0" oninput="hideToolTip('product-qty-tt');"><span class="shake animated field-tooltip" id="product-qty-tt"></span></div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-sm-12 col-md-6 col-lg-4 col-xl-4">
                                                <div class="d-flex justify-content-between align-items-center r-field"><span>Buying Price (Rs.)</span><input id="product-bprice" type="number" min="0" oninput="hideToolTip('product-bprice-tt');"><span class="shake animated field-tooltip" id="product-bprice-tt"></span></div>
                                            </div>
                                            <div class="col-sm-12 col-md-6 col-lg-4 col-xl-4">
                                                <div class="d-flex justify-content-between align-items-center r-field"><span>Selling Price (Rs.)</span><input id="product-sprice" type="number" min="0" oninput="hideToolTip('product-sprice-tt');"><span class="shake animated field-tooltip" id="product-sprice-tt"></span></div>
                                            </div>
                                            <div class="col-md-12 col-lg-4 col-xl-4">
                                                <div class="d-flex justify-content-between align-items-center r-field"><span>Discount (%)</span><input id="product-discount" type="number" min="0" oninput="hideToolTip('product-discount-tt');"><span class="shake animated field-tooltip" id="product-discount-tt"></span></div>
                                            </div>
                                        </div>
                                        <div class="d-flex justify-content-between align-items-center r-field"><span>Warranty</span><input id="product-warranty" type="text"oninput="hideToolTip('product-warranty-tt');"><span class="shake animated field-tooltip" id="product-warranty-tt"></span></div>
                                        <div class="d-flex justify-content-between r-field"><span>Description</span><textarea  id="product-desc" oninput="hideToolTip('product-desc-tt');"></textarea><span class="shake animated field-tooltip" id="product-desc-tt"></span></div>
                                    </div>
                                    <h4 style="margin-top: 20px;">Product Features</h4>
                                    <div class="table-responsive table-bordered spec-in-table">
                                        <table id="product-spec-table" class="table table-bordered table-sm">
                                            <thead style="background-color: rgba(109,0,0,0.33);">
                                                <tr>
                                                    <th style="display: none;">id</th>
                                                    <th>Specifation</th>
                                                    <th>Value</th>
                                                    <th class="text-center">Unit</th>
                                                </tr>
                                            </thead>
                                            <tbody id="tbody-product-spec">

                                            </tbody>
                                        </table>
                                    </div>
                                    <h4 style="margin-top: 20px;">Product Images</h4>
                                    <div class="row">
                                        <div class="col-sm-6 col-md-6 col-lg-4 col-xl-3">
                                            <div class="d-flex flex-column align-items-center prod-img-chooser">
                                                <img id="s-1" src="assets/img/products/def.png" />
                                                <div class="d-flex justify-content-between" style="margin-top: 20px;">
                                                    <button class="btn btn-primary" type="button" onclick="openFC('f-1');"><i class="fas fa-camera"></i></button>
                                                    <button class="btn btn-primary" id="rmv-btn-1" type="button" style="display: none;margin-left: 10px;" onclick="removeIMG('f-1', 's-1', 'rmv-btn-1', 'assets/img/products/def.png');"><i class="fas fa-trash-alt"></i></button>
                                                </div>
                                                <input class="d-none" type="file" id="f-1" accept="image/*" onchange="viewIMG('f-1', 's-1', 'rmv-btn-1', 'assets/img/products/def.png');" />
                                            </div>
                                        </div>
                                        <div class="col-sm-6 col-md-6 col-lg-4 col-xl-3">
                                            <div class="d-flex flex-column align-items-center prod-img-chooser">
                                                <img id="s-2" src="assets/img/products/def.png" />
                                                <div class="d-flex justify-content-between" style="margin-top: 20px;">
                                                    <button class="btn btn-primary" type="button" onclick="openFC('f-2');"><i class="fas fa-camera"></i></button>
                                                    <button class="btn btn-primary" id="rmv-btn-2" type="button" style="display: none;margin-left: 10px;" onclick="removeIMG('f-2', 's-2', 'rmv-btn-2', 'assets/img/products/def.png');"><i class="fas fa-trash-alt"></i></button>
                                                </div>
                                                <input class="d-none" type="file" id="f-2" accept="image/*" onchange="viewIMG('f-2', 's-2', 'rmv-btn-2', 'assets/img/products/def.png');" />
                                            </div>
                                        </div>
                                        <div class="col-sm-6 col-md-6 col-lg-4 col-xl-3">
                                            <div class="d-flex flex-column align-items-center prod-img-chooser">
                                                <img id="s-3" src="assets/img/products/def.png" />
                                                <div class="d-flex justify-content-between" style="margin-top: 20px;">
                                                    <button class="btn btn-primary" type="button" onclick="openFC('f-3');"><i class="fas fa-camera"></i></button>
                                                    <button class="btn btn-primary" id="rmv-btn-3" type="button" style="display: none;margin-left: 10px;" onclick="removeIMG('f-3', 's-3', 'rmv-btn-3', 'assets/img/products/def.png');"><i class="fas fa-trash-alt"></i></button>
                                                </div><input class="d-none" type="file" id="f-3" accept="image/*" onchange="viewIMG('f-3', 's-3', 'rmv-btn-3', 'assets/img/products/def.png');" />
                                            </div>
                                        </div>
                                        <div class="col-sm-6 col-md-6 col-lg-4 col-xl-3">
                                            <div class="d-flex flex-column align-items-center prod-img-chooser">
                                                <img id="s-4" src="assets/img/products/def.png" />
                                                <div class="d-flex justify-content-between" style="margin-top: 20px;">
                                                    <button class="btn btn-primary" type="button" onclick="openFC('f-4');"><i class="fas fa-camera"></i></button>
                                                    <button class="btn btn-primary" id="rmv-btn-4" type="button" style="display: none;margin-left: 10px;" onclick="removeIMG('f-4', 's-4', 'rmv-btn-4', 'assets/img/products/def.png');"><i class="fas fa-trash-alt"></i></button>
                                                </div>
                                                <input class="d-none" type="file" id="f-4" accept="image/*" onchange="viewIMG('f-4', 's-4', 'rmv-btn-4', 'assets/img/products/def.png');" />
                                            </div>
                                        </div>
                                    </div>

                                    <div class="d-flex justify-content-end r-field">
                                        <button id="stock-del-btn" class="btn btn-primary lst-btn" type="button" style="display: none;"><i class="fas fa-trash-alt"></i>Delete Product</button>
                                        <button id="stock-edit-btn" class="btn btn-primary lst-btn" type="button" style="display: none;"><i class="fas fa-edit"></i>Update Product</button>
                                        <button onclick="addNewProduct();" class="btn btn-primary lst-btn" type="button"><i class="fas fa-plus-circle"></i>Add Product</button>
                                    </div>
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
        <script src="assets/js/prod_mng.js"></script>
        <script>
                                            document.getElementById("dash-link-3").className += " active";
                                            document.getElementById("nav-link-3").className += " active";
        </script>
    </body>
</html>