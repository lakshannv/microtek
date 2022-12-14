package hibernate;
// Generated Mar 10, 2021 4:50:38 PM by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * ProductReview generated by hbm2java
 */
public class ProductReview  implements java.io.Serializable {


     private ProductReviewId id;
     private Customer customer;
     private Product product;
     private Integer rating;
     private String content;
     private Date datetimestamp;

    public ProductReview() {
    }

	
    public ProductReview(ProductReviewId id, Customer customer, Product product) {
        this.id = id;
        this.customer = customer;
        this.product = product;
    }
    public ProductReview(ProductReviewId id, Customer customer, Product product, Integer rating, String content, Date datetimestamp) {
       this.id = id;
       this.customer = customer;
       this.product = product;
       this.rating = rating;
       this.content = content;
       this.datetimestamp = datetimestamp;
    }
   
    public ProductReviewId getId() {
        return this.id;
    }
    
    public void setId(ProductReviewId id) {
        this.id = id;
    }
    public Customer getCustomer() {
        return this.customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public Product getProduct() {
        return this.product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    public Integer getRating() {
        return this.rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getContent() {
        return this.content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    public Date getDatetimestamp() {
        return this.datetimestamp;
    }
    
    public void setDatetimestamp(Date datetimestamp) {
        this.datetimestamp = datetimestamp;
    }




}


