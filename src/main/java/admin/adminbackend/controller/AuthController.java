package admin.adminbackend.controller;

import admin.adminbackend.dto.MemberReqeustDTO;
import admin.adminbackend.dto.MemberResponseDTO;
import admin.adminbackend.dto.TokenDTO;
import admin.adminbackend.dto.TokenRequestDTO;
import admin.adminbackend.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberReqeustDTO memberReqeustDTO) {
        return ResponseEntity.ok(authService.register(memberReqeustDTO));
    }


    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody MemberReqeustDTO memberReqeustDTO) {
        log.info("로그인 요청이 들어왔습니다.");
        TokenDTO tokenDTO = authService.login(memberReqeustDTO);
        log.info("로그인이 완료되었습니다. 반환된 토큰: {}", tokenDTO);
        return ResponseEntity.ok(tokenDTO);
    }



    @PostMapping("/reissuance")
    public ResponseEntity<TokenDTO> reissuance(@RequestBody TokenRequestDTO tokenRequestDTO) {
        return ResponseEntity.ok(authService.reissuance(tokenRequestDTO));
    }


}
