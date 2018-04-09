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

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {//Проверка на пустоту сообщения
            if (message.getText().equals("/info") || message.getText().equals("/start")) {
                sendMsg(message, "Привет. Напиши 'Тренды' ");
            }
            if (message.getText().toUpperCase().equals("ТРЕНДЫ") || message.getText().toUpperCase().equals("TRANDS")) {//Если пользователь вводит "Тренды"
                try {
                    ArrayList<Channel> trands = (ArrayList) new Trends().getTrends();//Создаем лист, заносим в него все ссылки на трендовые видео
                    for (int i = 0; i < 10; i++) {
                        Channel ch = trands.get(i);
                        sendMsg(message, /*getId(ch.getAdress())+"\n"+*/"*"+ch.getChannelTitle()+"*\n"+ch.getTitle()+"\n"+ch.getDate());//выводим данные ссылки
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void sendMsg(Message msg,String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(msg.getChatId().toString());
        sendMessage.setText(s);
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

    private String getId(String adress){
        //Если в ссылке встречается "_" то вылетает исключение. Данная функция преобразовывает все "_" в "\\_"
        StringBuffer id = new StringBuffer();
        id.append(adress);
        Pattern pat = Pattern.compile("[\\_]");//вписываем какие символы мы будем искать, в данном случае "_"
        Matcher m = pat.matcher(id);//ищем данный символ в нашей ссылке
        if(m.find()){
            id.insert(m.start(), '\\');//если находим, то ставим перед "_" 2 слеша.
        }
        return id.toString();
    }
}
