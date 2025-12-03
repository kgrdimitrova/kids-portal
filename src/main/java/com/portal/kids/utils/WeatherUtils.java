package com.portal.kids.utils;

import com.portal.kids.weather.client.dto.WeatherResponse;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
    public class WeatherUtils {

    public static String extractIcon(WeatherResponse weather) {
        return Optional.ofNullable(weather)
                .map(WeatherResponse::getWeather)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(WeatherResponse.Weather::getIcon)
                .orElse("img.png");
    }
    }
