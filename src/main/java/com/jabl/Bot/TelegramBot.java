package com.jabl.Bot;

import com.jabl.grabber.Channel;
import com.jabl.grabber.Trends;
import org.telegram.telegrambots.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramBot extends TelegramLongPollingBot {

Trends trends;

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {//Проверка на пустоту сообщения
            if (message.getText().equals("/start")) {
                sendMsg(message, "Привет. Выбери одну из команд.");
            }
            if (message.getText().toUpperCase().equals("ТРЕНДЫ")) {//Если пользователь вводит "Тренды"
                trends = new Trends();
                try {
                    ArrayList<Channel> channel = (ArrayList<Channel>) trends.getTrends();//Создаем лист, заносим в него все ссылки на трендовые видео
                    for (Channel ch:channel) {
                        sendMsg(message, "*" + ch.getChannelTitle() + "*\n" + ch.getTitle() + "\n" + ch.getDate());//выводим данные ссылки
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (message.getText().toUpperCase().equals("ДОБАВИТЬ КАНАЛ В ЧЕРНЫЙ СПИСОК")) {
                sendMsg(message, "Напиши название каналов которые ты хочешь добавить в черный список.");

            }
            if (message.getText().toUpperCase().equals("ДОБАВИТЬ В ЧЕРНЫЙ СПИСОК ТЕГИ")) {
                sendMsg(message, "Напиши теги которые ты не хочешь, чтобы я присылал.");
            }
            if (message.getText().toUpperCase().equals("УКАЗАТЬ РЕГИОН")) {
                sendMsg(message, "Укажите свой регион.");
            }
        }
    }


    private void sendMsg (Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Тренды");
        keyboardFirstRow.add("Добавить канал в черный список");

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add("Добавить в черный список теги");
        keyboardSecondRow.add("Указать регион");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public String getBotUsername() {
        return "YouTubeTrands";
    }

    public String getBotToken() {
        return "513242666:AAETr653EJNbC82F8uZrrm-uMMhcRkmiigA";
    }

//    private String getId(String adress){
//        //Если в ссылке встречается "_" то вылетает исключение. Данная функция преобразовывает все "_" в "\\_"
//        StringBuffer id = new StringBuffer();
//        id.append(adress);
//        Pattern pat = Pattern.compile("[\\_]");//вписываем какие символы мы будем искать, в данном случае "_"
//        Matcher m = pat.matcher(id);//ищем данный символ в нашей ссылке
//        if(m.find()){
//            id.insert(m.start(), '\\');//если находим, то ставим перед "_" 2 слеша.
//        }
//        return id.toString();
//    }

}
