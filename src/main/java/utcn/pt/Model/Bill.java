package utcn.pt.Model;
import java.sql.Timestamp;

public record Bill(
        int         id,           // primary key
        int         clientId,     // customer
        int         productId,    // what was sold
        int         quantity,     // how many
        Timestamp billDate,     // when it happened
        double      totalPrice    // computed price
) {
    public int        getId()         { return id;        }
    public int        getClientId()   { return clientId;  }
    public int        getProductId()  { return productId; }
    public int        getQuantity()   { return quantity;  }
    public Timestamp  getBillDate()   { return billDate;  }
    public double     getTotalPrice() { return totalPrice;}
}