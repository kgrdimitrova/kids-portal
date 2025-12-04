package com.portal.kids.job;

import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventDeactivator {

    private final EventService eventService;

    public EventDeactivator(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(initialDelay = 10000, fixedRate = 5000000)
    public void deactivatedPastEvents() {

        List<Event> events = eventService.getAllActiveEventsByStartDateBefore(LocalDate.now());
        events.forEach(eventService::deactivateEventInternal);
    }
}
