package antifraud.validation;

import antifraud.model.transaction.RegionCode;
import antifraud.util.AppUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegionValidator implements ConstraintValidator<ValidRegion, String> {

    @Override
    public boolean isValid(String region, ConstraintValidatorContext context) {

        AppUtils.valueOf(RegionCode.class, region);
        return true;
    }
}
