package com.portal.kids.event;

import com.portal.kids.club.service.ClubService;
import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import com.portal.kids.event.driven.GenerateEvent;
import com.portal.kids.event.driven.UpdateEvent;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.model.EventPeriodicity;
import com.portal.kids.event.model.EventStatus;
import com.portal.kids.event.repository.EventRepository;
import com.portal.kids.event.service.EventService;
import com.portal.kids.exception.DatePeriodException;
import com.portal.kids.user.model.User;
import com.portal.kids.web.dto.EventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceUTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ClubService clubService; // if EventService depends on it

    @InjectMocks
    private EventService eventService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("John");
    }

    @Test
    void createEvent_shouldSaveEvent() {
        EventRequest request = new EventRequest();
        request.setTitle("Football");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setLocation(Location.VARNA);
        request.setPeriodicity(EventPeriodicity.ONE_TIME);
        request.setType(ActivityType.SPORT);

        Event event = eventService.createEvent(request, user);

        verify(eventRepository, times(1)).save(any(Event.class));
        verify(eventPublisher, never()).publishEvent(any());

        assertThat(event.getTitle()).isEqualTo("Football");
        assertThat(event.getCreator()).isEqualTo(user);
        assertThat(event.getStatus()).isEqualTo(EventStatus.ACTIVE);
    }

    @Test
    void createEvent_whenPeriodicityTraining_shouldPublishEvent() {
        EventRequest request = new EventRequest();
        request.setTitle("Training");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(11, 0));
        request.setLocation(Location.VARNA);
        request.setPeriodicity(EventPeriodicity.TRAINING);
        request.setType(ActivityType.SPORT);

        Event event = eventService.createEvent(request, user);

        verify(eventRepository).save(any(Event.class));
        verify(eventPublisher).publishEvent(any(GenerateEvent.class));

        assertThat(event.getPeriodicity()).isEqualTo(EventPeriodicity.TRAINING);
    }

    @Test
    void createEvent_whenStartAfterEnd_shouldThrow() {
        EventRequest request = new EventRequest();
        request.setTitle("Invalid");
        request.setStartDate(LocalDate.now().plusDays(2));
        request.setEndDate(LocalDate.now());
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(9, 0));

        assertThatThrownBy(() -> eventService.createEvent(request, user))
                .isInstanceOf(DatePeriodException.class);
    }

    @Test
    void getById_shouldReturnEvent() {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        Event result = eventService.getById(id);

        assertThat(result).isEqualTo(event);
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getById(id))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void updateEvent_shouldUpdateAndPublishWhenTraining() {
        UUID eventId = UUID.randomUUID();

        Event existing = new Event();
        existing.setId(eventId);
        existing.setPeriodicity(EventPeriodicity.TRAINING);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existing));

        EventRequest update = new EventRequest();
        update.setTitle("Updated title");
        update.setPeriodicity(EventPeriodicity.TRAINING);

        eventService.updateEvent(eventId, update);

        verify(eventRepository).save(existing);
        verify(eventPublisher).publishEvent(any(UpdateEvent.class));
        assertThat(existing.getTitle()).isEqualTo("Updated title");
    }

    @Test
    void updateEvent_whenNotTraining_shouldNotPublish() {
        UUID eventId = UUID.randomUUID();

        Event existing = new Event();
        existing.setId(eventId);
        existing.setPeriodicity(EventPeriodicity.ONE_TIME);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existing));

        EventRequest update = new EventRequest();
        update.setTitle("Title");
        update.setPeriodicity(EventPeriodicity.ONE_TIME);

        eventService.updateEvent(eventId, update);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void deactivateEventInternal_shouldSetInactive() {
        Event event = new Event();
        event.setStatus(EventStatus.ACTIVE);

        eventService.deactivateEventInternal(event);

        verify(eventRepository).save(event);
        assertThat(event.getStatus()).isEqualTo(EventStatus.INACTIVE);
    }

    @Test
    void generateEvents_shouldGenerateWeeklyEvents() {
        UUID id = UUID.randomUUID();

        Event initial = new Event();
        initial.setId(id);
        initial.setTitle("Training");
        initial.setStartDate(LocalDate.of(2025, 1, 1));
        initial.setEndDate(LocalDate.of(2025, 1, 1));
        initial.setStartTime(LocalTime.of(10, 0));
        initial.setEndTime(LocalTime.of(11, 0));
        initial.setPeriodicity(EventPeriodicity.TRAINING);
        initial.setCreator(user);

        when(eventRepository.findById(id)).thenReturn(Optional.of(initial));

        GenerateEvent generate = GenerateEvent.builder()
                .eventId(id)
                .startDate(LocalDate.of(2025, 1, 1))
                .build();

        eventService.generateEvents(generate);

        verify(eventRepository, atLeastOnce()).save(any(Event.class));
    }
}
