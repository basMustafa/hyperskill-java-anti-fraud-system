package antifraud.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmountValidator implements ConstraintValidator<ValidAmount, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        if (value == null || value <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount has to be greater than 0!");
        }
        return true;
    }
}
