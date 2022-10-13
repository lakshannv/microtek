package model;

public class OrderItem {
    private int id;
    private String product;
    private String category;
    private double unit_price;
    private int qty;
    
    public OrderItem(int id, String product, String category, double unit_price, int qty) {
        this.id = id;
        this.product = product;
        this.category = category;
        this.unit_price = unit_price;
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
