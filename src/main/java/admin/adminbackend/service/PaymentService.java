package admin.adminbackend.service;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Payment;
import admin.adminbackend.domain.PaymentMethod;
import admin.adminbackend.domain.PaymentStatus;
import admin.adminbackend.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.google.common.base.Preconditions.checkNotNull;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Value("${pgmodule.app-id}")
    private String apiKey;
    @Value("${pgmodule.secret-key}")
    private String apiSecret;

    @Transactional
    public Payment requestPayment(Member buyer, String name, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setBuyer(buyer);
        payment.setOrderId(buyer.getEmail() + "_" + Objects.hash(buyer, name, amount, System.currentTimeMillis()));
        payment.setName(name);
        payment.setAmount(amount);
        return paymentRepository.save(payment);
    }

    @Transactional // 결제 검증
    public Payment verifyPayment(Payment payment,Member buyer) {
        checkNotNull(payment, "payment must be provided.");// payment와 buyer의 매개변수가 Null 인지 확
        checkNotNull(buyer, "buyer must be provided.");// payment와 buyer의 매개변수가 Null 인지 확인

        if (!payment.getBuyer().equals(buyer)) {
            throw new RuntimeException("Could not found payment for " + buyer.getEmail() + ".");
        } // payment 구매자가 buyer 와 일치하는지 확인

        IamportClient iamportClient = new IamportClient(apiKey, apiSecret);
        try {
            IamportResponse<com.siot.IamportRestClient.response.Payment> paymentResponse = iamportClient.paymentByImpUid(payment.getReceiptId());
            if (Objects.nonNull(paymentResponse.getResponse())) {
                com.siot.IamportRestClient.response.Payment paymentData = paymentResponse.getResponse();
                if (payment.getReceiptId().equals(paymentData.getImpUid()) && payment.getOrderId().equals(paymentData.getMerchantUid()) && payment.getAmount().compareTo(paymentData.getAmount()) == 0) {
                    PaymentMethod method = PaymentMethod.valueOf(paymentData.getPayMethod().toUpperCase());
                    PaymentStatus status = PaymentStatus.valueOf(paymentData.getStatus().toUpperCase());
                    payment.setMethod(method);
                    payment.setStatus(status);
                    paymentRepository.save(payment);
                    if (status.equals(PaymentStatus.READY)) {
                        if (method.equals(PaymentMethod.VBANK)) {
                            throw new RuntimeException(paymentData.getVbankNum() + " " + paymentData.getVbankDate() + " " + paymentData.getVbankName());
                        } else {
                            throw new RuntimeException("Payment was not completed.");
                        }
                    } else if (status.equals(PaymentStatus.PAID)) {
                        payment.setPaidAt(paymentData.getPaidAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                        paymentRepository.save(payment);
                    } else if (status.equals(PaymentStatus.FAILED)) {
                        throw new RuntimeException("Payment failed.");
                    } else if (status.equals(PaymentStatus.CANCELLED)) {
                        throw new RuntimeException("This is a cancelled payment.");
                    }
                } else {
                    throw new RuntimeException("The amount paid and the amount to be paid do not match.");
                }
            } else {
                throw new RuntimeException("Could not found payment for " + payment.getReceiptId() + ".");
            }
        } catch (IamportResponseException e) {
            e.printStackTrace();
            switch (e.getHttpStatusCode()) {
                case 401 -> throw new RuntimeException("Authentication token not passed or invalid.");
                case 404 -> throw new RuntimeException("Could not found payment for " + payment.getReceiptId() + ".");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return payment;
    }

    @Transactional
    public Payment verifyPayment(String receiptId, String orderId, Member buyer) {
        checkNotNull(receiptId, "receiptId must be provided.");

        Optional<Payment> optionalPayment = paymentRepository.findByOrderIdAndBuyer(orderId, buyer);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setReceiptId(receiptId);
            return verifyPayment(payment, buyer);
        } else {
            throw new RuntimeException("Could not found payment for " + orderId + ".");
        }
    }
}

