package admin.adminbackend.controller;
////

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.ResetToken;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.login.LogoutDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberRequestDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.dto.token.TokenDTO;
import admin.adminbackend.dto.token.TokenRequestDTO;
import admin.adminbackend.repository.MemberRepository;
import admin.adminbackend.repository.ResetTokenRepository;
import admin.adminbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final ResetTokenRepository resetTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO memberRequestDTO) {
        return ResponseEntity.ok(authService.register(memberRequestDTO));
    }


    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        log.info("로그인 요청이 들어왔습니다.");
        TokenDTO tokenDTO = authService.login(loginDTO);
        log.info("로그인이 완료되었습니다. 반환된 토큰: {}", tokenDTO);
        return ResponseEntity.ok(tokenDTO);
    }


    @PostMapping("/reissuance")
    public ResponseEntity<TokenDTO> reissuance(@RequestBody TokenRequestDTO tokenRequestDTO) {
        return ResponseEntity.ok(authService.reissuance(tokenRequestDTO));
    }

    @PostMapping("/sendCertification")
    public ResponseEntity<EmailResponseDTO> sendCertification(@RequestBody EmailRequestDTO emailRequestDTO) {
        return ResponseEntity.ok(authService.sendCertificationMail(emailRequestDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutDTO> logout(@RequestBody LogoutDTO logoutDTO) {
        return ResponseEntity.ok(authService.logout(logoutDTO));
    }

    @PostMapping("/withdrawalMembership")
    public ResponseEntity<MemberResponseDTO> withdrawalMembership(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.withdrawalMembership(loginDTO));
    }

    @PostMapping("/sendPasswordResetEmail")
    public ResponseEntity<EmailResponseDTO> sendPasswordResetEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        return ResponseEntity.ok(authService.sendPasswordResetEmail(emailRequestDTO));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<String> changePassword(@RequestParam("email") String email, @RequestBody MemberChangePasswordDTO memberChangePasswordDTO) {
        memberChangePasswordDTO.setEmail(email);
        String message = authService.memberChangePassword(memberChangePasswordDTO);
        return ResponseEntity.ok(message);
    }


    @GetMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestParam("token") String resetToken,
                                                 @RequestParam("email") String email) {

        // ResetToken을 검증합니다.
        ResetToken storedToken = resetTokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ResetToken을 찾을 수 없습니다."));

        // 저장된 토큰과 요청된 토큰이 일치하는지 확인합니다.
        if (!storedToken.getResetToken().equals(resetToken)) {
            throw new RuntimeException("유효하지 않은 ResetToken입니다.");
        }

        // 만료 여부를 검사합니다.
        LocalDateTime expiryDate = storedToken.getExpiryDate();
        if (expiryDate != null && expiryDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("ResetToken이 만료되었습니다.");
        }

        // ResetToken이 유효하고 만료되지 않았다면 비밀번호 재설정 페이지로 이동합니다.
        return ResponseEntity.ok("비밀번호 재설정 페이지로 이동합니다...");


    }
}