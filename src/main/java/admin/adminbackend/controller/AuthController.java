package admin.adminbackend.controller;

import admin.adminbackend.dto.email.EmailRequestDTO;
import admin.adminbackend.dto.email.EmailResponseDTO;
import admin.adminbackend.dto.login.LoginDTO;
import admin.adminbackend.dto.login.LogoutDTO;
import admin.adminbackend.dto.register.MemberRequestDTO;
import admin.adminbackend.dto.register.MemberResponseDTO;
import admin.adminbackend.dto.token.TokenDTO;
import admin.adminbackend.dto.token.TokenRequestDTO;
import admin.adminbackend.service.AuthService;
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






}