package com.portal.kids.payment.client;

import com.portal.kids.payment.client.dto.PaymentRequest;
import com.portal.kids.payment.client.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "payment", url = "http://localhost:8084/api/v1")
public interface PaymentClient {

    @PostMapping("/payments")
    ResponseEntity<Void> upsertPayment(@RequestBody PaymentRequest requestBody);

    @PutMapping("/payments/{eventId}/{userId}/status")
    PaymentResponse updateStatus(@PathVariable UUID eventId, @PathVariable UUID userId);

    @GetMapping("/payments/event/{eventId}")
    ResponseEntity<List<PaymentResponse>> getPaymentsByEventId(@PathVariable UUID eventId);

    @GetMapping("/payments/user/{userId}")
    ResponseEntity<List<PaymentResponse>> getPaymentsByUserId(@PathVariable UUID userId);
}
