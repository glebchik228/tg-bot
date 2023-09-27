package com.example.tgbot.model;

import lombok.Data;

import java.util.Date;

@Data
public class WeatherModel {
    Integer temperatureAir;
    String description;
    Date date;
}
