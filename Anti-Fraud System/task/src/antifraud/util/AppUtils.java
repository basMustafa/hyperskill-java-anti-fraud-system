package antifraud.util;

import antifraud.model.transaction.RegionCode;
import antifraud.model.transaction.TransactionValidation;
import antifraud.model.user.AccessOperation;
import antifraud.model.user.Role;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AppUtils {

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception e) {
            if (enumType.equals(Role.class)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found!");
            } else if (enumType.equals(AccessOperation.class)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Operation!");
            } else if (enumType.equals(RegionCode.class)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong region code!");
            } else if (enumType.equals(TransactionValidation.class)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong transaction!");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
    }
}
