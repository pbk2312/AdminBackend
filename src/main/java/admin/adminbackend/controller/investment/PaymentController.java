package admin.adminbackend.controller.investment;


import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.dto.payment.PaymentCallbackRequest;
import admin.adminbackend.dto.payment.PaymentCancelDTO;
import admin.adminbackend.dto.payment.PaymentDTO;
import admin.adminbackend.investcontract.domain.InvestorInvestment;
import admin.adminbackend.investcontract.repository.InvestorInvestmentRepository;
import admin.adminbackend.investcontract.service.InvestmentService;
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
    private final InvestorInvestmentRepository investmentRepository;
    private final InvestmentService investmentService;


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


    @PostMapping("/payment/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody PaymentCancelDTO paymentCancelDTO) {
        try {
            // 결제 취소 처리
            paymentService.cancelInvestment(paymentCancelDTO);

            // 성공적인 경우 200 OK 응답
            return ResponseEntity.ok("결제 취소 요청이 성공적으로 처리되었습니다.");

        } catch (IllegalArgumentException e) {
            // 유효하지 않은 결제 정보 예외 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 결제 정보입니다: " + e.getMessage());
        } catch (RuntimeException e) {
            // 비즈니스 로직 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결제 취소 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결제 취소 요청 처리 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @GetMapping("/venture-total-price")
    public ResponseEntity<Long> getTotalCompletedInvestment(@CookieValue(value = "accessToken", required = false) String accessToken) {

        Member member = memberService.getUserDetails(accessToken);

        VentureListInfo ventureListInfo = member.getVentureListInfo();
        Long totalCompletedInvestment = investmentService.getPrice(ventureListInfo);

        return ResponseEntity.ok(totalCompletedInvestment);
    }



}
