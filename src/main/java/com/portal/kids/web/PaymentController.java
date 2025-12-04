package com.portal.kids.web;

import com.portal.kids.payment.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PutMapping("/{eventId}/{userId}/status")
    public String updateStatus(@PathVariable UUID eventId, @PathVariable UUID userId) {

        paymentService.updateStatus(eventId, userId);
        return "redirect:/events/{eventId}/payments";
    }
}
