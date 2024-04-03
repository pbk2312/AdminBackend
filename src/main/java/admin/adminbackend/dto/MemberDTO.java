package admin.adminbackend.dto;

import admin.adminbackend.domain.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.StringReader;
import java.util.Collection;

@Getter
@Setter
@ToString
public class MemberDTO extends User {

    private String name;
    private String password;
    private String email;
    private Role role;

    public MemberDTO(String username, String password, Collection<? extends GrantedAuthority> authorities, String name, String email, Role role) {
        super(username, password, authorities);
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }

}
