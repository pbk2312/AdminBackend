package admin.adminbackend.service.investment;


import admin.adminbackend.domain.PaymentStatus;
import admin.adminbackend.dto.payment.PaymentCallbackRequest;
import admin.adminbackend.dto.payment.PaymentCancelDTO;
import admin.adminbackend.dto.payment.PaymentDTO;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

public interface PaymentService {

    // 결제 요청 데이터 조회
    PaymentDTO findRequestDto(String investmentUid);

    // 결제(콜백)
    IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request);

    admin.adminbackend.domain.Payment findPayment(String paymentUid);

    void remove(String paymentUid);


    void cancelReservation(PaymentCancelDTO paymentCancelDTO);

    void updatePaymentStatus(Long paymentId, PaymentStatus status);

}
