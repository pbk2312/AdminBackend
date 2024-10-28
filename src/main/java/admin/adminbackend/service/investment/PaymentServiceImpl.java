package admin.adminbackend.service.investment;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.PaymentStatus;
import admin.adminbackend.dto.payment.PaymentCallbackRequest;
import admin.adminbackend.dto.payment.PaymentCancelDTO;
import admin.adminbackend.dto.payment.PaymentDTO;
import admin.adminbackend.repository.investment.InvestmentRepository;
import admin.adminbackend.repository.investment.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;


@Service
@Log4j2
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final InvestmentRepository investmentRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Override
    public PaymentDTO findRequestDto(String investmentUid) {
        Investment investment = investmentRepository.findInvestmentAndPaymentAndMember(investmentUid)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않아요"));

        return PaymentDTO.builder()
                .investmentUid(investmentUid)
                .memberId(investment.getInvestor().getId())
                .ventureName(investment.getVentureListInfo().getName())
                .build();
    }

    @Override
    @Transactional
    public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {
        try {
            log.info("결제 시도..");

            // 결제 단건 조회(아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
            // 투자 내역 조회
            Investment investment = investmentRepository.findInvestmentAndPaymentAndMember(request.getInvestmentUid())
                    .orElseThrow(() -> new IllegalArgumentException("투자 내역이 없습니다."));

            // 결제 완료가 아니면
            if (!iamportResponse.getResponse().getStatus().equals("paid")) {
                // 주문, 결제 삭제
                investmentRepository.delete(investment);
                paymentRepository.delete(investment.getPayment());

                throw new RuntimeException("결제 미완료");
            }

            // DB에 저장된 결제 금액
            Long price = investment.getPayment().getPrice();
            // 실 결제 금액
            int iamportPrice = iamportResponse.getResponse().getAmount().intValue();


            // 결제 금액 검증
            if (iamportPrice != price) {
                // 주문, 결제 삭제
                investmentRepository.delete(investment);
                paymentRepository.delete(investment.getPayment());

                // 결제금액 위변조로 의심되는 결제금액을 취소(아임포트)
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(iamportPrice)));

                throw new RuntimeException("결제금액 위변조 의심");
            }


            log.info("iamportResponse : {} "  , iamportResponse.getResponse().getImpUid());


            // 결제 상태 변경
            investment.getPayment().changePaymentBySuccess(PaymentStatus.COMPLETED, iamportResponse.getResponse().getImpUid());


            log.info("결제 완료...");
            return iamportResponse;

        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public admin.adminbackend.domain.Payment findPayment(String paymentUid) {
        admin.adminbackend.domain.Payment byPaymentUid = paymentRepository.findByPaymentUid(paymentUid);
        return byPaymentUid;
    }

    @Override
    public void remove(String paymentUid) {
        admin.adminbackend.domain.Payment payment = findPayment(paymentUid);
        paymentRepository.delete(payment);
    }



    public void cancelReservation(PaymentCancelDTO paymentCancelDTO) {
        try {
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(paymentCancelDTO.getPaymentUid());

            if (response == null || response.getResponse() == null) {
                throw new IllegalArgumentException("Invalid payment information.");
            }

            // refundAmount가 Long 타입인 경우, int로 변환
            int refundAmount = paymentCancelDTO.getCancelRequestAmount().intValue();

            CancelData cancelData = createCancelData(response, refundAmount);
            IamportResponse<Payment> cancelResponse = iamportClient.cancelPaymentByImpUid(cancelData);

            // 환불 처리 결과에 대한 추가 로직
            if (cancelResponse.getCode() != 0) {
                throw new RuntimeException("Failed to cancel payment: " + cancelResponse.getMessage());
            }

        } catch (IamportResponseException e) {
            // Iamport API 응답 예외 처리
            e.printStackTrace();
            // 적절한 오류 처리 로직
        } catch (IOException e) {
            // I/O 예외 처리
            e.printStackTrace();
            // 적절한 오류 처리 로직
        } catch (Exception e) {
            // 기타 예외 처리
            e.printStackTrace();
            // 적절한 오류 처리 로직
        }
    }

    public void updatePaymentStatus(Long paymentId, PaymentStatus status) {
        paymentRepository.updatePaymentStatus(status, paymentId);
    }

    private CancelData createCancelData(IamportResponse<Payment> response, int refundAmount) {
        if (refundAmount == 0) { //전액 환불일 경우
            return new CancelData(response.getResponse().getImpUid(), true);
        }
        //부분 환불일 경우 checksum을 입력해 준다.
        return new CancelData(response.getResponse().getImpUid(), true, new BigDecimal(refundAmount));

    }
}
