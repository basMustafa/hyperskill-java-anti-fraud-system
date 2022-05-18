package antifraud.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";


        try {
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date format!");
        }

        return true;
    }
}
