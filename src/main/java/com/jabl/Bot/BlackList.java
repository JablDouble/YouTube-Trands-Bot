package com.jabl.Bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BlackList {
    private Set<String> blackChannel;
    private Set<String> blacktags;

    public BlackList(){
        blackChannel = new TreeSet<>();
        blacktags = new TreeSet<>();
    }

    public Set<String> getBlackChannel() {
        return blackChannel;
    }

    public void setBlackChannel(Set<String> blackChannel) {
        this.blackChannel = blackChannel;
    }

    public void addBlackChannel(String channel){
        blackChannel.add(channel);
    }

    public Set<String> getBlacktags() {
        return blacktags;
    }

    public void setBlacktags(Set<String> blacktags) {
        this.blacktags = blacktags;
    }

    public void addBlackTags(String tags){
        blackChannel.add(tags);
    }
}