package Validation;

public interface IValidator<T> {
    void validate(T entity) throws ValidationException;
}
