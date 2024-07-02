package admin.adminbackend.controller;


import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Payment;
import admin.adminbackend.dto.payment.PaymentRequest;
import admin.adminbackend.repository.MemberRepository;
import admin.adminbackend.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;


@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {



        private final PaymentService paymentService;
        private final MemberRepository memberRepository;

        /**
         * 결제 요청 처리
         */

        @PostMapping("/payment")
        public ResponseEntity<?> requestPayment(
                @AuthenticationPrincipal UserDetails userDetails,
                @Valid @RequestBody PaymentRequest request
        ) {
            log.info("결제 요청 처리...");
            try {
                Long memberId = Long.valueOf(userDetails.getUsername());
                Member buyer = memberRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Could not find user for " + userDetails.getUsername()));
                BigDecimal amount = new BigDecimal(request.getAmount());
                Payment payment = paymentService.requestPayment(buyer, request.getEmail(), amount);
                return ResponseEntity.ok(payment);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid member ID");
            }
        }


    /**
         * 결제 확인 처리
         */
        @PutMapping("/{orderId}")
        public ResponseEntity<?> verifyPayment(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long orderId,
                @Valid @RequestBody String receiptId
        ) {
            log.info("결제 확인 처리...");
            try {
                Long memberId = Long.valueOf(userDetails.getUsername());
                Member buyer = memberRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Could not find user for " + userDetails.getUsername()));
                Payment payment = paymentService.verifyPayment(receiptId, orderId.toString(), buyer);
                return ResponseEntity.ok(payment);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid member ID");
            }
        }
    }
