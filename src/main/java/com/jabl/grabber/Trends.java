package com.jabl.grabber;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trends {

    public List getTrends() throws IOException {
        Grabber grabber = new Grabber(new URL(
                "https://www.googleapis.com/youtube/v3/videos?" +
                        "part=contentDetails" +
                        "&chart=mostPopular" +
                        "&regionCode=RU" +
                        "&maxResults=10" +
                        "&key=AIzaSyBfsj9xmTSFy9hIHM9sDqimg0XvHstjpiY"));//Вкидываем get запрос
        ChannelsInfo infoTrends = grabber.getTrends(); //получаем данные о трендах
        List<String> output = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> info = (Map<String, String>) infoTrends.items.get(i);//Получаем всю информацию по отдельности о каждом канале
            output.add("https://www.youtube.com/watch?v=" + info.get("id"));//На выход нам нужна только ссылка на видео, поэтому добавляем только ее в лист.
        }
        return output;
    }
}
