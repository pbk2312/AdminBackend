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
    private String name;        // name 필드 추가
    private String nickname;    // nickname 필드 추가
    private String phoneNumber; // phoneNumber 필드 추가
    private String address;     // address 필드 추가
    private String dateOfBirth; // 생일 추가 (String으로 변환)
    private String memberRole;

}
