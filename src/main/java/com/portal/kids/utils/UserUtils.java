package com.portal.kids.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.ModelAndView;

@UtilityClass
public class UserUtils {

    public static void addLoginMessages(ModelAndView modelAndView, String errorMessage, String loginAttemptMessage) {

        if (loginAttemptMessage != null) {

            modelAndView.addObject("loginAttemptMessage", loginAttemptMessage);
        }
        if (errorMessage != null) {

            modelAndView.addObject("errorMessage", "Username or password is incorrect.");
        }
    }
}
