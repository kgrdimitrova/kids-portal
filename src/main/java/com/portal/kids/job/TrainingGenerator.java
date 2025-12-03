package com.portal.kids.job;
import com.portal.kids.event.driven.GenerateEvent;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.model.EventPeriodicity;
import com.portal.kids.event.service.EventService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TrainingGenerator {

    private final EventService eventService;
    private final ApplicationEventPublisher publishEvent;

    public TrainingGenerator(EventService eventService, ApplicationEventPublisher applicationEventPublisher) {
        this.eventService = eventService;
        this.publishEvent = applicationEventPublisher;
    }

    @Scheduled(cron = "0 0 0 L * ?")
    //@Scheduled(initialDelay = 10000, fixedRate = 50000)
    //@Scheduled(cron = "*/55 * * * * *")
    public void generateMonthlyTrainings() {

      List<Event> trainings = eventService.getAllByStartDateBetweenAndPeriodicity(LocalDate.now().minusWeeks(1), LocalDate.now(), EventPeriodicity.TRAINING);
                trainings.forEach(training -> {
                GenerateEvent newEvent = GenerateEvent.builder()
                .eventId(training.getId())
                .startDate(training.getStartDate().plusMonths(1))
                .createdOn(LocalDate.now())
                .build();
                publishEvent.publishEvent(newEvent);
        });
    }
}
