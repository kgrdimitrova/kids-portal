package com.portal.kids.web;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.common.Status;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import com.portal.kids.membership.service.UserClubService;
import com.portal.kids.security.UserData;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import com.portal.kids.web.dto.ClubRequest;
import com.portal.kids.web.dto.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/clubs")
public class ClubController {

    private final ClubService clubService;
    private final UserService userService;
    private final UserClubService userClubService;
    private final UserEventService userEventService;
    private final EventService eventService;

    public ClubController(ClubService clubService, UserService userService, UserClubService userClubService, UserEventService userEventService, EventService eventService) {
        this.clubService = clubService;
        this.userService = userService;
        this.userClubService = userClubService;
        this.userEventService = userEventService;
        this.eventService = eventService;
    }

    @GetMapping("/create-club")
    public ModelAndView createClubPage(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("clubRequest", new ClubRequest());
        modelAndView.setViewName("create-club");
        return modelAndView;
    }

    @GetMapping("/{id}/details")
    public ModelAndView clubDetails(@PathVariable UUID id, @AuthenticationPrincipal UserData userData){

        User user = userService.getById(userData.getUserId());
        Club club = clubService.getById(id);
        ClubRequest clubRequest = DtoMapper.fromClub(club);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("clubRequest", clubRequest);
        modelAndView.addObject("club", club);
        modelAndView.addObject("user", user);
        modelAndView.addObject("creator", club.getCreator());

        modelAndView.setViewName("update-club");
        return modelAndView;
    }

    @PutMapping("/{id}/details")
    public ModelAndView updateClub(@Valid ClubRequest clubRequest, BindingResult bindingResult, @PathVariable UUID id) {

        Club club = clubService.getById(id);
        List<User> members = userClubService.getClubUsers(club);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("club", club);
        modelAndView.addObject("creator", club.getCreator());
        modelAndView.addObject("members", members);

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("update-club");
            return modelAndView;
        }

        clubService.updateClub(id, clubRequest);

        return new ModelAndView("redirect:/");
    }

    @PostMapping("/create-club")
    public ModelAndView createClub(@Valid ClubRequest clubRequest, BindingResult bindingResult, @AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("create-club");

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("clubRequest", clubRequest);
            return modelAndView;
        }

        User user = userService.getById(userData.getUserId());
        clubService.createClub(clubRequest, user);

        return new ModelAndView("redirect:/");
    }

    @GetMapping("/{id}/join")
    public ModelAndView subscribeEvent(@AuthenticationPrincipal UserData userData, @PathVariable UUID id) {

        if (userData == null) {
            throw new RuntimeException("There is no user with the id " + id);
        }

        User user = userService.getById(userData.getUserId());
        userClubService.joinUserToClub(user.getId(), id);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/{id}/leave")
    public ModelAndView unsubscribeEvent(@AuthenticationPrincipal UserData userData, @PathVariable UUID id) {

        if (userData == null) {
            throw new RuntimeException("There is no user with the id " + id);
        }

        User user = userService.getById(userData.getUserId());
        userClubService.removeUserFromClub(user.getId(), id);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/{id}/schedule")
    public ModelAndView getClubTrainings(@AuthenticationPrincipal UserData userData, @PathVariable UUID id) {

        Club club  =  clubService.getById(id);
        User user = userService.getById(userData.getUserId());
        List<Event> userEvents = userEventService.getEventsByUser(user);
        List<Event> clubEvents = eventService.getActiveEventsByClubId(Status.ACTIVE, club.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("club", club);
        modelAndView.addObject("clubEvents", clubEvents);
        modelAndView.addObject("userEvents", userEvents);

        modelAndView.setViewName("club-schedule");
        return modelAndView;

    }
}
