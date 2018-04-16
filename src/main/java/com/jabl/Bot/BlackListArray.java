package com.jabl.Bot;

import java.util.HashMap;
import java.util.Map;

public class BlackListArray {
    private Map<String,BlackList> blackListCup = new HashMap<>();

    public Map<String, BlackList> getBlackListCup() {
        return blackListCup;
    }

    public void addBlackListCup(String name,BlackList blackList){
        blackListCup.put(name,blackList);
    }
}
