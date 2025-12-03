package com.portal.kids.utility;

import com.portal.kids.payment.client.dto.PaymentResponse;
import com.portal.kids.payment.client.dto.PaymentStatus;
import com.portal.kids.utils.PaymentUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PaymentUtilUTests {

    @Test
    void getPaidPaymentsCount_whenPassListOf2PaidAnd1PendingPayments_thenReturn2() {

        PaymentResponse oneSucceeded = PaymentResponse.builder().status(PaymentStatus.PAID).build();
        PaymentResponse twoSucceeded = PaymentResponse.builder().status(PaymentStatus.PAID).build();
        PaymentResponse thirdPending = PaymentResponse.builder().status(PaymentStatus.PENDING).build();

        List<PaymentResponse> payments = List.of(oneSucceeded, twoSucceeded, thirdPending);

        long result = PaymentUtils.getPaymentsCountByStatus(payments, PaymentStatus.PAID);

        assertEquals(2, result);
    }

    @Test
    void getPaymentsCount_whenPassEmptyList_thenReturn0() {

        long result = PaymentUtils.getPaymentsCount(List.of());

        assertEquals(0, result);
    }

    @Test
    void getPaidPaymentsAmount() {

        PaymentResponse oneSucceeded = PaymentResponse.builder().amount(BigDecimal.TEN).status(PaymentStatus.PAID).build();
        PaymentResponse twoSucceeded = PaymentResponse.builder().amount(BigDecimal.TEN).status(PaymentStatus.PAID).build();
        PaymentResponse thirdPending = PaymentResponse.builder().status(PaymentStatus.PENDING).build();

        List<PaymentResponse> payments = List.of(oneSucceeded, twoSucceeded, thirdPending);

        BigDecimal result = PaymentUtils.getPaymentsAmountByStatus(payments, PaymentStatus.PAID);

        assertEquals(BigDecimal.valueOf(20), result);
    }
}
