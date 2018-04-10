package com.jabl.grabber;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Grabber {
    private BufferedReader reader;
    private StringBuffer buffer;

    public ChannelsInfo makeParsing(String region) throws IOException {
        getConnection(new URL(
                 "https://www.googleapis.com/youtube/v3/videos?" +
                       "part=id%2C+snippet&" +
                       "chart=mostPopular&regionCode="+ region +"&maxResults=10&" +
                       "key=AIzaSyBfsj9xmTSFy9hIHM9sDqimg0XvHstjpiY"));
        return getTrends();
    }

    private void getConnection(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        //делаем get запрос

        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        this.reader = reader;
        this.buffer = buffer;
        //создаем reader с помощью которого будем считывать ответ и буффер, куда будем записывать ответ.
    }

    public ChannelsInfo getTrends() {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);//добавляем в наш буффер информацию, которую мы получаем с сервера. До тех пор, пока ответ не пустой.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultJson = buffer.toString(); // переводим нашу информацию в Стринг.
        ChannelsInfo channelsInfo = new Gson().fromJson(resultJson, ChannelsInfo.class);//десериализуем наш JSON файл и закидываем всю информацию в ChannelInfo
        return channelsInfo;//возвращаем объект обратно в функцию объект класса.
    }
}
