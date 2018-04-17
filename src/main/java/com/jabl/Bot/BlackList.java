package com.jabl.Bot;

import java.util.*;

public class BlackList {
    private TreeSet<String> blackChannel;
    private TreeSet<String> blacktags;

    public BlackList(){
        blackChannel = new TreeSet<>();
        blacktags = new TreeSet<>();
    }

    public TreeSet<String> getBlackChannel() {
        return blackChannel;
    }

    public void setBlackChannel(TreeSet<String> blackChannel) {
        this.blackChannel = blackChannel;
    }

    public void addBlackChannel(String channel){
        blackChannel.add(channel);
    }

    public TreeSet<String> getBlacktags() {
        return blacktags;
    }

    public void setBlacktags(TreeSet<String> blacktags) {
        this.blacktags = blacktags;
    }

    public void addBlackTags(String tags){
        blackChannel.add(tags);
    }
}