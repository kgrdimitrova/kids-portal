package com.portal.kids.payment.client.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentRequest {

    private UUID userId;

    private String username;

    private String eventName;

    private UUID eventId;

    private BigDecimal amount;

    private PaymentStatus status;

    private PaymentType type;
}
