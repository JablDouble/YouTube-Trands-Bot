package com.jabl.grabber;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trends {

    public List<Channel> getTrends() throws IOException {
        Grabber grabber = new Grabber(new URL(
                "https://www.googleapis.com/youtube/v3/videos?" +
                        "part=id%2C+snippet&" +
                        "chart=mostPopular&regionCode=RU&maxResults=10&" +
                        "key=AIzaSyBfsj9xmTSFy9hIHM9sDqimg0XvHstjpiY"));//Вкидываем get запрос
        ChannelsInfo infoTrends = grabber.getTrends(); //получаем данные о трендах
        List<Channel> output = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> info = (Map<String, String>) infoTrends.items.get(i);//Получаем всю информацию по отдельности о каждом канале
            Channel channel = addFields(info);
            output.add(channel);//На выход нам нужна только ссылка на видео, поэтому добавляем только ее в лист.
        }
        return output;
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
        Map<String,String> image = thumbnails.get("medium");
        channel.setPictures(image.get("url"));
        channel.setDate(snippet1.get("publishedAt"));

//        channel.setPictures();
        return channel;
    }
}
