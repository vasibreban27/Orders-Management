package utcn.pt.BusinessLogic.validators;

import java.util.regex.Pattern;

public class EmailValidator implements Validator<String> {

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    @Override
    public void validate(String email) throws ValidationException {
        if (email == null || !pattern.matcher(email).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }
}