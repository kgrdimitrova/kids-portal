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

    @GetMapping("/payments/event/")
    ResponseEntity<List<PaymentResponse>>getPaymentsByEventId(@RequestParam("eventId") UUID eventId);

    @GetMapping("/payments/user")
    ResponseEntity<List<PaymentResponse>>getPaymentsByUserId(@RequestParam("userId") UUID userId);

//    @PostMapping("/payments")
//    ResponseEntity<PaymentResponse> validatePayment(@RequestBody PaymentRequest requestBody);

//    @PostMapping("/payments")
//    void cancelPayment(@RequestBody PaymentRequest requestBody);
}
