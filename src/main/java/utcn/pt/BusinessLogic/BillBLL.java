package utcn.pt.BusinessLogic;

import utcn.pt.DataAccess.BillDAO;
import utcn.pt.Model.Bill;

import java.util.List;

public class BillBLL {
    private final BillDAO dao = new BillDAO();

   //find bill after id
    public Bill findBill(int id) {
        Bill b = dao.findById(id);
        if (b == null) {
            throw new IllegalArgumentException("Bill with id " + id + " not found");
        }
        return b;
    }

    //return all bills
    public List<Bill> findAllBills() {
        return dao.findAll();
    }

    //add bill to log
    public Bill addBill(Bill b) {
        return dao.insert(b);
    }
}
