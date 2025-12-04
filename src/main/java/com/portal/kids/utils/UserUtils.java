package com.portal.kids.utils;

import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.ModelAndView;

@UtilityClass
public class UserUtils {

    public static void addLoginMessages(ModelAndView modelAndView, HttpSession session, String errorMessage) {

        String inactiveUserMessage = (String) session.getAttribute("inactiveUserMessage");

        if (inactiveUserMessage != null) {
            modelAndView.addObject("inactiveAccountMessage", "Inactive user account");

            // Optional: clear message after showing it once
            session.removeAttribute("inactiveUserMessage");

        } else if (errorMessage != null) {
            modelAndView.addObject("errorMessage", "Invalid username or password or inactive account");
        }
    }
}
