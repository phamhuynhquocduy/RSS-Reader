package com.example.rssreader.model;

import java.io.Serializable;

public class RssFeedModel implements Serializable {

    private String title;
    private String link;
    private String date;

    public RssFeedModel(String title, String link, String date) {
        this.title = title;
        this.link = link;
        this.date = date;
    }

    @Override
    public String toString() {
        return "RssFeedModel{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + date + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}