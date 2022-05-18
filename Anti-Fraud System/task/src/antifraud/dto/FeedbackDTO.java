package antifraud.dto;

import antifraud.validation.ValidTransaction;
import lombok.Getter;

@Getter
public class FeedbackDTO {

    private Long transactionId;
    @ValidTransaction
    private String feedback;
}
