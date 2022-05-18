package antifraud.controller;

import antifraud.dto.*;
import antifraud.mapper.ModelMapper;
import antifraud.model.user.User;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerUser(@Valid @RequestBody UserDTO dto) {
        User user = userService.saveUser(modelMapper.mapToEntity(dto));
        return modelMapper.mapToDTO(user);
    }

    @GetMapping("/list")
    public List<UserDTO> getUserList() {
        return userService.getAllUsers().stream()
                .map(modelMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/user/{username}")
    public DeleteDTO deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return new DeleteDTO(username);
    }

    @PutMapping("/role")
    public UserDTO updateUserRole(@RequestBody RoleDTO roleDTO) {
        User user = userService.updateUserRole(roleDTO);
        return modelMapper.mapToDTO(user);
    }

    @PutMapping("/access")
    public StatusResponseDTO changeUserAccess(@RequestBody ChangeAccessDTO accessDTO) {
        return userService.changeAccess(accessDTO);
    }
}
