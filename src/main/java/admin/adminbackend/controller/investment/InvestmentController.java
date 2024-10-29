package admin.adminbackend.controller.investment;

import admin.adminbackend.domain.InvestorInvestment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.InvestmentDTO;
import admin.adminbackend.dto.InvestmentHistoryDTO;
import admin.adminbackend.service.investment.InvestmentService;
import admin.adminbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InvestmentController {

    private final InvestmentService investmentService;
    private final MemberService memberService;


    @PostMapping("/createInvest")
    public ResponseEntity<?> createInvestment(
            @RequestBody InvestmentDTO investmentDTO) {

        Member member = memberService.getUserDetails(investmentDTO.getAccessToken());
        // Investment 생성
        InvestorInvestment investorInvestment = investmentService.createInvestment(
                member,
                investmentDTO.getVentureId(),
                investmentDTO.getAmount(),
                investmentDTO.getAddress(),
                investmentDTO.getBusinessName());

        log.info("투자 생성 완료. 회원 ID: {}, 벤처 ID: {}, 투자 금액: {}", member.getId(), investmentDTO.getVentureId(),
                investmentDTO.getAmount());

        // InvestmentDTO로 응답 반환
        InvestmentDTO responseDTO = new InvestmentDTO();
        responseDTO.setInvestmentUid(investorInvestment.getInvestmentUid());
        responseDTO.setVentureId(investmentDTO.getVentureId());
        responseDTO.setAmount(investmentDTO.getAmount());
        responseDTO.setInvestedAt(investorInvestment.getInvestedAt());
        responseDTO.setPaymentId(investorInvestment.getPayment().getId());
        responseDTO.setAddress(investorInvestment.getAddress());
        responseDTO.setBusinessName(investmentDTO.getBusinessName());
        responseDTO.setInvestmentId(String.valueOf(investorInvestment.getId()));

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("investmentHistory")
    public ResponseEntity<?> investmentHistory(
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        // Member 정보 조회
        Member member = memberService.getUserDetails(accessToken);

        // 해당 회원의 투자 내역 조회
        List<InvestmentHistoryDTO> investmentListByMemberId = investmentService.getInvestmentListByMemberId(member);

        if (investmentListByMemberId.isEmpty()) {
            // 투자 내역이 없을 때 404 응답과 메시지 반환
            return new ResponseEntity<>("투자 내역이 없습니다.", HttpStatus.NOT_FOUND);
        }

        // 성공적으로 조회한 투자 내역 DTO 반환
        return ResponseEntity.ok(investmentListByMemberId);
    }


    @GetMapping("/ventureInvestmentHistory")
    public ResponseEntity<?> ventureInvestmentHistory(
            @CookieValue(value = "accessToken", required = false) String accessToken) {

        Member member = memberService.getUserDetails(accessToken);

        // 투자 내역 조회
        List<InvestmentHistoryDTO> investmentHistory = investmentService.getVentureInvestmentListByMemberId(member);

        return ResponseEntity.ok(investmentHistory);
    }

}