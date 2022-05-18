package antifraud.controller;

import antifraud.dto.FeedbackDTO;
import antifraud.dto.StatusResponseDTO;
import antifraud.dto.TransactionDTO;
import antifraud.dto.TransactionResponseDTO;
import antifraud.mapper.ModelMapper;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.transaction.Transaction;
import antifraud.service.AntiFraudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {

    private final AntiFraudService antiFraudService;
    private final ModelMapper modelMapper;

    @Autowired
    public AntiFraudController(AntiFraudService antiFraudService, ModelMapper modelMapper) {
        this.antiFraudService = antiFraudService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/transaction")
    public TransactionResponseDTO postTransaction(@Valid @RequestBody TransactionDTO dto) {
        return antiFraudService.validateTransaction(modelMapper.mapToEntity(dto));
    }

    @PutMapping("/transaction")
    public Transaction addFeedback(@Valid @RequestBody FeedbackDTO feedbackDTO) {
        return antiFraudService.addFeedback(feedbackDTO);
    }

    @GetMapping("/history")
    public List<Transaction> getTransactionHistory() {
        return antiFraudService.getTransactionList();
    }

    @GetMapping("/history/{number}")
    public List<Transaction> getTransaction(@PathVariable String number) {
        return antiFraudService.getTransaction(number);
    }

    @PostMapping("/suspicious-ip")
    public SuspiciousIp addSuspiciousIp(@Valid @RequestBody SuspiciousIp suspiciousIp) {
        return antiFraudService.saveIpAddress(suspiciousIp);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public StatusResponseDTO deleteSuspiciousIp(@PathVariable String ip) {
        return antiFraudService.deleteIpAddress(ip);
    }

    @GetMapping("/suspicious-ip")
    public List<SuspiciousIp> getIpList() {
        return antiFraudService.getIpList();
    }

    @PostMapping("/stolencard")
    public StolenCard addStolenCard(@Valid @RequestBody StolenCard stolenCard) {
        return antiFraudService.saveStolenCard(stolenCard);
    }

    @DeleteMapping("/stolencard/{number}")
    public StatusResponseDTO deleteStolenCard(@PathVariable String number) {
        return antiFraudService.deleteStolenCard(number);
    }

    @GetMapping("/stolencard")
    public List<StolenCard> getCardList() {
        return antiFraudService.getCardList();
    }
}
