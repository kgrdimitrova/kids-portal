package com.portal.kids.subscription.service;

import com.portal.kids.event.model.Event;
import com.portal.kids.event.model.EventStatus;
import com.portal.kids.event.service.EventService;
import com.portal.kids.payment.client.dto.PaymentStatus;
import com.portal.kids.payment.client.dto.PaymentType;
import com.portal.kids.payment.service.PaymentService;
import com.portal.kids.subscription.model.UserEvent;
import com.portal.kids.subscription.model.UserEventId;
import com.portal.kids.subscription.repository.UserEventRepository;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private final UserEventRepository userEventRepository;
    private final EventService eventService;
    private final PaymentService paymentService;
    private final UserService userService;

    @Transactional
    public UserEvent subscribeUserToEvent(UUID userId, UUID eventId) {

        User user = userService.getById(userId);

        Event event = eventService.getById(eventId);

        Optional<UserEvent> existing = userEventRepository.findByUserAndEvent(user, event);
        if (existing.isPresent()) {
            throw new RuntimeException("User [%s] is already subscribed to this event [%s]".formatted(user.getUsername(), event.getTitle()));
        }

        UserEvent userEvent = UserEvent.builder()
                .id(new UserEventId(user.getId(), event.getId()))
                .user(user)
                .event(event)
                .subscribedOn(LocalDateTime.now())
                .status(EventStatus.ACTIVE)
                .build();

        userEventRepository.save(userEvent);
        paymentService.upsertPayment(userId, eventId, PaymentStatus.PENDING, PaymentType.SINGLE, event.getPass());

        return userEvent;
    }

    @Transactional
    public void unsubscribeUserFromEvent(UUID userId, UUID eventId) {

        Event event = eventService.getById(eventId);
        UserEvent userEvent = getUserEvent(userService.getById(userId), event);
        userEventRepository.deleteById(userEvent.getId());
        paymentService.upsertPayment(userId, eventId, PaymentStatus.CANCELLED,  PaymentType.SINGLE, event.getPass());
    }

    public List<Event> getEventsByUser(User user) {
        return userEventRepository.findEventsByUserOrderByStartDateDESC(user);
    }

    public List<UserEvent> getUserEvents(User user) {
        return userEventRepository.findUserEventsByUser(user);
    }

    public List<User> getUsersByEvent(Event event) {
        return userEventRepository.findUsersByEvent(event);
    }

    public UserEvent getUserEvent(User user, Event event) {
        return userEventRepository.findByUserAndEvent(user, event).orElseThrow(()->new RuntimeException("User [%s] is not subscribed to this event [%s]".formatted(user.getUsername(), event.getTitle())));
    }
}
