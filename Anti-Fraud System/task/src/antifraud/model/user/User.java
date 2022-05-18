package antifraud.model.user;

import lombok.*;
import javax.persistence.*;

@Entity(name = "User")
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String password;
    private Role role;
    private boolean isAccountNonLocked;
}
