package com.portal.kids.web;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import com.portal.kids.membership.service.UserClubService;
import com.portal.kids.security.UserData;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import com.portal.kids.utils.UserUtils;
import com.portal.kids.utils.WeatherUtils;
import com.portal.kids.weather.client.dto.WeatherResponse;
import com.portal.kids.weather.service.WeatherService;
import com.portal.kids.web.dto.LoginRequest;
import com.portal.kids.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;
    private final EventService eventService;
    private final UserEventService userEventService;
    private final UserClubService userClubService;
    private final ClubService clubService;
    private final WeatherService weatherService;

    public IndexController(UserService userService, EventService eventService, UserEventService userEventService, UserClubService userClubService, ClubService clubService, WeatherService weatherService) {
        this.userService = userService;
        this.eventService = eventService;
        this.userEventService = userEventService;
        this.userClubService = userClubService;
        this.clubService = clubService;
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public ModelAndView getIndexPage(@AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView();

        if (userData != null) {
            User user = userService.getById(userData.getUserId());
            List<Event> userEvents = userEventService.getEventsByUser(user);
            List<Club> userClubs = userClubService.getUserClubs(user);
            modelAndView.addObject("user", user);
            modelAndView.addObject("userEvents", userEvents);
            modelAndView.addObject("userClubs", userClubs);
        } else {
            modelAndView.addObject("weather", null);
            modelAndView.addObject("icon", "img.png");
        }

        List<Event> events = eventService.getAllEventsByStartDate(LocalDate.now());
        List<Club> clubs = clubService.getAllClubs();

        modelAndView.addObject("userData", userData);
        modelAndView.addObject("events", events);
        modelAndView.addObject("clubs", clubs);

        modelAndView.setViewName("index");

        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(name = "loginAttemptMessage", required = false) String loginAttemptMessage,
                                     @RequestParam(name = "error", required = false) String errorMessage) {

        ModelAndView modelAndView = new ModelAndView("login");

        modelAndView.addObject("loginRequest", new LoginRequest());
        UserUtils.addLoginMessages(modelAndView, errorMessage, loginAttemptMessage);

        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("registerRequest", new RegisterRequest());

        modelAndView.setViewName("register");

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerRequest);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getUserId());
        WeatherResponse weather = weatherService.getWeather(String.valueOf(user.getLocation()));

        List<Event> events = userEventService.getEventsByUser(user);
        List<Club> clubs = userClubService.getUserClubs(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("events", events);
        modelAndView.addObject("clubs", clubs);
        modelAndView.addObject("weather", weather);
        modelAndView.addObject("icon", WeatherUtils.extractIcon(weather));
        modelAndView.setViewName("home");

        return modelAndView;
    }
}
