package antifraud.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TransactionResponseDTO {

    private final String result;
    private final String info;
}
