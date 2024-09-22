package admin.adminbackend.controller;

import admin.adminbackend.domain.IRNotification;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.MemberDTO;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.myPage.IRNotificationDTO;
import admin.adminbackend.dto.myPage.PasswordChangeDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.email.EmailProvider;
import admin.adminbackend.service.MemberServiceImpl;
import admin.adminbackend.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;
    private final MemberServiceImpl memberService;
    private final EmailProvider emailProvider;
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
            HttpSession session,
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