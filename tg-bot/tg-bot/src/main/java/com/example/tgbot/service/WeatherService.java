package com.example.tgbot.service;

import com.example.tgbot.model.WeatherModel;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class WeatherService {

    public static String getWeather(WeatherModel model, String city) throws IOException, ParseException {
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=68e0207421564cb8cf77b20c7de8d1dc&units=metric");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()) {
            result += scanner.nextLine();
        }
        JSONObject object = new JSONObject(result);

        model.setDate(new Date());

        model.setDescription(object.getJSONArray("weather").getJSONObject(0).getString("description"));
        model.setTemperatureAir(object.getJSONObject("main").getInt("temp"));
        return "погода в спб на " + getFormatDate(model) + ":\n" + model.getDescription() + "\n" + "температура воздуха: " + model.getTemperatureAir();
    }

    private static String getFormatDate(WeatherModel model) {
        return new SimpleDateFormat("dd MMM yyyy HH:mm").format(model.getDate());
    }
}
