package com.portal.kids.job;

import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventDeactivator {

    private final EventService eventService;
    private final ApplicationEventPublisher publishEvent;

    public EventDeactivator(EventService eventService, ApplicationEventPublisher applicationEventPublisher) {
        this.eventService = eventService;
        this.publishEvent = applicationEventPublisher;
    }

    @Scheduled(initialDelay = 10000, fixedRate = 5000000)
    public void deactivatedPastEvents() {

        List<Event> events = eventService.getAllEventsByStartDateBefore(LocalDate.now());
        events.forEach(event ->
                eventService.deactivateEvent(event)
        );
    }
}
