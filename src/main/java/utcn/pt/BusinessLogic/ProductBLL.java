package utcn.pt.BusinessLogic;

import utcn.pt.DataAccess.ProductDAO;
import utcn.pt.Model.Product;

import java.util.List;

public class ProductBLL {
    private final ProductDAO dao = new ProductDAO();

    public Product findProduct(int id) {
        Product p = dao.findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Product with id " + id + " not found");
        }
        return p;
    }

    public List<Product> findAllProducts() {
        return dao.findAll();
    }

    //validators for adding a product
    public void addProduct(Product p) {
        if (p.getName() == null || p.getName().isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (p.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (p.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        dao.insert(p);
    }

    public void updateProduct(Product p) {
        Product existing = findProduct(p.getProductId());
        existing.setName(p.getName());
        existing.setPrice(p.getPrice());
        existing.setStock(p.getStock());
        dao.update(existing);
    }

    public void deleteProduct(int id) {
        findProduct(id);
        dao.delete(id);
    }
}
