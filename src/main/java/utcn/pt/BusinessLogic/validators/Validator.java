package utcn.pt.BusinessLogic.validators;

public interface Validator<T> {

    void validate(T obj) throws ValidationException;
}
