package antifraud.dto;

import antifraud.validation.ValidAmount;
import antifraud.validation.ValidDate;
import antifraud.validation.ValidRegion;
import lombok.Getter;
import javax.validation.constraints.NotEmpty;

@Getter
public class TransactionDTO {

    @ValidAmount
    private Long amount;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String number;
    @ValidRegion
    private String region;
    @ValidDate
    private String date;
}
