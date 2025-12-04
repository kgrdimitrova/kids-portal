package com.portal.kids.web;

import com.portal.kids.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public String handleException() {

        return "not-found";
    }

    @ExceptionHandler(InvalidUserException.class)
    public String handleInvalidUserException(InvalidUserException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExistException(UsernameAlreadyExistException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(DatePeriodException.class)
    public String handleDatePeriodException(DatePeriodException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/events/create-event";
    }

    @ExceptionHandler(PaymentFailException.class)
    public String handleNotificationRetryFailedException(PaymentFailException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/payments";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NoResourceFoundException.class,
            AccessDeniedException.class
    })
    public String handleSpringException() {

        return "not-found";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleLeftoverExceptions() {

        return "internal-server-error";
    }
}
