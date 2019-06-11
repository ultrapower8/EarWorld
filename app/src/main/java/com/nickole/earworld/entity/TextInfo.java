package com.nickole.earworld.entity;

import java.util.List;

/**
 * @author shuzijie
 * @date 2019-05-13.
 */
public class TextInfo {

    private String id;
    private List<String> textList;
    private String type;
    private int tone = 1;
    private int speed = 1;
    private int standstillTime = 1000;

    public List<String> getTextList() {
        return textList;
    }

    public void setTextList(List<String> textList) {
        this.textList = textList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStandstillTime() {
        return standstillTime;
    }

    public void setStandstillTime(int standstillTime) {
        this.standstillTime = standstillTime;
    }

    public int getTone() {
        return tone;
    }

    public void setTone(int tone) {
        this.tone = tone;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public TextInfo(String id, String type, List<String> textList, int speed, int standstillTime, int tone) {
        this.id = id;
        this.textList = textList;
        this.type = type;
        this.speed = speed;
        this.standstillTime = standstillTime;
        this.tone = tone;
    }

    public TextInfo(String id, String type, List<String> textList) {
        this.id = id;
        this.textList = textList;
        this.type = type;
    }
}
