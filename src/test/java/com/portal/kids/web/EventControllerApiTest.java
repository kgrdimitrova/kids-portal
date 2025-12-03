package com.portal.kids.web;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import com.portal.kids.payment.client.dto.PaymentResponse;
import com.portal.kids.payment.client.dto.PaymentStatus;
import com.portal.kids.payment.service.PaymentService;
import com.portal.kids.security.UserData;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc
class EventControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserEventService userEventService;

    @MockitoBean
    private ClubService clubService;

    @MockitoBean
    private PaymentService paymentService;

    private User user;
    private Club club;
    private Event event;
    private UserData userData;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .password("1234")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        club = Club.builder()
                .id(UUID.randomUUID())
                .name("Test Club")
                .build();

        event = Event.builder()
                .id(UUID.randomUUID())
                .title("Test Event")
                .creator(user)
                .build();

        userData = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.isActive()
        );
    }

    @Test
    void createEventPage_shouldReturnView() throws Exception {

        when(clubService.getAllClubs()).thenReturn(List.of(club));

        mockMvc.perform(get("/events/create-event")
                        .with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"))
                .andExpect(model().attributeExists("createEventRequest"))
                .andExpect(model().attributeExists("clubs"));
    }

    @Test
    void createEvent_shouldRedirect() throws Exception {

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(post("/events/create-event")
                        .with(user(userData))
                        .with(csrf())
                        .param("title", "Test Event")
                        .param("description", "Description")
                        .param("location", "VARNA")
                        .param("startDate", LocalDate.now().plusDays(1).toString())
                        .param("endDate", LocalDate.now().plusDays(1).toString())
                        .param("startTime", "10:00")
                        .param("endTime", "12:00")
                        .param("periodicity", "TRAINING")
                        .param("ageCategory", "ALL")
                        .param("type", "EDUCATION"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(eventService).createEvent(any(), eq(user));
    }

    @Test
    void createEvent_whenBindingError_shouldReturnView() throws Exception {

        mockMvc.perform(post("/events/create-event")
                        .with(user(userData))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"));
    }

    @Test
    void eventDetails_shouldReturnUpdateEventView() throws Exception {

        when(userService.getById(any())).thenReturn(user);
        when(eventService.getById(event.getId())).thenReturn(event);
        when(clubService.getAllClubs()).thenReturn(List.of(club));

        mockMvc.perform(get("/events/{id}/details", event.getId())
                        .with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("update-event"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("eventRequest"))
                .andExpect(model().attributeExists("creator"));
    }

    @Test
    void eventDetails_whenServiceFails_shouldReturn500() throws Exception {

        when(userService.getById(any()))
                .thenThrow(new RuntimeException("Boom"));

        mockMvc.perform(get("/events/{id}/details", event.getId())
                        .with(user(userData)))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("internal-server-error"));
    }

    @Test
    void subscribeEvent_shouldRedirect() throws Exception {

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(get("/events/{id}/subscribe", event.getId())
                        .with(user(userData)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userEventService)
                .subscribeUserToEvent(user.getId(), event.getId());
    }

    @Test
    void unsubscribeEvent_shouldRedirect() throws Exception {

        when(userService.getById(any())).thenReturn(user);

        mockMvc.perform(get("/events/{id}/unsubscribe", event.getId())
                        .with(user(userData)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userEventService)
                .unsubscribeUserFromEvent(user.getId(), event.getId());
    }

    @Test
    void getPaymentPage_shouldReturnUserPaymentsView() throws Exception {

        List<PaymentResponse> payments = List.of(
                PaymentResponse.builder()
                        .status(PaymentStatus.PAID)
                        .amount(BigDecimal.TEN)
                        .build(),
                PaymentResponse.builder()
                        .status(PaymentStatus.PENDING)
                        .amount(BigDecimal.TEN)
                        .build()
        );

        when(eventService.getById(event.getId())).thenReturn(event);
        when(userEventService.getUsersByEvent(event)).thenReturn(List.of(user));
        when(paymentService.getEventPayments(event.getId())).thenReturn(payments);

        mockMvc.perform(get("/events/{id}/payments", event.getId())
                        .with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("user-payments"))
                .andExpect(model().attributeExists("payments"))
                .andExpect(model().attributeExists("paidPaymentsCount"))
                .andExpect(model().attributeExists("paymentsAmount"));
    }
}

