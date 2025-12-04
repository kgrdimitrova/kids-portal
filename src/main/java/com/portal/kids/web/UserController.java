package com.portal.kids.web;

import com.portal.kids.payment.client.dto.PaymentResponse;
import com.portal.kids.payment.client.dto.PaymentStatus;
import com.portal.kids.payment.service.PaymentService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import com.portal.kids.utils.PaymentUtils;
import com.portal.kids.web.dto.EditProfileRequest;
import com.portal.kids.web.dto.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PaymentService paymentService;

    public UserController(UserService userService, PaymentService paymentService) {

        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getProfilePage(@PathVariable UUID id) {

        User user = userService.getById(id);
        EditProfileRequest editProfileRequest = DtoMapper.fromUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        modelAndView.addObject("editProfileRequest", editProfileRequest);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/{id}/payments")
    public ModelAndView getPaymentPage(@PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView("user-payments");

        User user = userService.getById(id);
        List<PaymentResponse> payments = paymentService.getUserPayments(id);

        modelAndView.addObject("user", user);
        modelAndView.addObject("payments", payments);
        modelAndView.addObject("pendingPaymentsCount", PaymentUtils.getPaymentsCountByStatus(payments, PaymentStatus.PENDING));
        modelAndView.addObject("paidPaymentsCount", PaymentUtils.getPaymentsCountByStatus(payments, PaymentStatus.PAID));
        modelAndView.addObject("cancelledPaymentsCount", PaymentUtils.getPaymentsCountByStatus(payments, PaymentStatus.CANCELLED));
        modelAndView.addObject("paymentsCount", PaymentUtils.getPaymentsCount(payments));
        modelAndView.addObject("paymentsAmount", PaymentUtils.getPaymentsAmountByStatus(payments, PaymentStatus.PAID));

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateProfile(@Valid EditProfileRequest editProfileRequest,
                                      BindingResult bindingResult,
                                      @PathVariable UUID id) {

        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editProfileRequest", editProfileRequest); // also return form data
            return modelAndView;
        }

        userService.updateProfile(id, editProfileRequest);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers() {

        List<User> users = userService.getAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", users);

        modelAndView.setViewName("users");

        return modelAndView;
    }

    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserRole(@PathVariable UUID userId) {

        userService.switchRole(userId);
        return "redirect:/users";
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserStatus(@PathVariable UUID userId) {

        userService.switchStatus(userId);
        return "redirect:/users";
    }
}
