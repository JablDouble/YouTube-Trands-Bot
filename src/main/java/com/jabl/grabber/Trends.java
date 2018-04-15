package com.jabl.grabber;

import org.telegram.telegrambots.api.methods.send.SendPhoto;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trends {

    private String region = "RU";


    public List<Channel> getTrends() throws IOException {
        ChannelsInfo channelsInfo = new Grabber().makeParsing(region);//Вкидываем get запрос и получаем данные
        List<Channel> trendsChannel = new ArrayList<>(); // будем хранить здесь каналы которые находятся в трендах.
        for (int i = 0; i < 10; i++) {
            Map<String, String> info = (Map<String, String>) channelsInfo.getItems().get(i);//Получаем всю информацию по отдельности о каждом канале
            Channel channel = addFields(info);
            trendsChannel.add(channel);
        }
        return trendsChannel;
    }

    private Channel addFields(Map map){
        Channel channel = new Channel();
        channel.setAdress("https://www.youtube.com/watch?v="+(String) map.get("id"));
        Map<String,String> snippet1 = (Map<String, String>) map.get("snippet");
        channel.setTitle(snippet1.get("title"));
        channel.setDescription(snippet1.get("description"));
        channel.setChannelTitle(snippet1.get("channelTitle"));
        Map<String,List<String>> snippet2 = (Map<String, List<String>>) map.get("snippet");
        List tags = snippet2.get("tags");
        channel.setTags(tags);
        Map<String,Map<String,String>> thumbnails = (Map<String, Map<String, String>>) snippet2.get("thumbnails");
        Map<String,String> image = thumbnails.get("high");
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(image.get("url"));
        channel.setPictures(sendPhoto);
        channel.setDate(snippet1.get("publishedAt"));
        return channel;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
