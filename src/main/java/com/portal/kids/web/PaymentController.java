package com.portal.kids.web;

import com.portal.kids.event.service.EventService;
import com.portal.kids.payment.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final EventService eventService;

    public PaymentController(PaymentService paymentService, EventService eventService) {
        this.paymentService = paymentService;
        this.eventService = eventService;
    }

    @PutMapping("/{eventId}/{userId}/status")
    public String updateStatus(@PathVariable UUID eventId, @PathVariable UUID userId) {

        paymentService.updateStatus(eventId, userId);
        return "redirect:/events/{eventId}/payments";
    }
}
