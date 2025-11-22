package com.portal.kids.subscription.service;

import com.portal.kids.event.model.Event;
import com.portal.kids.event.model.EventPeriodicity;
import com.portal.kids.event.repository.EventRepository;
import com.portal.kids.event.service.EventService;
import com.portal.kids.payment.client.dto.PaymentStatus;
import com.portal.kids.payment.client.dto.PaymentType;
import com.portal.kids.payment.service.PaymentService;
import com.portal.kids.subscription.model.Status;
import com.portal.kids.subscription.model.UserEvent;
import com.portal.kids.subscription.model.UserEventId;
import com.portal.kids.subscription.repository.UserEventRepository;
import com.portal.kids.user.model.User;
import com.portal.kids.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private final UserEventRepository userEventRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PaymentService paymentService;
    private final EventService eventService;

    @Transactional
    public UserEvent subscribeUserToEvent(UUID userId, UUID eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        // Check if already subscribed
        Optional<UserEvent> existing = userEventRepository.findByUserAndEvent(user, event);
        if (existing.isPresent()) {
            throw new IllegalStateException("User already subscribed to this event");
        }

        // Create the link
        UserEvent userEvent = UserEvent.builder()
                .id(new UserEventId(user.getId(), event.getId()))
                .user(user)
                .event(event)
                .subscribedOn(LocalDateTime.now())
                .status(Status.ACTIVE)
                .build();

        userEventRepository.save(userEvent);
        paymentService.upsertPayment(userId, eventId, PaymentStatus.PENDING, PaymentType.SINGLE, event.getPass());

        return userEvent;
    }

    @Transactional
    public void unsubscribeUserFromEvent(UUID userId, UUID eventId) {

        Event event = eventService.getById(eventId);
        userEventRepository.deleteByUserIdAndEventId(userId, eventId);
        paymentService.upsertPayment(userId, eventId, PaymentStatus.CANCELLED,  PaymentType.SINGLE, event.getPass());
    }

    public List<Event> getUserEvents(User user) {
        return userEventRepository.findEventsByUserOrderByStartDateDESC(user);
    }

    public List<User> getEventUsers(Event event) {
        return userEventRepository.findUsersByEvent(event);
    }
}
