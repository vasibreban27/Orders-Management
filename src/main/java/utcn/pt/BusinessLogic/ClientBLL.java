package utcn.pt.BusinessLogic;

import utcn.pt.BusinessLogic.validators.EmailValidator;
import utcn.pt.DataAccess.ClientDAO;
import utcn.pt.DataAccess.ProductDAO;
import utcn.pt.DataAccess.OrdersDAO;
import utcn.pt.Model.Client;
import utcn.pt.Model.Product;
import utcn.pt.Model.Orders;

import java.util.List;

public class ClientBLL {
    private final ClientDAO dao = new ClientDAO();

    public Client findClient(int id) {
        Client c = dao.findById(id);
        if (c == null) {
            throw new IllegalArgumentException("Client with id " + id + " not found");
        }
        return c;
    }

    public List<Client> findAllClients() {
        return dao.findAll();
    }

    public void addClient(Client c) {
        if (c.getName() == null || c.getName().isBlank()) {
            throw new IllegalArgumentException("Client name cannot be empty");
        }
        //to do:: add email check validator here
        try {
            EmailValidator emailValidator = new EmailValidator();
            emailValidator.validate(c.getEmail());
        }catch(Exception e) {
            throw new IllegalArgumentException("Invalid email address", e);
        }
        dao.insert(c);
    }

    public void updateClient(Client c) {
        findClient(c.getClientId());
        dao.update(c);
    }

    public void deleteClient(int id) {
        findClient(id);
        dao.delete(id);
    }
}
