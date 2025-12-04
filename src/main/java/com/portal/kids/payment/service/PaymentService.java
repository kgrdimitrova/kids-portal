package com.portal.kids.payment.service;

import com.portal.kids.exception.PaymentFailException;
import com.portal.kids.payment.client.PaymentClient;
import com.portal.kids.payment.client.dto.PaymentRequest;
import com.portal.kids.payment.client.dto.PaymentResponse;
import com.portal.kids.payment.client.dto.PaymentStatus;
import com.portal.kids.payment.client.dto.PaymentType;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final UserService userService;

    public PaymentService(PaymentClient paymentClient, UserService userService) {
        this.paymentClient = paymentClient;
        this.userService = userService;
    }

    public void upsertPayment(UUID userId, UUID eventId, PaymentStatus status, PaymentType type, BigDecimal amount) {

        User user = userService.getById(userId);
        PaymentRequest payment = PaymentRequest.builder()
                .userId(userId)
                .username(user.getUsername())
                .eventId(eventId)
                .amount(amount)
                .status(status)
                .type(type)
                .build();
        try {
            paymentClient.upsertPayment(payment);
            log.info("Payment for user %s for event %s created".formatted(payment.getUsername(), payment.getEventId()));
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }

    public List<PaymentResponse> getUserPayments(UUID userId) {

        ResponseEntity<List<PaymentResponse>> response = paymentClient.getPaymentsByUserId(userId);

        return response.getBody() != null
                ? response.getBody().stream().toList()
                : Collections.emptyList();
    }

    @PreAuthorize("hasAuthority('VIEW_PAYMENTS')")
    public List<PaymentResponse> getEventPayments(UUID eventId) {

        ResponseEntity<List<PaymentResponse>> response = paymentClient.getPaymentsByEventId(eventId);

        return response.getBody() != null
                ? response.getBody().stream().toList()
                : Collections.emptyList();
    }

    @PreAuthorize("hasAuthority('EDIT_PAYMENT')")
    public void updateStatus(UUID eventId, UUID userId) {

        try {
            paymentClient.updateStatus(eventId, userId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            throw new PaymentFailException("Payment fail, Try again later.");
        }
    }
}
