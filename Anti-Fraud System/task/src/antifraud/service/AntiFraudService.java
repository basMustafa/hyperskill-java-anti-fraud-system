package antifraud.service;

import antifraud.dto.FeedbackDTO;
import antifraud.dto.StatusResponseDTO;
import antifraud.dto.TransactionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.transaction.RegionCode;
import antifraud.model.transaction.Transaction;
import antifraud.model.transaction.TransactionValidation;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import antifraud.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AntiFraudService {

    private final SuspiciousIpRepository suspiciousIpRepository;
    private final StolenCardRepository stolenCardRepository;
    private final TransactionRepository transactionRepository;
    private static Long maxAllowed = 200L;
    private static Long maxManual = 1500L;

    @Autowired
    public AntiFraudService(SuspiciousIpRepository suspiciousIpRepository,
                            StolenCardRepository stolenCardRepository,
                            TransactionRepository transactionRepository) {
        this.suspiciousIpRepository = suspiciousIpRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.transactionRepository = transactionRepository;
    }

    public SuspiciousIp saveIpAddress(SuspiciousIp suspiciousIp) {
        ipCheck(suspiciousIp.getIp());

        if (suspiciousIpRepository.findByIp(suspiciousIp.getIp()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ip already exists!");
        }
        return suspiciousIpRepository.save(suspiciousIp);
    }

    public StatusResponseDTO deleteIpAddress(String ip) {
        ipCheck(ip);

        SuspiciousIp ipAddress = suspiciousIpRepository.findByIp(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ip not found!"));

        suspiciousIpRepository.delete(ipAddress);
        return new StatusResponseDTO(String.format("IP %s successfully removed!", ip));
    }

    public void ipCheck(String ip) {
        if (!ip.matches( "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong ip format!");
        }
    }

    public List<SuspiciousIp> getIpList() {
        return suspiciousIpRepository.findAllByOrderByIdAsc();
    }

    public StolenCard saveStolenCard(StolenCard stolenCard) {
        stolenCardCheck(stolenCard.getNumber());

        if (stolenCardRepository.findByNumber(stolenCard.getNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Card already exists!");
        }
        return stolenCardRepository.save(stolenCard);
    }

    public StatusResponseDTO deleteStolenCard(String number) {
        stolenCardCheck(number);

        StolenCard stolenCard = stolenCardRepository.findByNumber(number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found!"));

        stolenCardRepository.delete(stolenCard);
        return new StatusResponseDTO(String.format("Card %s successfully removed!", number));
    }

    public List<StolenCard> getCardList() {
        return stolenCardRepository.findAllByOrderByIdAsc();
    }

    public void stolenCardCheck(String cardNumber) {
        int[] cardIntArray = new int[cardNumber.length()];

        for (int i = 0; i < cardNumber.length(); i++) {
            char c = cardNumber.charAt(i);
            cardIntArray[i]=  Integer.parseInt("" + c);
        }

        for (int i = cardIntArray.length - 2; i >= 0; i = i - 2) {
            int num = cardIntArray[i];
            num = num * 2;
            if (num > 9) {
                num = num % 10 + num / 10;
            }
            cardIntArray[i] = num;
        }

        int sum = Arrays.stream(cardIntArray).sum();

        if (sum % 10 != 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong card format!");
        }
    }

    public TransactionResponseDTO validateTransaction(Transaction transaction) {
        ipCheck(transaction.getIp());
        stolenCardCheck(transaction.getNumber());

        Long amountValue = transaction.getAmount();
        boolean ip = suspiciousIpRepository.findByIp(transaction.getIp()).isPresent();
        boolean cardNumber = stolenCardRepository.findByNumber(transaction.getNumber()).isPresent();
        boolean amount = amountValue > maxManual;
        TransactionValidation ipCorrelation = ipCorrelationCheck(transaction);
        TransactionValidation regionCorrelation = regionCorrelationCheck(transaction);
        String result;
        StringBuilder info = new StringBuilder();

        if (amount || cardNumber || ip || ipCorrelation.equals(TransactionValidation.PROHIBITED)
                || regionCorrelation.equals(TransactionValidation.PROHIBITED)) {
            result = TransactionValidation.PROHIBITED.name();
            info.append(amount ? "amount, " : "");
            info.append(cardNumber ? "card-number, " : "");
            info.append(ip ? "ip, " : "");
            info.append(ipCorrelation.equals(TransactionValidation.PROHIBITED) ? "ip-correlation, " : "");
            info.append(regionCorrelation.equals(TransactionValidation.PROHIBITED) ? "region-correlation" : "");
        } else if (amountValue > maxAllowed || ipCorrelation.equals(TransactionValidation.MANUAL_PROCESSING)
                || regionCorrelation.equals(TransactionValidation.MANUAL_PROCESSING)) {
            result = TransactionValidation.MANUAL_PROCESSING.name();
            info.append(amountValue > maxAllowed ? "amount, " : "");
            info.append(ipCorrelation.equals(TransactionValidation.MANUAL_PROCESSING) ? "ip-correlation, " : "");
            info.append(regionCorrelation.equals(TransactionValidation.MANUAL_PROCESSING) ? "region-correlation" : "");
        } else {
            result = TransactionValidation.ALLOWED.name();
            info.append("none");
        }

        String finalInfo;
        finalInfo = info.toString().trim();
        finalInfo = finalInfo.endsWith(",") ? finalInfo.substring(0, finalInfo.length() - 1) : finalInfo;

        transaction.setResult(AppUtils.valueOf(TransactionValidation.class, result));
        transactionRepository.save(transaction);
        return new TransactionResponseDTO(result, finalInfo);
    }

    public TransactionValidation regionCorrelationCheck(Transaction transaction) {
        List<Transaction> transactionList = transactionRepository
                .findAllByNumberAndDateAfterAndDateBefore(
                        transaction.getNumber(),
                        transaction.getDate().minusHours(1L),
                        transaction.getDate()
                );

        Set<RegionCode> regionCodes = transactionList.stream()
                .map(Transaction::getRegion)
                .collect(Collectors.toSet());

        regionCodes.remove(transaction.getRegion());

        if (regionCodes.size() > 2) {
            return TransactionValidation.PROHIBITED;
        } else if (regionCodes.size() == 2) {
            return TransactionValidation.MANUAL_PROCESSING;
        } else {
            return TransactionValidation.ALLOWED;
        }
    }

    public TransactionValidation ipCorrelationCheck(Transaction transaction) {
        List<Transaction> transactionList = transactionRepository
                .findAllByNumberAndDateAfterAndDateBefore(
                        transaction.getNumber(),
                        transaction.getDate().minusHours(1L),
                        transaction.getDate()
                );

        Set<String> ipSet = transactionList.stream()
                .map(Transaction::getIp)
                .collect(Collectors.toSet());

        ipSet.remove(transaction.getIp());

        if (ipSet.size() > 2) {
            return TransactionValidation.PROHIBITED;
        } else if (ipSet.size() == 2) {
            return TransactionValidation.MANUAL_PROCESSING;
        } else {
            return TransactionValidation.ALLOWED;
        }
    }

    public Transaction addFeedback(FeedbackDTO feedbackDTO) {
        Transaction transaction = transactionRepository.findById(feedbackDTO.getTransactionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction not found!"));

        TransactionValidation feedback = AppUtils.valueOf(TransactionValidation.class, feedbackDTO.getFeedback());

        if (!transaction.getFeedback().equals("")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Feedback is already in the database!");
        } else if (transaction.getResult().equals(feedback)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Feedback equals result!");
        }

        if (transaction.getResult().equals(TransactionValidation.ALLOWED)) {
            if (feedback.equals(TransactionValidation.MANUAL_PROCESSING)) {
                decreaseMaxAllowed(transaction.getAmount());
            } else {
                decreaseMaxAllowed(transaction.getAmount());
                decreaseMaxManual(transaction.getAmount());
            }
        } else if (transaction.getResult().equals(TransactionValidation.MANUAL_PROCESSING)) {
            if (feedback.equals(TransactionValidation.ALLOWED)) {
                increaseMaxAllowed(transaction.getAmount());
            } else {
                decreaseMaxManual(transaction.getAmount());
            }
        } else {
            if (feedback.equals(TransactionValidation.ALLOWED)) {
                increaseMaxAllowed(transaction.getAmount());
                increaseMaxManual(transaction.getAmount());
            } else {
                increaseMaxManual(transaction.getAmount());
            }
        }

        transaction.setFeedback(feedback);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionList() {
        return transactionRepository.findAllByOrderByIdAsc();
    }

    public List<Transaction> getTransaction(String cardNumber) {
        stolenCardCheck(cardNumber);
        if (!transactionRepository.existsByNumber(cardNumber)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction with specified number doesnt exist!");
        }

        return transactionRepository.findAllByNumberOrderByIdAsc(cardNumber);
    }

    public void increaseMaxAllowed(long value) {
        maxAllowed = (long) Math.ceil(0.8 * maxAllowed + 0.2 * value);
    }

    public void decreaseMaxAllowed(long value) {
        maxAllowed = (long) Math.ceil(0.8 * maxAllowed - 0.2 * value);
    }

    public void increaseMaxManual(long value) {
        maxManual = (long) Math.ceil(0.8 * maxManual + 0.2 * value);
    }

    public void decreaseMaxManual(long value) {
        maxManual = (long) Math.ceil(0.8 * maxManual - 0.2 * value);
    }


}
