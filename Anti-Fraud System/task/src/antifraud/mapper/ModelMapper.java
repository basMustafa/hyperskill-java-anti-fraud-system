package antifraud.mapper;

import antifraud.dto.TransactionDTO;
import antifraud.dto.UserDTO;
import antifraud.model.transaction.RegionCode;
import antifraud.model.transaction.Transaction;
import antifraud.model.user.User;
import antifraud.util.AppUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ModelMapper {

    public User mapToEntity(UserDTO dto) {
        return User.builder()
                .name(dto.getName())
                .username(dto.getUsername().toLowerCase())
                .password(dto.getPassword())
                .build();
    }

    public UserDTO mapToDTO(User entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .username(entity.getUsername())
                .role(entity.getRole().name())
                .build();
    }

    public Transaction mapToEntity(TransactionDTO dto) {
        return Transaction.builder()
                .amount(dto.getAmount())
                .ip(dto.getIp())
                .number(dto.getNumber())
                .region(AppUtils.valueOf(RegionCode.class, dto.getRegion()))
                .date(LocalDateTime.parse(dto.getDate()))
                .build();
    }
}
