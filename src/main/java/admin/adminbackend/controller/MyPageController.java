package admin.adminbackend.controller;

import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.MemberDTO;

import admin.adminbackend.service.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
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
    public String memberInfo(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            Model model
    ) {
        // Member 엔티티를 가져옵니다.
        Member member = memberService.getUserDetails(accessToken);


        // Member 엔티티를 MemberDTO로 변환합니다.
        MemberDTO memberDTO = member.toMemberDTO();

        // 모델에 MemberDTO를 추가합니다.
        model.addAttribute("MypageMemberDTO", memberDTO);

        // 뷰를 반환합니다.
        return "mypage/memberInfo";
    }



    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }
}