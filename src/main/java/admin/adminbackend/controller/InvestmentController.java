package admin.adminbackend.controller;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.InvestmentDTO;
import admin.adminbackend.dto.InvestmentHistoryDTO;
import admin.adminbackend.service.InvestmentService;
import admin.adminbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
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
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestBody InvestmentDTO investmentDTO) {

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

    @GetMapping("investmentHistory")
    public ResponseEntity<?> investmentHistory(
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        // Member 정보 조회
        Member member = memberService.getUserDetails(accessToken);

        // 해당 회원의 투자 내역 조회
        List<Investment> listInvestment = investmentService.getInvestmemtListfindByMemberId(member);

        if (listInvestment.isEmpty()) {
            // 투자 내역이 없을 때 404 응답과 메시지 반환
            return new ResponseEntity<>("투자 내역이 없습니다.", HttpStatus.NOT_FOUND);
        }

        // 투자 내역을 DTO로 변환
        List<InvestmentHistoryDTO> investmentDTOList = listInvestment.stream().map(investment -> {
            InvestmentHistoryDTO investmentHistoryDTO = new InvestmentHistoryDTO();
            investmentHistoryDTO.setInvestmentUid(investment.getInvestmentUid());
            investmentHistoryDTO.setAmount(investment.getAmount());
            investmentHistoryDTO.setInvestedAt(investment.getInvestedAt());
            investmentHistoryDTO.setMemberName(member.getName());
            investmentHistoryDTO.setVentureName(investment.getVentureListInfo().getName());
            return investmentHistoryDTO;
        }).collect(Collectors.toList());

        // 성공적으로 조회한 투자 내역 DTO 반환
        return ResponseEntity.ok(investmentDTOList);
    }
}