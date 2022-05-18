package antifraud.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteDTO {

    private final String username;
    private final String status = "Deleted successfully!";
}
