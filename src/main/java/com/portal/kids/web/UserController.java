package com.portal.kids.web;

import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
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

    public UserController(UserService userService) {

        this.userService = userService;
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
