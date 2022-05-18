package antifraud.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RoleDTO {

    private final String username;
    private final String role;
}
