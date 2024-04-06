package admin.adminbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {


    @NotNull
    @Size(min = 3, max = 100)
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 쓰기 전용
    @NotNull
    @Size(min = 6, max = 100)
    private String password;

    @NotNull
    @Size(min = 1, max = 30)
    private String nickname;

}
