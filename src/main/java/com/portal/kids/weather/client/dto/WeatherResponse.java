package com.portal.kids.weather.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeatherResponse {
    private Main main;
    private List<Weather> weather;
    private Wind wind;
    private String name; // City name

    @Data
    public static class Main {
        private double temp;
        private double feels_like;
        private double temp_min;
        private double temp_max;
        private int humidity;
    }

    @Data
    public static class Weather {
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Wind {
        private double speed;
    }
}
