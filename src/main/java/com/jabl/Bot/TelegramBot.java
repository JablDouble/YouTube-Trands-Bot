package com.jabl.Bot;

import com.jabl.grabber.Channel;
import com.jabl.grabber.Trends;
import org.telegram.telegrambots.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.api.objects.stickers.Sticker;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramBot extends TelegramLongPollingBot {

private BlackList blackList;
private Trends trends;
private Boolean addBlackChannel;
private Boolean addBlackTags;
private Boolean delBlackTags;
private Boolean delBlackChannel;

    public TelegramBot() {
        blackList = new BlackList();
        trends = new Trends();
        addBlackChannel = false;
        addBlackTags = false;
        delBlackTags = false;
        delBlackChannel = false;
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {//Проверка на пустоту сообщения
            if (message.getText().equals("/start")) {
                sendMsg(message, "Привет. Выбери одну из команд.");
            }
            else if (message.getText().toUpperCase().equals("ТРЕНДЫ")) {//Если пользователь вводит "Тренды"
                try {
                    ArrayList<Channel> channel = (ArrayList<Channel>) trends.getTrends();//Создаем лист, заносим в него все ссылки на трендовые видео
                    for (Channel ch:channel) {
                        Boolean chInBlacklistChannel = channelInBlacklistChannel(ch);
                        Boolean chInBlackListTags = channelInBlacklistTags(ch);
                        if (!chInBlacklistChannel && !chInBlackListTags) {
                            sendMsg(message, "*" + ch.getChannelTitle() + "*\n" + ch.getTitle() + "\n" + ch.getDate());//выводим данные ссылки
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (message.getText().toUpperCase().equals("ДОБАВИТЬ КАНАЛ В ЧЕРНЫЙ СПИСОК")) {
                sendMsg(message, "Напиши название канала которй ты хочешь добавить в черный список. Пиши по одному каналу за раз. Если ты передумал добавлять напиши 'Конец'");
                addBlackChannel = true;
            }
            else if (message.getText().toUpperCase().equals("ДОБАВИТЬ В ЧЕРНЫЙ СПИСОК ТЕГИ")) {
                sendMsg(message, "Напиши теги которые ты не хочешь, чтобы я присылал. Например *Политика*.  Пиши по одному каналу за раз. Если ты передумал добавлять напиши 'Конец'");
                addBlackTags = true;
            }
            else if (message.getText().toUpperCase().equals("УКАЗАТЬ РЕГИОН")) {
                sendMsg(message, "Укажите свой регион.");
            }
            else if (message.getText().toUpperCase().equals("УДАЛИТЬ КАНАЛ ИЗ ЧС")) {
                if (blackList.getBlackChannel().size() == 0){
                    sendMsg(message, "Ваш черный список каналов пуст.");
                } else {
                    sendMsg(message, "Укажите название канала, который ты хочешь удалить из черного списка.");
                    delBlackChannel = true;
                }
            }
            else if (message.getText().toUpperCase().equals("УДАЛИТЬ ТЕГ ИЗ ЧС")) {
                if (blackList.getBlacktags().size() == 0){
                    sendMsg(message, "Ваш черный список тегов пуст.");
                } else {
                    sendMsg(message, "Укажите тег, который ты хочешь удалить из черного списка.");
                    delBlackTags = true;
                }
            }
            else {
                if(addBlackChannel){
                    if (message.getText().toUpperCase().equals("КОНЕЦ")){
                        sendMsg(message,"Добавление закончено.");
                        addBlackChannel = false;
                    } else {
                        blackList.addBlackChannel(message.getText());
                        sendMsg(message, "Канал " + message.getText() + " добавлен в черный список. Если хочешь закончить напиши 'Конец'");
                    }
                }
                else if(addBlackTags){
                    if (message.getText().toUpperCase().equals("КОНЕЦ")){
                        sendMsg(message,"Добавление закончено.");
                        addBlackTags = false;
                    } else {
                        blackList.getBlacktags().add(message.getText());
                        sendMsg(message, "Тег " + message.getText() + " добавлен в черный список. Если хочешь закончить напиши 'Конец'");
                    }
                }
                else if(delBlackChannel){
                    Boolean delete = false;
                    for (int i = 0; i < blackList.getBlackChannel().size(); i++) {
                        if (message.getText().equals(blackList.getBlackChannel().get(i))) {
                                blackList.getBlackChannel().remove(i);
                                sendMsg(message, "Канал " + message.getText() + " удален из черного списка.");
                                delete = true;
                        }
                    }
                    if(!delete){
                       sendMsg(message,"Канал " + message.getText() + " не находится в черном списке");
                    }
                    delBlackChannel = false;
                }
                else if(delBlackTags){
                    Boolean delete = false;
                    for (int i = 0; i < blackList.getBlacktags().size(); i++) {
                        if (message.getText().equals(blackList.getBlacktags().get(i))) {
                            blackList.getBlacktags().remove(i);
                            sendMsg(message, "Тег " + message.getText() + " удален из черного списка.");
                            delete = true;
                        }
                    }
                    if(!delete){
                        sendMsg(message,"Тег " + message.getText() + " не находится в черном списке");
                    }
                    delBlackTags = false;
                }
                else {
                    sendMsg(message, "Прости я не сильно люблю болтать, я всего лишь исскуственный интелект, который выполняет свою задачу. БИП-БУП-БАБ-БАБ");
                    //sendSticker("CAADAgADFAEAArnzlwuWSQcmEDh9mwI");
                }
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
        keyboardFirstRow.add("Указать регион");

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add("Добавить канал в черный список");
        keyboardSecondRow.add("Добавить в черный список теги");

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardThirdRow.add("Удалить канал из ЧС");
        keyboardThirdRow.add("Удалить тег из ЧС");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
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

    private Boolean channelInBlacklistChannel(Channel ch){
        Boolean chInBlacklist = false;
        for (int i = 0; i < blackList.getBlackChannel().size(); i++) {
            if (ch.getChannelTitle().toUpperCase().equals(blackList.getBlackChannel().get(i).toUpperCase())) {
                chInBlacklist = true;
            }
        }
        return chInBlacklist;
    }

    private Boolean channelInBlacklistTags(Channel ch){
        Boolean tagsInBlacklist = false;
        if(blackList.getBlacktags() != null && ch.getTags() != null ) {
            if(blackList.getBlacktags().size() >= 1 && ch.getTags().size() >= 1) {
                for (int i = 0; i < blackList.getBlacktags().size(); i++) {
                    for (int j = 0; j < ch.getTags().size(); j++) {
                        if (ch.getTags().get(j).equals(blackList.getBlacktags().get(i))) {
                            tagsInBlacklist = true;
                        }
                    }
                }
            }
        }
        return tagsInBlacklist;
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
