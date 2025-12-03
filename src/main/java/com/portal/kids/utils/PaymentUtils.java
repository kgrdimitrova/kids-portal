package com.portal.kids.utils;

import com.portal.kids.payment.client.dto.PaymentResponse;
import com.portal.kids.payment.client.dto.PaymentStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class PaymentUtils {

    public static long getPaymentsCount(List<PaymentResponse> payments) {

        return payments.stream().toList().size();
    }

    public static long getPaymentsCountByStatus(List<PaymentResponse> payments, PaymentStatus status) {

        return payments.stream().filter(p -> p.getStatus().equals(status)).count();
    }

    public static BigDecimal getPaymentsAmountByStatus(List<PaymentResponse> payments, PaymentStatus status) {

        return payments.stream()
                .filter(p -> p.getStatus().equals(status))
                .map(PaymentResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
