package admin.adminbackend.investcontract.controller;

import admin.adminbackend.investcontract.domain.InvestorInvestment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.investcontract.domain.VentureInvestment;
import admin.adminbackend.investcontract.dto.InvestorInvestmentDTO;
import admin.adminbackend.dto.InvestmentHistoryDTO;
import admin.adminbackend.investcontract.dto.VentureInvestmentDTO;
import admin.adminbackend.investcontract.service.InvestmentService;
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

    @PostMapping("/submitInvestor")
    public ResponseEntity<?> submitInvestor(@RequestBody InvestorInvestmentDTO investorInvestmentDTO) {
        /*log.info("투자자가 입력한 정보 전송됨. " +
                        "투자자ID : {}, 대표명 : {}, 금액 : {}, 주소 : {}, 상호명 : {}",
                investorInvestmentDTO.getInvestorId(), investorInvestmentDTO.getInvestorName(),
                investorInvestmentDTO.getPrice(), investorInvestmentDTO.getAddress(),
                investorInvestmentDTO.getBusinessName());*/
        // 서비스 호출하여 데이터 저장
        investmentService.saveInvestorInvestment(investorInvestmentDTO);
        log.info("투자자가 입력한 정보 저장됨. " +
                "투자자ID:{}, 대표명:{}, 금액:{}, 주소:{}, 상호명:{}",
                investorInvestmentDTO.getMemberId(), investorInvestmentDTO.getInvestorName(),
                investorInvestmentDTO.getPrice(), investorInvestmentDTO.getAddress(),
                investorInvestmentDTO.getBusinessName());
        //로그에 기업 ID도 출력하려면? 프론트에서 ID값을 받아와야 하나?

        return ResponseEntity.ok(investorInvestmentDTO);
    }

    @PostMapping("/submitVenture")
    public ResponseEntity<?> submitVenture(@RequestBody VentureInvestmentDTO ventureInvestmentDTO) {
        log.info("기업이 입력한 정보 전송됨. 기업ID : {}, 대표명 : {}, 주소 : {}, 상호명 : {}",
                ventureInvestmentDTO.getVentureId(), ventureInvestmentDTO.getName(),
                ventureInvestmentDTO.getAddress(), ventureInvestmentDTO.getBusinessName());
        // 서비스 호출하여 데이터 저장
        investmentService.saveVentureInvestment(ventureInvestmentDTO);
        log.info("기업이 입력한 정보 저장됨. 기업ID : {}, 대표명 : {}, 주소 : {}, 상호명 : {}",
                ventureInvestmentDTO.getVentureId(), ventureInvestmentDTO.getName(),
                ventureInvestmentDTO.getAddress(), ventureInvestmentDTO.getBusinessName());

        return ResponseEntity.ok(ventureInvestmentDTO);
    }

    @PostMapping("/createInvest")
    public ResponseEntity<?> createInvestment(
            @RequestBody InvestorInvestmentDTO investmentDTO) {

        Member member = memberService.getUserDetails(investmentDTO.getAccessToken());
        // Investment 생성
        InvestorInvestment investorInvestment = investmentService.createInvestment(
                member,
                investmentDTO.getVentureId(),
                investmentDTO.getPrice(),
                investmentDTO.getAddress(),
                investmentDTO.getBusinessName());

        log.info("투자 생성 완료. 회원 ID: {}, 벤처 ID: {}, 투자 금액: {}", member.getId(), investmentDTO.getVentureId(),
                investmentDTO.getPrice());

        // InvestmentDTO로 응답 반환
        InvestorInvestmentDTO responseDTO = new InvestorInvestmentDTO();
        //responseDTO.setInvestmentUid(investorInvestment.getInvestmentUid());
        responseDTO.setVentureId(investmentDTO.getVentureId());
        responseDTO.setPrice(investmentDTO.getPrice());
        //responseDTO.setInvestedAt(investorInvestment.getInvestedAt());
        responseDTO.setPaymentId(investorInvestment.getPayment().getPaymentId());
        responseDTO.setAddress(investorInvestment.getAddress());
        responseDTO.setBusinessName(investmentDTO.getBusinessName());
        //responseDTO.setInvestorId(String.valueOf(investorInvestment.getId()));
        //responseDTO.setInvestorId(investorInvestment.getId());

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