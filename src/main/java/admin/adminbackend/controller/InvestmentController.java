package admin.adminbackend.controller;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.jwt.TokenProvider;
import admin.adminbackend.service.AuthService;
import admin.adminbackend.service.InvestmentService;
import admin.adminbackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InvestmentController {

    private final InvestmentService investmentService;
    private final MyPageService myPageService;
    private final TokenProvider tokenProvider;

    @PostMapping("/createInvest")
    public ResponseEntity<?> createInvestment(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestParam Long ventureId,
            @RequestParam Long amount) {

        // 토큰 유효성 검사
        if (isInvalidToken(accessToken)) {
            log.warn("유효하지 않은 토큰: {}", accessToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 사용자 정보 가져오기
        UserDetails userDetails = getUserDetails(accessToken);
        if (userDetails == null) {
            log.error("사용자 정보를 찾을 수 없습니다. 토큰: {}", accessToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 찾을 수 없습니다.");
        }

        // 사용자 정보로부터 회원 정보 조회
        Member member = myPageService.getMemberInfo(userDetails.getUsername());
        if (member == null) {
            log.error("회원 정보를 찾을 수 없습니다. 사용자: {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보를 찾을 수 없습니다.");
        }

        // Investment 생성
        Investment investment = investmentService.createInvestment(member.getId(), ventureId, amount);
        log.info("투자 생성 완료. 회원 ID: {}, 벤처 ID: {}, 투자 금액: {}", member.getId(), ventureId, amount);

        // ResponseEntity를 통해 응답 반환
        return new ResponseEntity<>(investment, HttpStatus.CREATED);
    }

    // 토큰 유효성 검사 메서드
    private boolean isInvalidToken(String accessToken) {
        return accessToken == null || !tokenProvider.validate(accessToken);
    }

    // 토큰에서 UserDetails 가져오기
    private UserDetails getUserDetails(String accessToken) {
        try {
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            return (UserDetails) authentication.getPrincipal();
        } catch (Exception e) {
            log.error("토큰에서 사용자 정보를 가져오는 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }
}