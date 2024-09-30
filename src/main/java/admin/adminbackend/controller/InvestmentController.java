package admin.adminbackend.controller;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.InvestmentDTO;
import admin.adminbackend.service.InvestmentService;
import admin.adminbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InvestmentController {

    private final InvestmentService investmentService;
    private final MemberService memberService;



    // 투자
    @PostMapping("/createInvest")
    public ResponseEntity<?> createInvestment(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestBody InvestmentDTO investmentDTO) {

        // AccessToken을 이용해 사용자 정보 조회
        Member member = memberService.getUserDetails(accessToken);

        // Investment 생성
        Investment investment = investmentService.createInvestment(
                member.getId(),
                investmentDTO.getVentureId(),
                investmentDTO.getAmount());

        log.info("투자 생성 완료. 회원 ID: {}, 벤처 ID: {}, 투자 금액: {}", member.getId(), investmentDTO.getVentureId(), investmentDTO.getAmount());

        // InvestmentDTO로 응답 반환
        InvestmentDTO responseDTO = new InvestmentDTO();
        responseDTO.setId(investment.getId());
        responseDTO.setInvestmentUid(investment.getInvestmentUid());
        responseDTO.setMemberId(member.getId());
        responseDTO.setVentureId(investmentDTO.getVentureId());
        responseDTO.setAmount(investmentDTO.getAmount());
        responseDTO.setInvestedAt(investment.getInvestedAt());

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }


}