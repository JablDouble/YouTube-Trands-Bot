package com.jabl.grabber;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelsInfo {
    @SerializedName("items")
    private List items;//Хранит всю информацию о трендах, включая какие-то особенности видео.

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
}
