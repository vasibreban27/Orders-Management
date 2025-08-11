package utcn.pt.BusinessLogic;

import utcn.pt.BusinessLogic.validators.OrderValidator;
import utcn.pt.BusinessLogic.validators.ValidationException;
import utcn.pt.DataAccess.BillDAO;
import utcn.pt.DataAccess.ClientDAO;
import utcn.pt.DataAccess.OrdersDAO;
import utcn.pt.DataAccess.ProductDAO;
import utcn.pt.Model.Bill;
import utcn.pt.Model.Client;
import utcn.pt.Model.Orders;
import utcn.pt.Model.Product;

import java.sql.Timestamp;
import java.util.List;

public class OrdersBLL {
    private final OrdersDAO   ordersDao  = new OrdersDAO();
    private final ClientDAO   clientDao  = new ClientDAO();
    private final ProductDAO  productDao = new ProductDAO();
    private final BillDAO     billDao    = new BillDAO();

    public Orders findOrder(int id) {
        Orders o = ordersDao.findById(id);
        if (o == null) {
            throw new IllegalArgumentException("Order with id " + id + " not found");
        }
        return o;
    }

    public List<Orders> findAllOrders() {
        return ordersDao.findAll();
    }

    public void addOrder(Orders order) {
        //  check client
        Client client = clientDao.findById(order.getClientId());
        if (client == null) {
            throw new IllegalArgumentException("Client with id " + order.getClientId() + " not found");
        }

        // check product & stock
        Product product = productDao.findById(order.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product with id " + order.getProductId() + " not found");
        }
        int qty = order.getQuantity();
        if (qty <= 0) {
            throw new IllegalArgumentException("Order quantity must be positive");
        }
        if (product.getStock() < qty) {
            throw new IllegalArgumentException(
                    "Insufficient stock: have " + product.getStock() + ", tried to order " + qty);
        }

        //  deduct stock
        product.setStock(product.getStock() - qty);
        productDao.update(product);

        //  insert the order
        try {
            OrderValidator validator = new OrderValidator();
            validator.validate(order);
            ordersDao.insert(order);
        }catch(ValidationException e) {
            throw new IllegalArgumentException("Not valid order arguments");
        }

        //  create and save bill
        double total = product.getPrice() * qty;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Bill bill = new Bill(
                0,                      // db auto increment the id
                order.getClientId(),
                order.getProductId(),
                qty,
                now,                    // timestamp --now
                total
        );
        billDao.insert(bill);
    }

    public void updateOrder(Orders updated) {
        Orders existing = findOrder(updated.getOrdersId());

        Product product = productDao.findById(existing.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product with id " + existing.getProductId() + " not found");
        }

        int oldQty = existing.getQuantity();
        int newQty = updated.getQuantity();
        int diff   = newQty - oldQty;
        if (diff > 0 && product.getStock() < diff) {
            throw new IllegalArgumentException("Cannot increase to " + newQty
                    + "; only " + product.getStock() + " left in stock");
        }

        // adjust stock
        product.setStock(product.getStock() - diff);
        productDao.update(product);

        // now apply all changes from the ui model
        existing.setClientId(  updated.getClientId() );
        existing.setProductId( updated.getProductId() );
        existing.setQuantity(  newQty );
        existing.setOrderDate(updated.getOrderDate());

        ordersDao.update(existing);
    }

    public void deleteOrder(int id) {
        Orders o = findOrder(id);

        Product product = productDao.findById(o.getProductId());
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product with id " + o.getProductId() + " not found");
        }

        // restore stock
        product.setStock(product.getStock() + o.getQuantity());
        productDao.update(product);

        // remove the order
        ordersDao.delete(id);
        // bills are not deleted
    }
}
