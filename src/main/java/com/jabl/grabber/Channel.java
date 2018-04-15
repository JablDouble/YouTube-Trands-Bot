package com.jabl.grabber;

import org.telegram.telegrambots.api.methods.send.SendPhoto;

import java.util.Date;
import java.util.List;

public class Channel {
    private String adress;
    private String title;
    private String channelTitle;
    private String description;
    private SendPhoto pictures;
    private String date;
    private List tags;

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SendPhoto getPictures() {
        return pictures;
    }

    public void setPictures(SendPhoto pictures) {
        this.pictures = pictures;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List getTags() {
        return tags;
    }

    public void setTags(List tags) {
        this.tags = tags;
    }
}
