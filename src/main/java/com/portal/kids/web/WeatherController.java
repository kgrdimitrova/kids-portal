package com.portal.kids.web;

import com.portal.kids.weather.client.dto.WeatherResponse;
import com.portal.kids.weather.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public String showWeather(@RequestParam(defaultValue = "Sofia") String city, ModelAndView modelAndView) {
        WeatherResponse weather = weatherService.getWeather(city);
        modelAndView.addObject("weather", weather);
        return "index"; // Thymeleaf template
    }
}
