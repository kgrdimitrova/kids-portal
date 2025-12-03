package com.portal.kids.email;

import com.portal.kids.event.driven.UpdateEvent;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceUTest {
    @Mock
    private EventService eventService;

    @Mock
    private UserEventService userEventService;

    @Mock
    private MailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_shouldSendEmailToAllSubscribedUsers() {

        UUID eventId = UUID.fromString("86ef3fb8-1366-415f-a3b0-c9403efdd8f2");

        UpdateEvent updateEvent = mock(UpdateEvent.class);
        when(updateEvent.getEventId()).thenReturn(eventId);

        Event event = new Event();
        event.setTitle("Sport Day");
        event.setStartDate(LocalDate.now());

        when(eventService.getById(eventId)).thenReturn(event);

        User user1 = new User();
        user1.setEmail("a@test.com");
        user1.setUsername("UserA");

        User user2 = new User();
        user2.setEmail("b@test.com");
        user2.setUsername("UserB");

        when(userEventService.getUsersByEvent(event))
                .thenReturn(List.of(user1, user2));

        emailService.sendEmail(updateEvent);

        verify(eventService).getById(eventId);
        verify(userEventService).getUsersByEvent(event);
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }


    // ✅ Scenario 2: One email fails, but the others still send
    @Test
    void sendEmail_whenOneFails_shouldContinueWithOthers() {

        UUID eventId = UUID.fromString("22ef3fb8-1366-415f-a3b0-c9403efdd8f2");

        UpdateEvent updateEvent = mock(UpdateEvent.class);
        when(updateEvent.getEventId()).thenReturn(eventId);

        Event event = new Event();
        event.setTitle("Drama Class");
        event.setStartDate(LocalDate.now());

        when(eventService.getById(eventId)).thenReturn(event);

        User user1 = new User();
        user1.setEmail("fail@test.com");
        user1.setUsername("FailUser");

        User user2 = new User();
        user2.setEmail("ok@test.com");
        user2.setUsername("OkUser");

        when(userEventService.getUsersByEvent(event))
                .thenReturn(List.of(user1, user2));

        doThrow(new MailException("BOOM!") {})
                .when(mailSender)
                .send(argThat((SimpleMailMessage msg) ->
                        msg.getTo() != null &&
                                Arrays.asList(msg.getTo()).contains("fail@test.com")
                ));

        emailService.sendEmail(updateEvent);

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_whenNoSubscribedUsers_shouldNotSendAnything() {

        UUID eventId = UUID.fromString("36ef3fb8-1366-415f-a3b0-c9403efdd8f2");;

        UpdateEvent updateEvent = mock(UpdateEvent.class);
        when(updateEvent.getEventId()).thenReturn(eventId);

        Event event = new Event();
        when(eventService.getById(eventId)).thenReturn(event);

        when(userEventService.getUsersByEvent(event))
                .thenReturn(List.of());

        emailService.sendEmail(updateEvent);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldBuildCorrectMailMessage() {

        UUID eventId = UUID.fromString("46ef3fb8-1366-415f-a3b0-c9403efdd8f2");

        UpdateEvent updateEvent = mock(UpdateEvent.class);
        when(updateEvent.getEventId()).thenReturn(eventId);

        LocalDate date = LocalDate.of(2025, 12, 12);

        Event event = new Event();
        event.setTitle("Chess Tournament");
        event.setStartDate(date);

        when(eventService.getById(eventId)).thenReturn(event);

        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("Tester");

        when(userEventService.getUsersByEvent(event))
                .thenReturn(List.of(user));

        ArgumentCaptor<SimpleMailMessage> messageCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(updateEvent);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage mail = messageCaptor.getValue();

        assert Arrays.asList(mail.getTo()).contains("test@test.com");
        assert mail.getSubject().equals("updated event");
        assert mail.getText().contains("Chess Tournament");
        assert mail.getText().contains("2025-12-12");
    }
}
