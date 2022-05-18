package antifraud.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChangeAccessDTO {

    private final String username;
    private final String operation;
}
