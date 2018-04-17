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
import java.util.List;
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
            if (message.getText().equals("/start")) {
                sendMsg(message, "Привет. Выбери одну из команд.");
            }
            else if (message.getText().toUpperCase().equals("ТРЕНДЫ")) {//Если пользователь вводит "Тренды"
                try {
                    ArrayList<Channel> channel = trends.getTrends();//Создаем лист, заносим в него все ссылки на трендовые видео
                    showTrends(message,channel);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (message.getText().toUpperCase().equals("ДОБАВИТЬ КАНАЛ В ЧЕРНЫЙ СПИСОК")) {
                endBlacklist(message, "Напиши название канала которй ты хочешь добавить в черный список. Пишите по одному каналу за раз. Если хотите закончить нажмите кнопку завершения.");
                offOtherFunction("addBlackChannel");
            }
            else if (message.getText().toUpperCase().equals("ДОБАВИТЬ В ЧЕРНЫЙ СПИСОК ТЕГИ")) {
                endBlacklist(message, "Напиши теги которые ты не хочешь, чтобы я присылал. Например *Политика*. Пишите по одному каналу за раз. Если хотите закончить нажмите кнопку завершения.");
                offOtherFunction("addBlackTags");
            }
            else if (message.getText().toUpperCase().equals("УКАЗАТЬ РЕГИОН")) {
                setRegionKeyboard(message, "Укажите свой регион.");
                offOtherFunction("setRegion");
            }
            else if (message.getText().toUpperCase().equals("ЧС")) {
                System.out.println(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel());
                System.out.println(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags());
                System.out.println(addBlackChannel);
                System.out.println(addBlackTags);
            }
            else if (message.getText().toUpperCase().equals("ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ")){
                otherSettingKeyboard(message);
            }
            else if (message.getText().toUpperCase().equals("УДАЛИТЬ КАНАЛ ИЗ ЧС")) {
                if (blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel().size() == 0){
                    sendMsg(message, "Ваш черный список каналов пуст.");
                } else {
                    sendMsg(message, "Укажите название канала, который ты хочешь удалить из черного списка.");
                    offOtherFunction("delBlackChannel");
                }
            }
            else if (message.getText().toUpperCase().equals("УДАЛИТЬ ТЕГ ИЗ ЧС")) {
                if (blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().size() == 0){
                    sendMsg(message, "Ваш черный список тегов пуст.");
                } else {
                    sendMsg(message, "Укажите тег, который ты хочешь удалить из черного списка.");
                    offOtherFunction("delBlackTags");
                }
            }
            else if (message.getText().toUpperCase().equals("НАЗАД")) {
                sendMsg(message,"Вы вернулись в главное меню");
            }
            else if (message.getText().toUpperCase().equals("ПОКАЗАТЬ ЧЕРНЫЙ СПИСОК")) {
                StringBuilder getBlacklist = new StringBuilder();
                getBlacklist.append("Каналы в черном списке:\n");
                getBlacklist.append(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel() + "\n");
                getBlacklist.append("Теги в черном списке:\n");
                getBlacklist.append(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags() + "\n");
                sendMsg(message,getBlacklist.toString());
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
        for (int i = 0; i < blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel().size(); i++) {
            if (ch.getChannelTitle().equals(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel().toArray()[i])) {
                chInBlacklist = true;
            }
        }
        return chInBlacklist;
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

    private Boolean channelInBlacklistTags(Channel ch,Message message){
        Boolean tagsInBlacklist = false;
        if(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags() != null && ch.getTags() != null ) {
            if(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().size() >= 1 && ch.getTags().size() >= 1) {
                for (int i = 0; i < blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().size(); i++) {
                    for (int j = 0; j < ch.getTags().size(); j++) {
                        if (ch.getTags().get(j).equals(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().toArray()[i])) {
                            tagsInBlacklist = true;
                        }
                    }
                }
            }
        }
        return tagsInBlacklist;
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
        for (int i = 0; i < blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel().size(); i++) {
            if (message.getText().equals(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel().toArray()[i])) {
                blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlackChannel().remove(message.getText());
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
        for (int i = 0; i < blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().size(); i++) {
            if (message.getText().equals(blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().toArray()[i])) {
                blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().remove(message.getText());
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
            blackListArray.getBlackListCup().get(message.getChatId().toString()).addBlackChannel(message.getText());
            endBlacklist(message, "Канал " + message.getText() + " добавлен в черный список. Если хотите закончить нажмите кнопку завершения.");
        }
    }

    private void addBlackTags(Message message){
        if (message.getText().toUpperCase().equals("ЗАКОНЧИТЬ ДОБАВЛЕНИЕ")){
            sendMsg(message,"Добавление закончено.");
            addBlackTags = false;
        } else {
            blackListArray.getBlackListCup().get(message.getChatId().toString()).getBlacktags().add(message.getText());
            endBlacklist(message, "Тег " + message.getText() + " добавлен в черный список. Если хотите закончить нажмите кнопку завершения.");
        }
    }

    private void setRegion(Message message){
        if(message.getText().equals("RU") || message.getText().equals("ES") || message.getText().equals("US") || message.getText().equals("KZ")) {
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
