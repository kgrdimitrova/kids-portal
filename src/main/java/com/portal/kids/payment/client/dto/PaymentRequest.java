package com.portal.kids.payment.client.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PaymentRequest {

    private UUID userId;

    private String username;

    private UUID eventId;

    //private String trainer;
    private BigDecimal amount;

    private PaymentStatus status;

    private PaymentType type;
}
