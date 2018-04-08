package com.jabl.grabber;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Grabber {
    URL url;
    BufferedReader reader;
    StringBuffer buffer;


    public Grabber(URL url) throws IOException {
        this.url = url;
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
        ChannelsInfo parsing = new Gson().fromJson(resultJson, ChannelsInfo.class);//десериализуем наш JSON файл и закидываем всю информацию в ChannelInfo
        return parsing;//возвращаем объект обратно в функцию объект класса.
    }
}
