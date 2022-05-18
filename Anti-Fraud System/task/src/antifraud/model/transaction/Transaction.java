package antifraud.model.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "Transaction")
@Table(name = "transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("transactionId")
    private Long id;
    private Long amount;
    private String ip;
    private String number;
    private RegionCode region;
    private LocalDateTime date;
    private TransactionValidation result;
    private TransactionValidation feedback;

    public String getFeedback() {
        return feedback != null ? feedback.name() : "";
    }
}
