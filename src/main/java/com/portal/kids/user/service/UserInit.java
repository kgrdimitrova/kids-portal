package com.portal.kids.user.service;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.property.ClubProperties;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.property.EventProperties;
import com.portal.kids.event.service.EventService;
import com.portal.kids.membership.service.UserClubService;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.property.UserProperties;
import com.portal.kids.user.repository.UserRepository;
import com.portal.kids.web.dto.ClubRequest;
import com.portal.kids.web.dto.EventRequest;
import com.portal.kids.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserInit implements ApplicationRunner {

    private final UserService userService;
    private final UserProperties userProperties;
    private final EventService eventService;
    private final EventProperties eventProperties;
    private final ClubService clubService;
    private final ClubProperties clubProperties;
    private final UserEventService userEventService;
    private final UserClubService membershipService;
    private final UserRepository userRepository;

    public UserInit(UserService userService, UserProperties userProperties, EventService eventService, EventProperties eventProperties, ClubService clubService, ClubProperties clubProperties, UserEventService userEventService, UserClubService membershipService, UserRepository userRepository) {
        this.userService = userService;
        this.userProperties = userProperties;
        this.eventService = eventService;
        this.eventProperties = eventProperties;
        this.clubService = clubService;
        this.clubProperties = clubProperties;
        this.userEventService = userEventService;
        this.membershipService = membershipService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        initializeDefaultData();
    }

    private void initializeDefaultData() {
        List<User> users = userService.getAllInternal();

        boolean defaultUserDoesNotExist = users.stream().noneMatch(user -> user.getUsername().equals(userProperties.getDefaultUser().getUsername()));

        if (defaultUserDoesNotExist) {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username(userProperties.getDefaultUser().getUsername())
                    .password(userProperties.getDefaultUser().getPassword())
                    .location(userProperties.getDefaultUser().getLocation())
                    .build();

            userService.register(registerRequest);
        }

        User createdUser = userService.getByUsername(userProperties.getDefaultUser().getUsername());
        createdUser.setRole(UserRole.TRAINER);
        userRepository.save(createdUser);

        List<Club> clubs = clubService.getAllClubs();
        boolean defaultClubDoesNotExist = clubs.stream().noneMatch(club -> club.getName().equals(clubProperties.getDefaultClub().getName()));

        if (defaultClubDoesNotExist) {
            ClubRequest createClubRequest = ClubRequest.builder()
                    .name(clubProperties.getDefaultClub().getName())
                    .description(clubProperties.getDefaultClub().getName())
                    .location(clubProperties.getDefaultClub().getLocation())
                    .type(clubProperties.getDefaultClub().getType())
                    .build();

            // Use internal method without security checks for initialization
            clubService.createClubInternal(createClubRequest, createdUser);
        }

        Club createdClub = clubService.getClubByName(clubProperties.getDefaultClub().getName());

        List<Club> userClubs = membershipService.getUserClubs(createdUser);
        boolean defaultUserClubsDoesNotExist = userClubs.stream().noneMatch(club -> club.getName().equals(clubProperties.getDefaultClub().getName()));

        if (defaultUserClubsDoesNotExist) {
            membershipService.joinUserToClub(createdUser.getId(), createdClub.getId());
        }

        List<Event> events = eventService.getAllEvents();
        boolean defaultEventDoesNotExist = events.stream().noneMatch(event -> event.getTitle().equals(eventProperties.getDefaultEvent().getTitle()));

        if (defaultEventDoesNotExist) {
            EventRequest createEventRequest = EventRequest.builder()
                    .title(eventProperties.getDefaultEvent().getTitle())
                    .description(eventProperties.getDefaultEvent().getTitle())
                    .location(eventProperties.getDefaultEvent().getLocation())
                    .periodicity(eventProperties.getDefaultEvent().getPeriodicity())
                    .type(eventProperties.getDefaultEvent().getType())
                    .startDate(eventProperties.getDefaultEvent().getStartDate().toLocalDate())
                    .endDate(eventProperties.getDefaultEvent().getEndDate().toLocalDate())
                    .startTime(eventProperties.getDefaultEvent().getStartDate().toLocalTime())
                    .endTime(eventProperties.getDefaultEvent().getEndDate().toLocalTime())
                    .pass(eventProperties.getDefaultEvent().getPass())
                    .clubId(createdClub.getId())
                    .build();

            // Use internal method without security checks for initialization
            eventService.createEventInternal(createEventRequest, createdUser);
        }

        try {
            Event createdEvent = eventService.getEventByTitleAndStartDateAndAgeCategory(
                    eventProperties.getDefaultEvent().getTitle(),
                    eventProperties.getDefaultEvent().getStartDate().toLocalDate(),
                    eventProperties.getDefaultEvent().getAgeCategory()
            );

            List<Event> userEvents = userEventService.getEventsByUser(createdUser);
            boolean defaultUserEventDoesNotExist = userEvents.stream()
                    .noneMatch(event -> event.getTitle().equals(eventProperties.getDefaultEvent().getTitle()));

            if (defaultUserEventDoesNotExist) {
                userEventService.subscribeUserToEvent(createdUser.getId(), createdEvent.getId());
            }
        } catch (Exception e) {
            log.info("Event with title [{}], start date [{}] and category [{}] does not exist.",
                    eventProperties.getDefaultEvent().getTitle(),
                    eventProperties.getDefaultEvent().getStartDate().toLocalDate(),
                    eventProperties.getDefaultEvent().getAgeCategory());
        }
    }
}