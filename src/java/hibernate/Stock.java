package hibernate;
// Generated Mar 10, 2021 4:50:38 PM by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Stock generated by hbm2java
 */
public class Stock  implements java.io.Serializable {


     private Integer id;
     private Product product;
     private Double buyingPrice;
     private Double sellingPrice;
     private Integer qty;
     private Double discount;
     private String warranty;
     private Date createdOn;
     private Byte active;
     private Set carts = new HashSet(0);
     private Set invoiceItems = new HashSet(0);

    public Stock() {
    }

	
    public Stock(Product product) {
        this.product = product;
    }
    public Stock(Product product, Double buyingPrice, Double sellingPrice, Integer qty, Double discount, String warranty, Date createdOn, Byte active, Set carts, Set invoiceItems) {
       this.product = product;
       this.buyingPrice = buyingPrice;
       this.sellingPrice = sellingPrice;
       this.qty = qty;
       this.discount = discount;
       this.warranty = warranty;
       this.createdOn = createdOn;
       this.active = active;
       this.carts = carts;
       this.invoiceItems = invoiceItems;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Product getProduct() {
        return this.product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    public Double getBuyingPrice() {
        return this.buyingPrice;
    }
    
    public void setBuyingPrice(Double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }
    public Double getSellingPrice() {
        return this.sellingPrice;
    }
    
    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public Integer getQty() {
        return this.qty;
    }
    
    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public Double getDiscount() {
        return this.discount;
    }
    
    public void setDiscount(Double discount) {
        this.discount = discount;
    }
    public String getWarranty() {
        return this.warranty;
    }
    
    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }
    public Date getCreatedOn() {
        return this.createdOn;
    }
    
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    public Byte getActive() {
        return this.active;
    }
    
    public void setActive(Byte active) {
        this.active = active;
    }
    public Set getCarts() {
        return this.carts;
    }
    
    public void setCarts(Set carts) {
        this.carts = carts;
    }
    public Set getInvoiceItems() {
        return this.invoiceItems;
    }
    
    public void setInvoiceItems(Set invoiceItems) {
        this.invoiceItems = invoiceItems;
    }




}

