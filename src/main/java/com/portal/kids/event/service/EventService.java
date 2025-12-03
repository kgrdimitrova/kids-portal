package com.portal.kids.event.service;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.event.driven.GenerateEvent;
import com.portal.kids.event.driven.UpdateEvent;
import com.portal.kids.event.model.*;
import com.portal.kids.event.repository.EventRepository;
import com.portal.kids.exception.DatePeriodException;
import com.portal.kids.user.model.User;
import com.portal.kids.web.dto.EventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ClubService clubService;
    private final ApplicationEventPublisher eventPublisher;

    public EventService(EventRepository eventRepository, ClubService clubService, ApplicationEventPublisher eventPublisher) {
        this.eventRepository = eventRepository;
        this.clubService = clubService;
        this.eventPublisher = eventPublisher;
    }

    public Event createEvent(EventRequest eventRequest, User user) {

        Club club = eventRequest.getClubId() != null ? clubService.getById(eventRequest.getClubId()) : null;

        LocalDateTime start = LocalDateTime.of(eventRequest.getStartDate(), eventRequest.getStartTime());
        LocalDateTime end = LocalDateTime.of(eventRequest.getEndDate(), eventRequest.getEndTime());

        if (!start.isBefore(end)) {
            throw new DatePeriodException("Start date and time must be before end date and time.");
        }

        Event event = Event.builder()
                .title(eventRequest.getTitle())
                .startDate(eventRequest.getStartDate())
                .endDate(eventRequest.getEndDate())
                .startTime(eventRequest.getStartTime())
                .endTime(eventRequest.getEndTime())
                .location(eventRequest.getLocation())
                .type(eventRequest.getType())
                .periodicity(eventRequest.getPeriodicity())
                .status(EventStatus.ACTIVE)
                .pass(eventRequest.getPass())
                .creator(user)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .ageCategory(AgeCategory.ALL)
                .club(club)
                .build();

        eventRepository.save(event);
        log.info("A event [{}] is created by [{}].", event.getTitle(), user.getUsername());

        if (event.getPeriodicity() == EventPeriodicity.TRAINING) {
            GenerateEvent generateEvent = GenerateEvent.builder()
                    .eventId(event.getId())
                    .startDate(event.getStartDate())
                    .status(EventStatus.ACTIVE)
                    .createdOn(LocalDate.now())
                    .build();
            eventPublisher.publishEvent(generateEvent);
        }
        log.info("There will be generated periodic trainings till the end of the month for the event [{}].", event.getTitle());

        return event;
    }

    public Event getById(UUID id) {

        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event with such id [%s] does not exist.".formatted(id)));
    }

    public Event getEventByTitleAndStartDateAndAgeCategory(String title, LocalDate startDate, AgeCategory ageCategory) {

        return eventRepository.findByTitleAndStartDateAndAgeCategory(title, startDate, ageCategory).orElseThrow(() -> new RuntimeException("Event with such name [%s] does not exist.".formatted(title)));
    }

    public List<Event> getAllEvents() {

        return eventRepository.findAll();
    }

    public List<Event> getAllEventsByStartDate(LocalDate localDate) {

        return eventRepository.findByStartDateAfter(localDate.minusDays(1));
    }

    public List<Event> getAllEventsByStartDateBefore(LocalDate startDate) {
        return eventRepository.findByStartDateBefore(startDate);
    }

    public List<Event> getAllByStartDateBetweenAndPeriodicity(LocalDate startDate, LocalDate endDate, EventPeriodicity eventPeriodicity) {
        return eventRepository.findAllByStartDateBetweenAndPeriodicity(startDate, endDate, eventPeriodicity);
    }

    public List<Event> getActiveEventsByClubId(EventStatus status, UUID clubId) {
        return eventRepository.findEventByStatusAndClubId(status, clubId);
    }

    @PreAuthorize("@eventSecurity.isCreator(#id, authentication.principal.userId)")
    public void updateEvent(UUID id, EventRequest eventRequest) {

        Club club = null;
        if (eventRequest.getClubId() != null) {
            club = clubService.getById(eventRequest.getClubId());
        }
        Event event = getById(id);
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setStartDate(eventRequest.getStartDate());
        event.setEndDate(eventRequest.getEndDate());
        event.setStartTime(eventRequest.getStartTime());
        event.setEndTime(eventRequest.getEndTime());
        event.setLocation(eventRequest.getLocation());
        event.setPeriodicity(eventRequest.getPeriodicity());
        event.setPass(eventRequest.getPass());
        event.setClub(club);
        event.setPicture(eventRequest.getPicture());
        event.setType(eventRequest.getType());
        event.setMaxParticipants(eventRequest.getMaxParticipants());
        event.setAgeCategory(eventRequest.getAgeCategory());
        event.setRequirements(eventRequest.getRequirements());

        eventRepository.save(event);

        if (event.getPeriodicity() == EventPeriodicity.TRAINING) {
            UpdateEvent updateEvent = UpdateEvent.builder()
                    .eventId(event.getId())
                    .createdOn(LocalDate.now())
                    .build();
            eventPublisher.publishEvent(updateEvent);
        }
    }

    @PreAuthorize("hasAuthority('EDIT_EVENT') and @eventSecurity.isCreator(#eventId, authentication.principal.userId)")
    public void deactivateEvent(Event event) {
        deactivateEventInternal(event);
    }

    public void deactivateEventInternal(Event event) {
        event.setStatus(EventStatus.INACTIVE);
        eventRepository.save(event);
    }

    @EventListener
    public void generateEvents(GenerateEvent generateEvent) {

        LocalDate start = generateEvent.getStartDate();
        LocalDate endOfMonth = start.withDayOfMonth(start.lengthOfMonth());

        Event initialEvent = getById(generateEvent.getEventId());

        LocalDate current = start.plusWeeks(1);

        while (!current.isAfter(endOfMonth)) {
            Event generatedEvent = generateEvent(initialEvent, current);
            eventRepository.save(generatedEvent);
            current = current.plusWeeks(1);
        }
    }

    private Event generateEvent(Event initialEvent, LocalDate current) {
        return Event.builder()
                .title(initialEvent.getTitle())
                .startDate(current)
                .endDate(current)
                .startTime(initialEvent.getStartTime())
                .endTime(initialEvent.getEndTime())
                .location(initialEvent.getLocation())
                .type(initialEvent.getType())
                .periodicity(initialEvent.getPeriodicity())
                .status(EventStatus.ACTIVE)
                .pass(initialEvent.getPass())
                .creator(initialEvent.getCreator())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .ageCategory(initialEvent.getAgeCategory())
                .club(initialEvent.getClub())
                .requirements(initialEvent.getRequirements())
                .maxParticipants(initialEvent.getMaxParticipants())
                .build();
    }

    public void createEventInternal(EventRequest eventRequest, User user) {
        Club club = eventRequest.getClubId() != null ? clubService.getById(eventRequest.getClubId()) : null;

        LocalDateTime start = LocalDateTime.of(eventRequest.getStartDate(), eventRequest.getStartTime());
        LocalDateTime end = LocalDateTime.of(eventRequest.getEndDate(), eventRequest.getEndTime());

        if (!start.isBefore(end)) {
            throw new DatePeriodException("Start date and time must be before end date and time.");
        }

        Event event = Event.builder()
                .title(eventRequest.getTitle())
                .startDate(eventRequest.getStartDate())
                .endDate(eventRequest.getEndDate())
                .startTime(eventRequest.getStartTime())
                .endTime(eventRequest.getEndTime())
                .location(eventRequest.getLocation())
                .type(eventRequest.getType())
                .periodicity(eventRequest.getPeriodicity())
                .status(EventStatus.ACTIVE)
                .pass(eventRequest.getPass())
                .creator(user)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .ageCategory(AgeCategory.ALL)
                .club(club)
                .build();

        eventRepository.save(event);
        log.info("A event [{}] is created by [{}].", event.getTitle(), user.getUsername());

        if (event.getPeriodicity() == EventPeriodicity.TRAINING) {
            GenerateEvent generateEvent = GenerateEvent.builder()
                    .eventId(event.getId())
                    .startDate(event.getStartDate())
                    .status(EventStatus.ACTIVE)
                    .createdOn(LocalDate.now())
                    .build();
            eventPublisher.publishEvent(generateEvent);
        }
        log.info("There will be generated periodic trainings till the end of the month for the event [{}].", event.getTitle());
    }
}
