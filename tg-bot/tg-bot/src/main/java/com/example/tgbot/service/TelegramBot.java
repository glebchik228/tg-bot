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
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Time;
import java.text.ParseException;
import java.util.Optional;
import java.util.Random;

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
        String photoWeather = "";


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
                    sendMessage(chatId, "время HH:mm \nчто сложного??");
                }
            }

            switch (messageText.toLowerCase()) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "погода":
                    try {
                        weather = WeatherService.getWeather(weatherModel, userModelRepository.findById(chatId).get().getCity());
                        Random random = new Random();
                        int photoIndex = random.nextInt(1, 325);
                        photoWeather = ImageService.getPathImage("C:/Users/Mi/IdeaProjects/tg-bot/tg-bot/tg-bot/src/main/resources/images/" + photoIndex + "_.jpg", weather);

                    } catch (IOException e) {
                        sendMessage(chatId, "ерор, что-то сломалось(");
                    } catch (ParseException e) {
                        throw new RuntimeException("Unable to parse date");
                    }
                    sendPhoto(chatId, photoWeather);
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = name.toLowerCase() + "\n" +
                "привет" + "\n" +
                "я глеб" + "\n" +
                "буду отправлять тебе погоду";
        sendMessage(chatId, answer);
        String mess = "для начала краткий гайд." +
                " если напишешь слово 'погода' я отправлю тебе погоду погоду прямо сейчас в спб." +
                " также я буду отправлять тебе погоду в 9 утра. \n" +
                "если ты вдруг захочешь сменить город или время отправки " +
                "используй комады 'город (твой город на английском)' и 'время (желаемое время отправки в формате HH:mm)' соответственно. \n" +
                "по всем вопросам пожеланиям предложениям приглашениям попить пиво обращаться к @glebchik_gg\n" +
                "глеб не гарантирует что на всех картинках будет разборчив текст.\n" +
                "продолжая пользоваться ботом вы соглашаетесь никогда в жизни не обижать котят";
        sendMessage(chatId, mess);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }
    private void sendPhoto(Long chatId, String path){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));

        sendPhoto.setPhoto(new InputFile(new File(path)));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
