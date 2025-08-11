package utcn.pt.BusinessLogic.validators;

import utcn.pt.Model.Orders;

public class OrderValidator implements Validator<Orders> {

    @Override
    public void validate(Orders order) throws ValidationException {
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        if (order.getClientId() <= 0) {
            throw new ValidationException("Client ID must be positive");
        }
        if (order.getProductId() <= 0) {
            throw new ValidationException("Product ID must be positive");
        }
        if (order.getQuantity() <= 0) {
            throw new ValidationException("Quantity must be greater than zero");
        }
        if (order.getOrderDate() == null) {
            throw new ValidationException("Order date cannot be null");
        }
    }
}