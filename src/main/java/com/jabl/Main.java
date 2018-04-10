package com.jabl;


import com.jabl.Bot.TelegramBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TelegramBot());//регистрируем нового бота и управление передаем на TelegramBot
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
