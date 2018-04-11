package com.jabl.Bot;

import java.util.ArrayList;
import java.util.List;

public class BlackList {
    private List<String> blackChannel;
    private List<String> blacktags;

    public BlackList(){
        blackChannel = new ArrayList<>();
        blacktags = new ArrayList<>();
    }

    public List<String> getBlackChannel() {
        return blackChannel;
    }

    public void setBlackChannel(List<String> blackChannel) {
        this.blackChannel = blackChannel;
    }

    public void addBlackChannel(String channel){
        blackChannel.add(channel);
    }

    public List<String> getBlacktags() {
        return blacktags;
    }

    public void setBlacktags(List<String> blacktags) {
        this.blacktags = blacktags;
    }

    public void addBlackTags(String tags){
        blackChannel.add(tags);
    }
}