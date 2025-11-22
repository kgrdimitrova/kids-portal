package com.portal.kids.payment.client.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {

    private UUID eventId;

    private UUID userId;

    private String username;

    private BigDecimal amount;

    private PaymentType type;

    private PaymentStatus status;
}
