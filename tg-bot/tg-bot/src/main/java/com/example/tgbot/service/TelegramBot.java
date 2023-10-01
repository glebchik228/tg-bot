package com.example.tgbot.service;

import com.example.tgbot.config.BotConfig;
import com.example.tgbot.model.UserModel;
import com.example.tgbot.model.WeatherModel;
import com.example.tgbot.repository.UserModelRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.util.Optional;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    @Autowired
    private UserModelRepository userModelRepository;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        WeatherModel weatherModel = new WeatherModel();
        String weather = "";



        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (!userModelRepository.existsById(chatId)){
                UserModel user = new UserModel(
                        chatId,
                        update.getMessage().getChat().getFirstName(),
                        update.getMessage().getChat().getLastName(),
                        update.getMessage().getChat().getUserName());
                userModelRepository.save(user);
            }

            if (messageText.toLowerCase().split(" ")[0].equals("город")) {
                String city = messageText.substring(6);
                UserModel user = userModelRepository.findById(chatId).get();
                user.setCity(city);
                userModelRepository.save(user);
                sendMessage(chatId, "ура ты живешь в " + city);
            }
            if (messageText.toLowerCase().split(" ")[0].equals("время")) {
                try {
                    Time time = Time.valueOf(messageText.substring(6) + ":00");
                    UserModel user = userModelRepository.findById(chatId).get();
                    user.setTime(time);
                    userModelRepository.save(user);
                    sendMessage(chatId, "буду беспокоить тебя в " + time);
                }
                catch (Exception e){
                    sendMessage(chatId, "время HH:mm \n что сложного??");
                }
            }

            switch (messageText.toLowerCase()) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "погода":
                    try {
                        weather = WeatherService.getWeather(weatherModel, userModelRepository.findById(chatId).get().getCity());

                    } catch (IOException e) {
                        sendMessage(chatId, "ерор, что-то сломалось(");
                    } catch (ParseException e) {
                        throw new RuntimeException("Unable to parse date");
                    }
                    sendMessage(chatId, weather);
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = name.toLowerCase() + "\n" +
                "сап" + "\n" +
                "я глеб" + "\n" +
                "буду отправлять тебе погоду";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}
