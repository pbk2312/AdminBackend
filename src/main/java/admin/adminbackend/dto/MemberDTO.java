package admin.adminbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDTO {

    private Long id;
    private String email;
    private String memberRole;
}
