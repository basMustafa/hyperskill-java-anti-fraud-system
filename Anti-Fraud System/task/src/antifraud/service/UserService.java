package antifraud.service;

import antifraud.dto.ChangeAccessDTO;
import antifraud.dto.RoleDTO;
import antifraud.dto.StatusResponseDTO;
import antifraud.model.user.AccessOperation;
import antifraud.model.user.Role;
import antifraud.model.user.User;
import antifraud.repository.UserRepository;
import antifraud.util.AppUtils;
import antifraud.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists!");
        }
        user.setRole(userRepository.findAll().isEmpty() ? Role.ADMINISTRATOR : Role.MERCHANT);
        user.setAccountNonLocked(user.getRole().equals(Role.ADMINISTRATOR));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc();
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
        userRepository.delete(user);
    }

    public User findUserByUsername(String username) throws ResponseStatusException {
        return userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public User updateUserRole(RoleDTO roleDTO) throws ResponseStatusException {
        User user = findUserByUsername(roleDTO.getUsername());
        Role role = AppUtils.valueOf(Role.class, roleDTO.getRole());

        if (!role.equals(Role.SUPPORT) && !role.equals(Role.MERCHANT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be support or merchant!");
        } else if (user.getRole().equals(role)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already assigned!");
        }
        user.setRole(role);
        return userRepository.save(user);
    }

    public StatusResponseDTO changeAccess(ChangeAccessDTO accessDTO) {
        User user = findUserByUsername(accessDTO.getUsername());
        AccessOperation op = AppUtils.valueOf(AccessOperation.class, accessDTO.getOperation());

        if (user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }
        user.setAccountNonLocked(!Objects.equals(op, AccessOperation.LOCK));
        userRepository.save(user);

        return new StatusResponseDTO(String.format("User %s %sed!", user.getUsername(), op.name().toLowerCase()));
    }
}
