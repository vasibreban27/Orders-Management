package utcn.pt.Model;

import java.sql.Timestamp;

public class Orders {

    private int ordersId;
    private int clientId;
    private int productId;
    private int quantity;
    private Timestamp orderDate;

    public Orders(){}
    public Orders(int id, int clientID, int productID, int quantity, Timestamp orderDate) {
        this.ordersId = id;
        this.clientId = clientID;
        this.productId = productID;
        this.quantity = quantity;
        this.orderDate = orderDate;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientID) {
        this.clientId = clientID;
    }

    public int getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(int id) {
        this.ordersId = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productID) {
        this.productId = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String toString(){
        return "Order: " + ordersId + "for Client " + clientId + "with Product " + productId + "and Quantity " + quantity + ",has date " + orderDate;
    }
}
