package admin.adminbackend.investcontract.controller;

import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.ResponseDTO;
import admin.adminbackend.investcontract.domain.InvestorInvestment;
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
    public ResponseEntity<?> submitInvestor(@RequestBody InvestorInvestmentDTO investorInvestmentDTO,
                                            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {

        Member member = memberService.getUserDetails(accessToken);
        /*log.info("투자자가 입력한 정보 전송됨. " +
                        "투자자ID : {}, 대표명 : {}, 금액 : {}, 주소 : {}, 상호명 : {}",
                investorInvestmentDTO.getInvestorId(), investorInvestmentDTO.getInvestorName(),
                investorInvestmentDTO.getPrice(), investorInvestmentDTO.getAddress(),
                investorInvestmentDTO.getBusinessName());*/
        // 서비스 호출하여 데이터 저장
        investmentService.saveInvestorInvestment(investorInvestmentDTO,member);
        log.info("투자자가 입력한 정보 저장됨. " +
                        "투자자ID:{}, 대표명:{}, 금액:{}, 주소:{}, 상호명:{}",
                investorInvestmentDTO.getMemberId(), investorInvestmentDTO.getInvestorName(),
                investorInvestmentDTO.getPrice(), investorInvestmentDTO.getAddress(),
                investorInvestmentDTO.getBusinessName());
        //로그에 기업 ID도 출력하려면? 프론트에서 ID값을 받아와야 하나?

        return ResponseEntity.ok(investorInvestmentDTO);
    }

    @PostMapping("/submitVenture")
    public ResponseEntity<?> submitVenture(@RequestBody VentureInvestmentDTO ventureInvestmentDTO,
                                           @CookieValue(value = "accessToken", required = false) String accessToken) {
        Member member = memberService.getUserDetails(accessToken);
        Long ventureId = member.getVentureListInfo().getId();
        log.info("기업이 입력한 정보 전송됨. 기업ID : {}, 대표명 : {}, 주소 : {}, 상호명 : {}",
                ventureId, ventureInvestmentDTO.getName(),
                ventureInvestmentDTO.getAddress(), ventureInvestmentDTO.getBusinessName());
        ventureInvestmentDTO.setVentureId(ventureId);
        // 서비스 호출하여 데이터 저장
        Long investmentId = investmentService.saveVentureInvestment(ventureInvestmentDTO, member);
        log.info("기업이 입력한 정보 저장됨. 기업ID : {}, 대표명 : {}, 주소 : {}, 상호명 : {}",
                ventureInvestmentDTO.getVentureId(), ventureInvestmentDTO.getName(),
                ventureInvestmentDTO.getAddress(), ventureInvestmentDTO.getBusinessName());
        ventureInvestmentDTO.setVentureInvestmentId(investmentId);

        log.info("investmentId : {} " ,investmentId);

        return ResponseEntity.ok(ventureInvestmentDTO);
    }

    @PostMapping("/createInvest")
    public ResponseEntity<?> createInvestment(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @RequestBody InvestorInvestmentDTO investmentDTO) {

        Member member = memberService.getUserDetails(accessToken);
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
        responseDTO.setInvestmentUid(investorInvestment.getInvestmentUid());
        responseDTO.setVentureId(investmentDTO.getVentureId());
        responseDTO.setPrice(investmentDTO.getPrice());
        responseDTO.setInvestedAt(investorInvestment.getInvestedAt());
        responseDTO.setPaymentId(investorInvestment.getPayment().getPaymentId());
        responseDTO.setAddress(investorInvestment.getAddress());
        responseDTO.setBusinessName(investmentDTO.getBusinessName());
        responseDTO.setInvestorId(investorInvestment.getId());
        responseDTO.setId(investorInvestment.getId());
        log.info("responseDTO : {} " ,responseDTO);

        return new ResponseEntity<>(responseDTO.getId(), HttpStatus.CREATED);
    }


    @GetMapping("/investmentHistory")
    public ResponseEntity<ResponseDTO<List<InvestmentHistoryDTO>>> getInvestmentHistory(
            @CookieValue(value = "accessToken", required = false) String accessToken) {

        // 회원 정보 가져오기
        Member member = memberService.getUserDetails(accessToken);

        // 투자 내역 가져오기 (Investment -> InvestmentHistoryDTO로 변환된 리스트)
        List<InvestmentHistoryDTO> investmentListByMemberId = investmentService.getInvestmentListByMemberId(member);

        // ResponseDTO 객체 생성 - 메시지와 데이터를 함께 전달
        ResponseDTO<List<InvestmentHistoryDTO>> response = new ResponseDTO<>("투자 내역 조회 성공", investmentListByMemberId);


        return ResponseEntity.ok(response);
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
