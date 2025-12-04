package com.portal.kids.web;

import com.portal.kids.club.service.ClubService;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import com.portal.kids.membership.service.UserClubService;
import com.portal.kids.payment.service.PaymentService;
import com.portal.kids.security.UserData;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.service.UserService;
import com.portal.kids.web.dto.EventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
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

    @MockitoBean
    private UserClubService userClubService;

    private UUID eventId;
    private UUID userId;
    private User user;
    private Event event;
    private UserData userData;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        // Initialize IDs
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();

        // Setup User with role
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setRole(UserRole.TRAINER);

        // Setup Event
        event = new Event();
        event.setId(eventId);
        event.setCreator(user);

        // Setup UserData
        userData = new UserData(userId, "testuser", "password", UserRole.TRAINER, true);

        // Authentication token for tests
        auth = new UsernamePasswordAuthenticationToken(userData, null, userData.getAuthorities());
    }

    @Test
    void createEventPage_shouldReturnView() throws Exception {
        Mockito.when(userService.getById(userId)).thenReturn(user);
        Mockito.when(userClubService.getUserClubs(user)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/events/create-event").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"))
                .andExpect(model().attributeExists("createEventRequest"))
                .andExpect(model().attributeExists("clubs"));
    }

    @Test
    void eventDetails_shouldReturnView() throws Exception {
        Mockito.when(userService.getById(userId)).thenReturn(user);
        Mockito.when(userClubService.getUserClubs(user)).thenReturn(Collections.emptyList());
        Mockito.when(eventService.getById(eventId)).thenReturn(event);

        mockMvc.perform(get("/events/{id}/details", eventId).with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("update-event"))
                .andExpect(model().attributeExists("eventRequest"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("clubs"))
                .andExpect(model().attributeExists("creator"));
    }

    @Test
    void updateEvent_shouldRedirect_whenValidInput() throws Exception {
        Mockito.when(eventService.getById(eventId)).thenReturn(event);
        Mockito.doNothing().when(eventService).updateEvent(eq(eventId), any(EventRequest.class));

        mockMvc.perform(put("/events/{id}/details", eventId).with(authentication(auth)).with(csrf())
                        .param("title", "Updated Event")
                        .param("location", "VARNA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void unsubscribeEvent_shouldRedirect() throws Exception {
        Mockito.when(userService.getById(userId)).thenReturn(user);
        Mockito.doNothing().when(userEventService).unsubscribeUserFromEvent(userId, eventId);

        mockMvc.perform(get("/events/{id}/unsubscribe", eventId).with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        Mockito.verify(userEventService).unsubscribeUserFromEvent(userId, eventId);
    }
}
