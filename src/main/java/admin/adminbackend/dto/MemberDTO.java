package admin.adminbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class MemberDTO {

    private String email;
    private String name;        // name 필드 추가
    private String nickname;    // nickname 필드 추가
    private String phoneNumber; // phoneNumber 필드 추가
    private String address;     // address 필드 추가
    private LocalDate dateOfBirth; // 생일 추가 (String으로 변환)
    private String memberRole;


}
