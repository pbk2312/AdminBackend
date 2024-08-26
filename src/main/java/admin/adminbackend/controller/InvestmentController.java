package admin.adminbackend.controller;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.service.AuthService;
import admin.adminbackend.service.InvestmentService;
import admin.adminbackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InvestmentController {

    private final InvestmentService investmentService;
    private final MyPageService myPageService;
    @PostMapping("/createInvest")
    public ResponseEntity<Investment> createInvestment(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestParam Long ventureId,
                                                       @RequestParam Long amount) {

        Member member = myPageService.getMemberInfo(userDetails.getUsername());


        // Investment 생성
        Investment investment = investmentService.createInvestment(member.getId(), ventureId, amount);

        // ResponseEntity를 통해 응답 반환
        return new ResponseEntity<>(investment, HttpStatus.CREATED);
    }
}
