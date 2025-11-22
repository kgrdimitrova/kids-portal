package com.portal.kids.weather.service;

import com.portal.kids.weather.client.WeatherClient;
import com.portal.kids.weather.client.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public WeatherResponse getWeather(String city) {
        return weatherClient.getCurrentWeather(city, apiKey, "metric");
    }
}
