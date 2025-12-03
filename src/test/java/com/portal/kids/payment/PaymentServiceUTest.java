package com.portal.kids.payment;

import com.portal.kids.exception.PaymentFailException;
import com.portal.kids.payment.client.PaymentClient;
import com.portal.kids.payment.client.dto.PaymentResponse;
import com.portal.kids.payment.client.dto.PaymentStatus;
import com.portal.kids.payment.client.dto.PaymentType;
import com.portal.kids.payment.service.PaymentService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PaymentServiceUTest {

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private UserService userService;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentService(paymentClient, userService);
    }

    @Test
    void upsertPayment_shouldCallClient() {

        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("TestUser");

        when(userService.getById(userId)).thenReturn(user);

        paymentService.upsertPayment(userId, eventId, PaymentStatus.PAID, PaymentType.SINGLE, BigDecimal.TEN);

        verify(paymentClient).upsertPayment(any());
    }

    @Test
    void upsertPayment_whenClientThrowsException_shouldLogError() {

        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("TestUser");

        when(userService.getById(userId)).thenReturn(user);

        doThrow(FeignException.class)
                .when(paymentClient).upsertPayment(any());

        paymentService.upsertPayment(userId, eventId, PaymentStatus.PENDING, PaymentType.SINGLE, BigDecimal.ONE);

        verify(paymentClient).upsertPayment(any());
    }

    @Test
    void getUserPayments_shouldReturnPayments() {

        UUID userId = UUID.randomUUID();

        PaymentResponse response1 = mock(PaymentResponse.class);
        PaymentResponse response2 = mock(PaymentResponse.class);
        List<PaymentResponse> expectedPayments = List.of(response1, response2);

        when(paymentClient.getPaymentsByUserId(userId))
                .thenReturn(ResponseEntity.ok(expectedPayments));

        List<PaymentResponse> payments = paymentService.getUserPayments(userId);

        assertThat(payments).containsExactlyElementsOf(expectedPayments);
    }

    @Test
    void getEventPayments_shouldReturnPayments() {

        UUID eventId = UUID.randomUUID();

        PaymentResponse response1 = mock(PaymentResponse.class);
        PaymentResponse response2 = mock(PaymentResponse.class);
        List<PaymentResponse> expectedPayments = List.of(response1, response2);

        when(paymentClient.getPaymentsByEventId(eventId))
                .thenReturn(ResponseEntity.ok(expectedPayments));

        List<PaymentResponse> payments = paymentService.getEventPayments(eventId);

        assertThat(payments).containsExactlyElementsOf(expectedPayments);
    }

    @Test
    void updateStatus_shouldCallClient() {

        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        paymentService.updateStatus(eventId, userId);

        verify(paymentClient).updateStatus(eventId, userId);
    }

    @Test
    void updateStatus_whenClientThrowsException_shouldThrowPaymentFailException() {

        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        doThrow(new PaymentFailException("Payment fail"))
                .when(paymentClient).updateStatus(eventId, userId);

        assertThatThrownBy(() -> paymentService.updateStatus(eventId, userId))
                .isInstanceOf(PaymentFailException.class)
                .hasMessageContaining("Payment fail");

        verify(paymentClient).updateStatus(eventId, userId);
    }
}
