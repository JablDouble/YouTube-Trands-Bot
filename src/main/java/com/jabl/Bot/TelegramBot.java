package com.jabl.Bot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jabl.grabber.Channel;
import com.jabl.grabber.Trends;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramBot extends TelegramLongPollingBot {

private BlackListArray blackListArray;
private Trends trends;
private Boolean addBlackChannel;
private Boolean addBlackTags;
private Boolean delBlackTags;
private Boolean delBlackChannel;
private Boolean setRegion;

    public TelegramBot() {
        blackListArray = new BlackListArray();
        trends = new Trends();
        addBlackChannel = false;
        addBlackTags = false;
        delBlackTags = false;
        delBlackChannel = false;
        setRegion = false;

    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        createBlackList(message);
        if (message != null && message.hasText()) {//Проверка на пустоту сообщения
            String messageCapsLock = message.getText().toUpperCase();
            if (message.getText().equals("/start")) {
                sendMsg(message, "Привет. Выбери одну из команд.");
            }
            else if (messageCapsLock.equals("ТРЕНДЫ")) {//Если пользователь вводит "Тренды"
                try {
                    ArrayList<Channel> channel = trends.getTrends();//Создаем лист, заносим в него все ссылки на трендовые видео
                    showTrends(message,channel);
                    sendMsg(message,"Для управления воспользуйтесь клавиатурой");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (messageCapsLock.equals("ДОБАВИТЬ КАНАЛ В ЧЕРНЫЙ СПИСОК")) {
                endBlacklist(message, "Напиши название канала которй ты хочешь добавить в черный список. Пишите по одному каналу за раз. Если хотите закончить нажмите кнопку завершения.");
                offOtherFunction("addBlackChannel");
            }
            else if (messageCapsLock.equals("ДОБАВИТЬ В ЧЕРНЫЙ СПИСОК ТЕГИ")) {
                endBlacklist(message, "Напиши теги которые ты не хочешь, чтобы я присылал. Например *Политика*. Пишите по одному каналу за раз. Если хотите закончить нажмите кнопку завершения.");
                offOtherFunction("addBlackTags");
            }
            else if (messageCapsLock.equals("УКАЗАТЬ РЕГИОН")) {
                setRegionKeyboard(message, "Укажите свой регион.");
                offOtherFunction("setRegion");
            }
            else if (messageCapsLock.equals("ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ")){
                otherSettingKeyboard(message);
            }
            else if (messageCapsLock.equals("УДАЛИТЬ КАНАЛ ИЗ ЧС")) {
                long blacklistChannelSize = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel().size();
                if (blacklistChannelSize == 0){
                    sendMsg(message, "Ваш черный список каналов пуст.");
                } else {
                    sendMsg(message, "Укажите название канала, который ты хочешь удалить из черного списка.");
                    offOtherFunction("delBlackChannel");
                }
            }
            else if (messageCapsLock.equals("УДАЛИТЬ ТЕГ ИЗ ЧС")) {
                long blacklistTagsSize = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().size();
                if (blacklistTagsSize == 0){
                    sendMsg(message, "Ваш черный список тегов пуст.");
                } else {
                    sendMsg(message, "Укажите тег, который ты хочешь удалить из черного списка.");
                    offOtherFunction("delBlackTags");
                }
            }
            else if (messageCapsLock.equals("НАЗАД")) {
                sendMsg(message,"Вы вернулись в главное меню");
            }
            else if (messageCapsLock.equals("ПОКАЗАТЬ ЧЕРНЫЙ СПИСОК")) {
                showBlacklist(message);
            }
            else {
                if(addBlackChannel){
                    addBlackChannel(message);
                }
                else if(addBlackTags){
                    addBlackTags(message);
                }
                else if(delBlackChannel){
                    delBlacklistChannel(message);
                    delBlackChannel = false;
                }
                else if(delBlackTags){
                    delBlacklistTag(message);
                    delBlackTags = false;
                }
                else if(setRegion){
                    setRegion(message);
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

        ReplyKeyboardMarkup replyKeyboardMarkup = showKeyboard();// Создаем клавиуатуру
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard = makeMenuKeyboard(keyboard);
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void otherSettingKeyboard(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = showKeyboard();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard = makeSettingKeyboard(keyboard);
        replyKeyboardMarkup.setKeyboard(keyboard);// и устанваливаем этот список нашей клавиатуре

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Выберете функцию");
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup showKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private void sendButton(Message message,Channel channel,String url,SendPhoto sendPhoto) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Смотреть на " + channel.getChannelTitle()).setUrl(getId(url)));
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        String text = channel.getChannelTitle() + "\n" + channel.getTitle();
        sendPhoto.setChatId(message.getChatId().toString()).setCaption(text);
        sendPhoto.setReplyMarkup(markupInline);
        sendPhoto(sendPhoto); // Call method to send the photo
    }

    private void setRegionKeyboard(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = showKeyboard();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard = makeRegionKeyboard(keyboard);

        replyKeyboardMarkup.setKeyboard(keyboard);// и устанваливаем этот список нашей клавиатуре

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void endBlacklist(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        ReplyKeyboardMarkup replyKeyboardMarkup = showKeyboard();// Создаем клавиуатуру
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        List<KeyboardRow> keyboard = new ArrayList<>();// Создаем список строк клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();// Первая строчка клавиатуры
        keyboardFirstRow.add("Закончить добавление");
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private Boolean channelInBlacklistChannel(Channel ch, Message message){
        Boolean chInBlacklist = false;
        long blacklistChannelSize = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel().size();
        TreeSet<String> blacklistCH = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel();
        for (int i = 0; i < blacklistChannelSize; i++) {
            if (ch.getChannelTitle().equals(blacklistCH.toArray()[i])) {
                chInBlacklist = true;
            }
        }
        return chInBlacklist;
    }

    private Boolean channelInBlacklistTags(Channel ch,Message message){
        Boolean tagsInBlacklist = false;
        TreeSet<String> blacklistTG = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags();
        if(blacklistTG != null && ch.getTags() != null ) {
            if(blacklistTG.size() >= 1 && ch.getTags().size() >= 1) {
                for (int i = 0; i < blacklistTG.size(); i++) {
                    for (int j = 0; j < ch.getTags().size(); j++) {
                        if (ch.getTags().get(j).equals(blacklistTG.toArray()[i])) {
                            tagsInBlacklist = true;
                        }
                    }
                }
            }
        }
        return tagsInBlacklist;
    }

    private void offOtherFunction(String action){
        if(action.equals("addBlackChannel")){
            addBlackTags = false;
            delBlackTags = false;
            delBlackChannel = false;
            setRegion = false;
            addBlackChannel = true;
        }
        if(action.equals("addBlackTags")){
            addBlackChannel = false;
            delBlackTags = false;
            delBlackChannel = false;
            setRegion = false;
            addBlackTags = true;
        }
        if(action.equals("delBlackChannel")){
            addBlackTags = false;
            addBlackChannel = false;
            delBlackTags = false;
            setRegion = false;
            delBlackChannel = true;
        }
        if(action.equals("delBlackTags")){
            addBlackTags = false;
            addBlackChannel = false;
            delBlackChannel = false;
            setRegion = false;
            delBlackTags = true;
        }
        if(action.equals("setRegion")){
            addBlackTags = false;
            addBlackChannel = false;
            delBlackTags = false;
            delBlackChannel = false;
            setRegion = true;
        }
    }


    private void createBlackList(Message message){
        if(blackListArray.getBlackListCup().size() == 0){
            blackListArray.addBlackListCup(message.getChatId().toString(),new BlackList());
        }
        else if (!blackListArray.getBlackListCup().containsKey(message.getChatId().toString())){
            blackListArray.addBlackListCup(message.getChatId().toString(),new BlackList());
        }
    }

    private void showTrends(Message message,List<Channel> channel) throws TelegramApiException {
        int video = 10;
        for (int i = 0; i < video; i++) {
            Boolean chInBlacklistChannel = channelInBlacklistChannel(channel.get(i),message);
            Boolean chInBlackListTags = channelInBlacklistTags(channel.get(i),message);
            if (!chInBlacklistChannel && !chInBlackListTags) {
                sendButton(message, channel.get(i),channel.get(i).getAdress(),channel.get(i).getPictures());
            }
            if(chInBlacklistChannel || chInBlackListTags){
                video++;
            }
        }
    }

    private void delBlacklistChannel(Message message){
        Boolean delete = false;
        TreeSet<String> blacklistCH = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel();
        for (int i = 0; i < blacklistCH.size(); i++) {
            if (message.getText().equals(blacklistCH.toArray()[i])) {
                blacklistCH.remove(message.getText());
                sendMsg(message, "Канал " + message.getText() + " удален из черного списка.");
                delete = true;
            }
        }
        if(!delete){
            sendMsg(message,"Канал " + message.getText() + " не находится в черном списке");
        }
    }

    private void delBlacklistTag(Message message){
        Boolean delete = false;
        TreeSet<String> blacklistTG = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags();
        for (int i = 0; i < blacklistTG.size(); i++) {
            if (message.getText().equals(blacklistTG.toArray()[i])) {
                blacklistTG.remove(message.getText());
                sendMsg(message, "Тег " + message.getText() + " удален из черного списка.");
                delete = true;
            }
        }
        if(!delete){
            sendMsg(message,"Тег " + message.getText() + " не находится в черном списке");
        }
    }

    private void addBlackChannel(Message message){
        if (message.getText().toUpperCase().equals("ЗАКОНЧИТЬ ДОБАВЛЕНИЕ")){
            sendMsg(message,"Добавление закончено.");
            addBlackChannel = false;
        } else {
            BlackList addblacklist = blackListArray.getBlackListCup().get(message.getChatId().toString());
            addblacklist.addBlackChannel(message.getText());
            endBlacklist(message, "Канал " + message.getText() + " добавлен в черный список. Если хотите закончить нажмите кнопку завершения.");
        }
    }

    private void addBlackTags(Message message){
        String messageCapslock = message.getText().toUpperCase();
        if (messageCapslock.equals("ЗАКОНЧИТЬ ДОБАВЛЕНИЕ")){
            sendMsg(message,"Добавление закончено.");
            addBlackTags = false;
        } else {
            TreeSet<String> addblacklistTG = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags();
            addblacklistTG.add(message.getText());
            endBlacklist(message, "Тег " + message.getText() + " добавлен в черный список. Если хотите закончить нажмите кнопку завершения.");
        }
    }

    private void setRegion(Message message){
        Boolean russian = message.getText().equals("RU");
        Boolean espan = message.getText().equals("ES");
        Boolean usa = message.getText().equals("US");
        Boolean kaz = message.getText().equals("KZ");
        if(russian || espan || usa || kaz) {
            trends.setRegion(message.getText());
            setRegion = false;
            sendMsg(message, "Регион был успешно изменен.");
        } else{
            sendMsg(message, "Ошибка, вы ввели регион не находящийся в списке.");
        }
    }

    private List<KeyboardRow> makeMenuKeyboard(List<KeyboardRow> keyboard){
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("Тренды");
        keyboardFirstRow.add("Дополнительные настройки");
        KeyboardRow keyboardSecondRow = new KeyboardRow();// Вторая строчка клавиатуры
        keyboardSecondRow.add("Добавить канал в черный список");
        keyboardSecondRow.add("Добавить в черный список теги");
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add("Удалить канал из ЧС");
        keyboardThirdRow.add("Удалить тег из ЧС");
        keyboard.add(keyboardFirstRow);// Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        return keyboard;
    }

    private List<KeyboardRow> makeRegionKeyboard(List<KeyboardRow> keyboard){
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        KeyboardRow keyboardForthRow = new KeyboardRow();
        keyboardFirstRow.add("RU");// Добавляем кнопки в первую строчку клавиатуры
        keyboardSecondRow.add("US");
        keyboardThirdRow.add("ES");
        keyboardForthRow.add("KZ");
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        keyboard.add(keyboardForthRow);
        return keyboard;
    }

    private List<KeyboardRow> makeSettingKeyboard(List<KeyboardRow> keyboard){
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardFirstRow.add("Указать регион");// Добавляем кнопки в первую строчку клавиатуры
        keyboardSecondRow.add("Показать черный список");
        keyboardThirdRow.add("Назад");
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        return keyboard;
    }

    private void showBlacklist(Message message){
        StringBuilder getBlacklist = new StringBuilder();
        TreeSet<String> blacklistCH = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel();
        TreeSet<String> blacklistTG = blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags();
        getBlacklist.append("Каналы в черном списке:\n");
        getBlacklist.append(blacklistCH + "\n");
        getBlacklist.append("Теги в черном списке:\n");
        getBlacklist.append(blacklistTG + "\n");
        sendMsg(message,getBlacklist.toString());
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
