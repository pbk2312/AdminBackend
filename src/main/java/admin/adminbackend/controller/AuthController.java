package admin.adminbackend.controller;

import admin.adminbackend.dto.MemberReqeustDTO;
import admin.adminbackend.dto.MemberResponseDTO;
import admin.adminbackend.dto.TokenDTO;
import admin.adminbackend.dto.TokenRequestDTO;
import admin.adminbackend.service.AuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberReqeustDTO memberReqeustDTO) {
        return ResponseEntity.ok(authService.regiter(memberReqeustDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody MemberReqeustDTO memberReqeustDTO) {
        return ResponseEntity.ok(authService.login(memberReqeustDTO));
    }

    @PostMapping("/reissuance")
    public ResponseEntity<TokenDTO> reissuance(@RequestBody TokenRequestDTO tokenRequestDTO) {
        return ResponseEntity.ok(authService.reissuance(tokenRequestDTO));
    }


}
