package com.portal.kids.email;

import com.portal.kids.event.driven.UpdateEvent;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmailService {

    private final EventService eventService;
    private final UserEventService userEventService;
    private final MailSender mailSender;

    public EmailService(EventService eventService, UserEventService userEventService, MailSender mailSender) {
        this.eventService = eventService;
        this.userEventService = userEventService;
        this.mailSender = mailSender;
    }

    @Async
    @EventListener
    public void sendEmail(UpdateEvent updateEvent) {

        Event updatedEvent = eventService.getById(updateEvent.getEventId());
        List<User> subscribedUsers = userEventService.getUsersByEvent(updatedEvent);

        subscribedUsers.forEach(user -> {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("updated event");
            mailMessage.setText("Event [%s] at [%s] has been updated.\n".formatted(updatedEvent.getTitle(), updatedEvent.getStartDate()));
            try {
                mailSender.send(mailMessage);
            } catch (MailException e) {
                log.error("Failed to send mail to %s, email: %s due to %s.".formatted(user.getUsername(), user.getEmail(), e.getMessage()));
            }
        });
    }
}
