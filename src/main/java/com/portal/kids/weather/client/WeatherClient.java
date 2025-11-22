package com.portal.kids.weather.client;

import com.portal.kids.weather.client.dto.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weatherClient", url = "${weather.api.url}")
public interface WeatherClient {

    @GetMapping("/weather")
    WeatherResponse getCurrentWeather(
            @RequestParam("q") String city,
            @RequestParam("appid") String apiKey,
            @RequestParam("units") String units
    );
}
