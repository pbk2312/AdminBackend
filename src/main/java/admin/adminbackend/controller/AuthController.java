package admin.adminbackend.controller;

import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.login.LogoutDTO;
import admin.adminbackend.dto.register.MemberRequestDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.dto.token.TokenDTO;
import admin.adminbackend.dto.token.TokenRequestDTO;
<<<<<<< HEAD
=======
import admin.adminbackend.repository.ResetTokenRepository;
>>>>>>> Yoonseo
import admin.adminbackend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("회원 가입 요청이 들어왔습니다.");
        MemberResponseDTO responseDTO = authService.register(memberRequestDTO);
        log.info("회원 가입이 완료되었습니다. 회원 ID: {}", responseDTO.getEmail());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        log.info("로그인 요청이 들어왔습니다.");
        TokenDTO tokenDTO = authService.login(loginDTO);
        log.info("로그인이 완료되었습니다. 반환된 토큰: {}", tokenDTO.getAccessToken());

        // 쿠키 설정
        addCookie(response, "accessToken", tokenDTO.getAccessToken(), 60 * 60);
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/reissuance")
    public ResponseEntity<TokenDTO> reissuance(@RequestBody TokenRequestDTO tokenRequestDTO) {
        log.info("토큰 재발급 요청이 들어왔습니다.");
        TokenDTO tokenDTO = authService.reissuance(tokenRequestDTO);
        log.info("토큰이 재발급되었습니다. 새 토큰: {}", tokenDTO.getAccessToken());
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/sendCertification")
    public ResponseEntity<EmailResponseDTO> sendCertification(@RequestBody EmailRequestDTO emailRequestDTO) {
        log.info("인증 메일 발송 요청이 들어왔습니다.");
        EmailResponseDTO emailResponseDTO = authService.sendCertificationMail(emailRequestDTO);
        log.info("인증 메일이 발송되었습니다. 이메일: {}", emailResponseDTO.getEmail());
        return ResponseEntity.ok(emailResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutDTO> logout(@RequestBody LogoutDTO logoutDTO, HttpServletResponse response) {
        log.info("로그아웃 요청이 들어왔습니다.");
        LogoutDTO result = authService.logout(logoutDTO);
        log.info("로그아웃이 완료되었습니다.");

        // 쿠키 제거
        removeCookie(response, "accessToken");
        return ResponseEntity.ok(result);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
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