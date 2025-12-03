package com.portal.kids.web;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.service.EventService;
import com.portal.kids.payment.client.dto.PaymentResponse;
import com.portal.kids.payment.client.dto.PaymentStatus;
import com.portal.kids.payment.service.PaymentService;
import com.portal.kids.security.UserData;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import com.portal.kids.utils.PaymentUtils;
import com.portal.kids.web.dto.EventRequest;
import com.portal.kids.web.dto.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final UserEventService userEventService;
    private final ClubService clubService;
    private final PaymentService paymentService;

    public EventController(EventService eventService, UserService userService, UserEventService userEventService, ClubService clubService, PaymentService paymentService) {
        this.eventService = eventService;
        this.userService = userService;
        this.userEventService = userEventService;
        this.clubService = clubService;
        this.paymentService = paymentService;
    }

    @GetMapping("/create-event")
    public ModelAndView createEventPage() {

        List<Club> clubs = clubService.getAllClubs();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("createEventRequest", new EventRequest());
        modelAndView.addObject("clubs", clubs);
        modelAndView.setViewName("create-event");
        return modelAndView;
    }

    @PostMapping("/create-event")
    public ModelAndView createEvent(@Valid EventRequest createEventRequest, BindingResult bindingResult, @AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView("create-event");

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("createEventRequest", createEventRequest);
            return modelAndView;
        }

        User user = userService.getById(userData.getUserId());
        eventService.createEvent(createEventRequest, user);

        return new ModelAndView("redirect:/");
    }

    @GetMapping("/create-training/{id}")
    public ModelAndView createTrainingPage(@PathVariable UUID id) {

        Club club = clubService.getById(id);
        List<Club> clubs = new ArrayList<>();
        clubs.add(club);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("createEventRequest", new EventRequest());
        modelAndView.addObject("clubs", clubs);
        modelAndView.addObject("club", club);
        modelAndView.setViewName("create-event");
        return modelAndView;
    }

    @PostMapping("/create-training/{id}")
    public ModelAndView createTraining(@Valid EventRequest createEventRequest, BindingResult bindingResult, @AuthenticationPrincipal UserData userData, @PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView("create-event");

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("createEventRequest", createEventRequest);
            return modelAndView;
        }

        Club club = clubService.getById(id);
        User user = userService.getById(userData.getUserId());
        eventService.createEvent(createEventRequest, user);

        return new ModelAndView("redirect:/");
    }

    @GetMapping("/{id}/details")
    public ModelAndView eventDetails(@PathVariable UUID id, @AuthenticationPrincipal UserData userData){

        User user = userService.getById(userData.getUserId());
        List<Club> clubs = clubService.getAllClubs();

        Event event = eventService.getById(id);
        EventRequest eventRequest = DtoMapper.fromEvent(event);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("update-event");
        modelAndView.addObject("eventRequest", eventRequest);
        modelAndView.addObject("event", event);
        modelAndView.addObject("user", user);
        modelAndView.addObject("clubs", clubs);
        modelAndView.addObject("creator", event.getCreator());

        return modelAndView;
    }

    @PutMapping("/{id}/details")
    public ModelAndView updateEvent(@Valid EventRequest eventRequest, BindingResult bindingResult, @PathVariable UUID id, @AuthenticationPrincipal UserData userData) throws IOException {

        Event event = eventService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("event", event);
        modelAndView.addObject("creator", event.getCreator());

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("update-event");
        }

        eventService.updateEvent(id, eventRequest);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/{id}/subscribe")
    public ModelAndView subscribeEvent(@AuthenticationPrincipal UserData userData, @PathVariable UUID id) {

        if (userData == null) {
            throw new RuntimeException("There is no user with the id " + id);
        }

        User user = userService.getById(userData.getUserId());
        userEventService.subscribeUserToEvent(user.getId(), id);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/{id}/unsubscribe")
    public ModelAndView unsubscribeEvent(@AuthenticationPrincipal UserData userData, @PathVariable UUID id) {

        if (userData.equals(null)) {
            throw new RuntimeException("There is no user with the id " + id);
        }

        User user = userService.getById(userData.getUserId());
        userEventService.unsubscribeUserFromEvent(user.getId(), id);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/{id}/payments")
    public ModelAndView getPaymentPage(@AuthenticationPrincipal UserData user, @PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView("user-payments");

        Event event = eventService.getById(id);
        List<User> users = userEventService.getUsersByEvent(event);

        List<PaymentResponse> payments = paymentService.getEventPayments(id);


        modelAndView.addObject("event", event);
        modelAndView.addObject("users", users);
        modelAndView.addObject("payments", payments);
        modelAndView.addObject("pendingPaymentsCount", PaymentUtils.getPaymentsCountByStatus(payments, PaymentStatus.PENDING));
        modelAndView.addObject("paidPaymentsCount", PaymentUtils.getPaymentsCountByStatus(payments, PaymentStatus.PAID));
        modelAndView.addObject("cancelledPaymentsCount", PaymentUtils.getPaymentsCountByStatus(payments, PaymentStatus.CANCELLED));
        modelAndView.addObject("paymentsCount", PaymentUtils.getPaymentsCount(payments));
        modelAndView.addObject("paymentsAmount", PaymentUtils.getPaymentsAmountByStatus(payments, PaymentStatus.PAID));

        return modelAndView;
    }
}
