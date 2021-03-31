package com.example.roinovel_2.Novel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Novel implements Serializable {
    private String Name;
    private String Author;
    private int ImgId=0;
    private String LastUpdate;
    private String MainUrl;
    private final Map<Integer,String> ChapterName = new HashMap<>();
    private final Map<Integer,String> ChapterUrlList = new HashMap<>();

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public int getImgId() {
        return ImgId;
    }

    public void setImgId(int imgId) {
        ImgId = imgId;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        LastUpdate = lastUpdate;
    }

    public String getMainUrl() {
        return MainUrl;
    }

    public void setMainUrl(String mainUrl) {
        MainUrl = mainUrl;
    }

    public Map<Integer, String> getChapterName() {
        return ChapterName;
    }

    public Map<Integer, String> getChapterUrlList() {
        return ChapterUrlList;
    }

    public String string()
    {
        return "小说名：" + this.Name + "\n作者：" + this.Author + "\n最后更新：" + this.LastUpdate + "\n" ;
    }
}
