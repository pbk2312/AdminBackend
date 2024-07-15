package admin.adminbackend.controller;


import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.myPage.MyPageRequestDTO;
import admin.adminbackend.dto.myPage.PasswordChangeDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.service.AuthService;
import admin.adminbackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Slf4j
public class MyPageController {


    private final MyPageService myPageService;
    private final AuthService authService;


    @PostMapping("/check")
    public ResponseEntity<String> checkMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody String password
    ) {
        if (userDetails == null) {
            throw new IllegalArgumentException("사용자 정보를 가져올 수 없습니다.");
        }

        String userEmail = userDetails.getUsername();
        boolean isPasswordCorrect = myPageService.checkPassword(userEmail, password);

        if (isPasswordCorrect) {
            return ResponseEntity.ok("비밀번호 확인 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordChangeDTO passwordChangeDTO
    ) {
        String username = userDetails.getUsername();
        String message = myPageService.changePassword(username, passwordChangeDTO.getChangePassword(), passwordChangeDTO.getChangeCheckPassword());
        return ResponseEntity.ok(message);
    }


    @PostMapping("/withdrawalMembership")
    public ResponseEntity<MemberResponseDTO> withdrawalMembership(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody LoginDTO loginDTO) {
        loginDTO.setEmail(userDetails.getUsername());
        return ResponseEntity.ok(authService.withdrawalMembership(loginDTO));
    }

    @PostMapping("/sendPasswordResetEmail")
    public ResponseEntity<EmailResponseDTO> sendPasswordResetEmail(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("회원 탈퇴 이메일을 전송합니다...");
        if (userDetails == null){
            log.info("userDetails = Null");
        }
        log.info("회원 탈퇴를 진행하는 이메일 : {}" ,userDetails.getUsername());
        EmailRequestDTO requestDTO = new EmailRequestDTO();
        requestDTO.setEmail(userDetails.getUsername());
        return ResponseEntity.ok(authService.sendPasswordResetEmail(requestDTO));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MemberChangePasswordDTO memberChangePasswordDTO) {
        String email = userDetails.getUsername();
        memberChangePasswordDTO.setEmail(email);
        String message = authService.memberChangePassword(memberChangePasswordDTO);
        return ResponseEntity.ok(message);
    }


}
