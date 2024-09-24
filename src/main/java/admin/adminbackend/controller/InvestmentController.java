package admin.adminbackend.controller;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.service.InvestmentService;
import admin.adminbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InvestmentController {

    private final InvestmentService investmentService;
    private final MemberService memberService;



    // 투자 저
    @PostMapping("/createInvest")
    public ResponseEntity<?> createInvestment(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestParam Long ventureId,
            @RequestParam Long amount) {

        Member member = memberService.getUserDetails(accessToken);

        // Investment 생성
        Investment investment = investmentService.createInvestment(member.getId(), ventureId, amount);
        log.info("투자 생성 완료. 회원 ID: {}, 벤처 ID: {}, 투자 금액: {}", member.getId(), ventureId, amount);

        // ResponseEntity를 통해 응답 반환
        return new ResponseEntity<>(investment, HttpStatus.CREATED);
    }


}