package admin.adminbackend.controller;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.ResetToken;
import admin.adminbackend.dto.WithdrawalMembershipDTO;
import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.login.LogoutDTO;
import admin.adminbackend.dto.register.MemberChangePasswordDTO;
import admin.adminbackend.dto.register.MemberRequestDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.dto.token.TokenDTO;
import admin.adminbackend.exception.InvalidTokenException;
import admin.adminbackend.exception.SpecificException;
import admin.adminbackend.repository.ResetTokenRepository;
import admin.adminbackend.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ResetTokenRepository resetTokenRepository;
    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("회원 가입 요청이 들어왔습니다.");
        MemberResponseDTO responseDTO = memberService.register(memberRequestDTO);
        log.info("회원 가입이 완료되었습니다. 회원 ID: {}", responseDTO.getEmail());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        log.info("로그인 요청이 들어왔습니다.");
        TokenDTO tokenDTO = memberService.login(loginDTO);
        log.info("로그인이 완료되었습니다. 반환된 토큰: {}", tokenDTO.getAccessToken());

        // 쿠키 설정
        addCookie(response, "accessToken", tokenDTO.getAccessToken(), 60 * 60);
        return ResponseEntity.ok(tokenDTO);
    }


    @PostMapping("/sendCertification")
    public ResponseEntity<EmailResponseDTO> sendCertification(@RequestBody EmailRequestDTO emailRequestDTO) {
        log.info("인증 메일 발송 요청이 들어왔습니다.");
        EmailResponseDTO emailResponseDTO = memberService.sendCertificationMail(emailRequestDTO);
        log.info("인증 메일이 발송되었습니다. 이메일: {}", emailResponseDTO.getEmail());
        return ResponseEntity.ok(emailResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutDTO> logout(@RequestBody LogoutDTO logoutDTO, HttpServletResponse response) {
        log.info("로그아웃 요청이 들어왔습니다.");
        LogoutDTO result = memberService.logout(logoutDTO);
        log.info("로그아웃이 완료되었습니다.");

        // 쿠키 제거
        removeCookie(response, "accessToken");
        return ResponseEntity.ok(result);
    }


    // 비밀번호 재설정 이메일
    @PostMapping("/sendResetPasswordEmail")
    public ResponseEntity<String> sendResetPasswordEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        try {
            String message = memberService.sendPasswordResetEmail(emailRequestDTO);
            return ResponseEntity.ok(message);
        } catch (SpecificException e) {
            log.error("이메일 전송 중 특정 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 전송 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패: " + e.getMessage());
        }
    }

    // 비밀번호 변경
    @PostMapping("/updatePassword")
    public ResponseEntity<String> changePassword(@RequestParam("token") String resetToken,
                                                 @RequestParam("email") String email,
                                                 @RequestBody MemberChangePasswordDTO memberChangePasswordDTO) {
        try {
            // 요청 데이터 확인
            if (resetToken == null || email == null || memberChangePasswordDTO == null) {
                return ResponseEntity.badRequest().body("요청 데이터가 부족합니다.");
            }


            // 토큰 유효성 검사
            validateResetToken(email, resetToken);

            // 비밀번호 변경
            memberChangePasswordDTO.setEmail(email);
            memberService.memberChangePassword(memberChangePasswordDTO);

            return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
        } catch (InvalidTokenException e) {
            log.warn("비밀번호 변경 시 유효하지 않은 토큰 사용: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        } catch (RuntimeException e) {
            log.error("비밀번호 변경 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 중 오류가 발생했습니다.");
        }
    }


    private void validateResetToken(String email, String resetToken) {
        ResetToken storedToken = resetTokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ResetToken을 찾을 수 없습니다."));

        if (!storedToken.getResetToken().equals(resetToken)) {
            throw new RuntimeException("유효하지 않은 ResetToken입니다.");
        }

        if (storedToken.getExpiryDate() != null && storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("ResetToken이 만료되었습니다.");
        }
    }

    // 회원 탈퇴
    @PostMapping("/deleteAccount")
    public ResponseEntity<Void> deleteAccount(@RequestBody WithdrawalMembershipDTO withdrawalMembershipDTO,
                                              @CookieValue(value = "accessToken", required = false) String accessToken,
                                              HttpServletResponse response) {
        log.info("회원 탈퇴 요청...");
        Member member = memberService.getUserDetails(accessToken);
        withdrawalMembershipDTO.setEmail(member.getEmail());

        try {
            String message = memberService.deleteAccount(withdrawalMembershipDTO);
            if ("회원 정보가 정상적으로 삭제되었습니다.".equals(message)) {
                log.info("회원 정보 삭제 완료");
                removeCookie(response, "accessToken");
                return ResponseEntity.ok().build();
            } else {
                log.warn("회원 탈퇴 실패: {}", message);
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("회원 탈퇴 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false); // https로 할때는 true로
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 쿠키 즉시 만료
        response.addCookie(cookie);
    }
}