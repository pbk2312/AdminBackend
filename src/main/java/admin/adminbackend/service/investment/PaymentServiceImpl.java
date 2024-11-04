package admin.adminbackend.service.investment;

import admin.adminbackend.investcontract.domain.InvestorInvestment;
import admin.adminbackend.domain.PaymentStatus;
import admin.adminbackend.dto.payment.PaymentCallbackRequest;
import admin.adminbackend.dto.payment.PaymentCancelDTO;
import admin.adminbackend.dto.payment.PaymentDTO;
import admin.adminbackend.investcontract.repository.InvestorInvestmentRepository;
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

    private final InvestorInvestmentRepository investorInvestmentRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Override
    public PaymentDTO findRequestDto(String investmentUid) {
        InvestorInvestment investorInvestment = investorInvestmentRepository.findInvestmentAndPaymentAndMember(investmentUid)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않아요"));

        return PaymentDTO.builder()
                .investmentUid(investmentUid)
                .memberId(investorInvestment.getInvestor().getId())
                .ventureName(investorInvestment.getVentureListInfo().getName())
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
            InvestorInvestment investorInvestment = investorInvestmentRepository.findInvestmentAndPaymentAndMember(request.getInvestmentUid())
                    .orElseThrow(() -> new IllegalArgumentException("투자 내역이 없습니다."));

            // 결제 완료가 아니면
            if (!iamportResponse.getResponse().getStatus().equals("paid")) {
                // 주문, 결제 삭제
                investorInvestmentRepository.delete(investorInvestment);
                paymentRepository.delete(investorInvestment.getPayment());

                throw new RuntimeException("결제 미완료");
            }

            // DB에 저장된 결제 금액
            Long price = investorInvestment.getPayment().getPrice();
            // 실 결제 금액
            int iamportPrice = iamportResponse.getResponse().getAmount().intValue();


            // 결제 금액 검증
            if (iamportPrice != price) {
                // 주문, 결제 삭제
                investorInvestmentRepository.delete(investorInvestment);
                paymentRepository.delete(investorInvestment.getPayment());

                // 결제금액 위변조로 의심되는 결제금액을 취소(아임포트)
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(iamportPrice)));

                throw new RuntimeException("결제금액 위변조 의심");
            }


            log.info("iamportResponse : {} "  , iamportResponse.getResponse().getImpUid());


            // 결제 상태 변경
            investorInvestment.getPayment().changePaymentBySuccess(PaymentStatus.COMPLETED, iamportResponse.getResponse().getImpUid());


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

    @Override
    public void remove(String paymentUid) {

    }

    @Override
    public void cancelReservation(PaymentCancelDTO paymentCancelDTO) {

    }
}
