package antifraud.validation;

import antifraud.model.transaction.RegionCode;
import antifraud.model.transaction.TransactionValidation;
import antifraud.util.AppUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TransactionValidator implements ConstraintValidator<ValidTransaction, String> {

    @Override
    public boolean isValid(String feedback, ConstraintValidatorContext context) {

        AppUtils.valueOf(TransactionValidation.class, feedback);
        return true;
    }
}
