package admin.adminbackend.controller.member;

import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.MemberDTO;

import admin.adminbackend.dto.ResponseDTO;
import admin.adminbackend.service.member.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {

    private final MemberServiceImpl memberService;
    private final PasswordEncoder passwordEncoder;


    // 비밀번호 확인
    @PostMapping("/checkPassword")
    public ResponseEntity<Void> checkPassword(
            @RequestParam("password") String password,
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpSession session
    ) {
        Member member = memberService.getUserDetails(accessToken);
        validatePassword(password, member.getPassword());
        session.setAttribute("passwordChecked", true);
        return ResponseEntity.ok().build();
    }

    // 개인 정보 보기
    @GetMapping("/memberInfo")
    public ResponseEntity<ResponseDTO<MemberDTO>> memberInfo(
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("로그인이 필요합니다.", null));
        }

        Member member = memberService.getUserDetails(accessToken);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>("회원 정보를 찾을 수 없습니다.", null));
        }

        MemberDTO memberDTO = member.toMemberDTO();

        return ResponseEntity.ok(new ResponseDTO<>("회원 정보 조회 성공", memberDTO));
    }





    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }
}
