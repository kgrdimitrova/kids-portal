package com.portal.kids.web;

import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import com.portal.kids.event.model.AgeCategory;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.model.EventPeriodicity;
import com.portal.kids.event.service.EventService;
import com.portal.kids.subscription.model.UserEvent;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import com.portal.kids.web.dto.EventRequest;
import com.portal.kids.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class UnSubscribeForEventITest {

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;


    @Test
    void unsubscribeUserFromEvent_success() {

        RegisterRequest registerRequest = new RegisterRequest("test1", "123123", "test1@gmail.com", Location.VARNA);
        User user = userService.register(registerRequest);
        assertThat(user).isNotNull();

        EventRequest eventRequest = EventRequest.builder()
                .title("Test Event")
                .startDate(LocalDate.of(2025, 12, 10))
                .endDate(LocalDate.of(2025, 12, 10))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .periodicity(EventPeriodicity.TRAINING)
                .ageCategory(AgeCategory.ALL)
                .type(ActivityType.EDUCATION)
                .location(Location.VARNA)
                .build();
        Event event = eventService.createEvent(eventRequest, user);
        UserEvent userEvent = userEventService.subscribeUserToEvent(user.getId(), event.getId());
        List<UserEvent> userEventsBefore = userEventService.getUserEvents(user);
        assertThat(userEventsBefore).hasSize(1);

        userEventService.unsubscribeUserFromEvent(user.getId(), userEvent.getEvent().getId());
        List<UserEvent> userEvents = userEventService.getUserEvents(user);
        assertThat(userEvents).hasSize(0);
    }

    @Test
    void unsubscribeUser_whenUserNotSubscribed_shouldThrowException() {

        RegisterRequest registerRequest = new RegisterRequest("test2", "123123", "test2@gmail.com", Location.VARNA);
        User user = userService.register(registerRequest);
        assertThat(user).isNotNull();

        EventRequest eventRequest = EventRequest.builder()
                .title("Test Event 2")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(5))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .location(Location.VARNA)
                .periodicity(EventPeriodicity.TRAINING)
                .ageCategory(AgeCategory.ALL)
                .type(ActivityType.EDUCATION)
                .build();
        Event event = eventService.createEvent(eventRequest, user);

        assertThatThrownBy(() -> userEventService.unsubscribeUserFromEvent(user.getId(), event.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User [%s] is not subscribed to this event [%s]".formatted(user.getUsername(), event.getTitle()));
    }

    @Test
    void unsubscribeUser_whenEventDoesNotExist_shouldThrowException() {

        RegisterRequest registerRequest = new RegisterRequest("test3", "123123", "test3@gmail.com", Location.VARNA);
        User user = userService.register(registerRequest);

        UUID invalidEventId = UUID.fromString("2b7a0bb1-14c0-4d06-9e2e-66a1e792b6");
        assertThatThrownBy(() -> userEventService.unsubscribeUserFromEvent(user.getId(), invalidEventId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Event with such id [%s] does not exist.".formatted(invalidEventId));
    }

    @Test
    void unsubscribeUser_whenUserDoesNotExist_shouldThrowException() {

        RegisterRequest registerRequest = new RegisterRequest("test4", "123123", "test4@gmail.com", Location.VARNA);
        User user = userService.register(registerRequest);

        EventRequest eventRequest = EventRequest.builder()
                .title("Test Event 4")
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusDays(7))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .location(Location.VARNA)
                .periodicity(EventPeriodicity.TRAINING)
                .ageCategory(AgeCategory.ALL)
                .type(ActivityType.EDUCATION)
                .build();
        Event event = eventService.createEvent(eventRequest, user);

        UUID invalidUserId = UUID.fromString("2b7a0bb1-14c0-4d06-9e2e-66a1e792b6");
        assertThatThrownBy(() -> userEventService.unsubscribeUserFromEvent(invalidUserId, event.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User with [%s] [%s] does not exists.".formatted("id", invalidUserId));
    }
}
