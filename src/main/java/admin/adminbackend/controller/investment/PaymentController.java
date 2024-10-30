package admin.adminbackend.controller.investment;


import admin.adminbackend.domain.InvestorInvestment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.dto.payment.PaymentCallbackRequest;
import admin.adminbackend.dto.payment.PaymentDTO;
import admin.adminbackend.repository.investment.InvestmentRepository;
import admin.adminbackend.service.investment.PaymentService;
import admin.adminbackend.service.member.MemberService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MemberService memberService;
    private final InvestmentRepository investmentRepository;


    @GetMapping("/payment")
    public ResponseEntity<?> paymentPage(
            @RequestParam("investmentId") Long investmentId,
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {

        Member member = memberService.getUserDetails(accessToken);

        // 투자 내역 조회
        InvestorInvestment investorInvestment = investmentRepository.findById(investmentId).orElseThrow(() -> {
            log.error("투자 정보를 찾을 수 없습니다. investmentId: {}", investmentId);
            return new RuntimeException("투자 정보를 찾을 수 없습니다.");
        });

        String investmentUid = investorInvestment.getInvestmentUid();
        log.info("investmentUid: {}", investmentUid);
        PaymentDTO requestDto = paymentService.findRequestDto(investmentUid);

        // PaymentDTO에 필요한 정보 세팅
        requestDto.setMemberEmail(member.getEmail());
        requestDto.setVentureName(investorInvestment.getVentureListInfo().getName());
        requestDto.setTotalPrice(investorInvestment.getPrice());

        // JSON 응답 반환
        return new ResponseEntity<>(requestDto, HttpStatus.OK);
    }


    @ResponseBody
    @PostMapping("/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request) {
        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);

        log.info("결제 응답={}", iamportResponse.getResponse().toString());

        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
    }
}
