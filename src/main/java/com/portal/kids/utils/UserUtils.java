package com.portal.kids.utils;

import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.ModelAndView;

@UtilityClass
public class UserUtils {

    public static void addLoginMessages(ModelAndView modelAndView, String errorMessage, HttpSession session) {

        String inactiveUserMessage = (String) session.getAttribute("inactiveUserMessage");

        if (inactiveUserMessage != null) {
            modelAndView.addObject("inactiveUserMessage", inactiveUserMessage);

            // Optional: clear message after showing it once
            session.removeAttribute("inactiveUserMessage");

        }
        if (errorMessage != null) {
            modelAndView.addObject("errorMessage", "Username or password is incorrect.");
        }
    }
}
