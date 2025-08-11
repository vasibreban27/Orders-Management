package utcn.pt.Model;

public class Product {
    private int productId;
    private String name;
    private double price;
    private int stock;

    public Product(){}
    public Product(int id, String name, double price, int stock) {
        this.productId = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int id) {
        this.productId = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String toString() {
        return "Product with id " + productId + " ,name " + name + " ,at price " + price + " total quantity: " + stock;
    }
}
