package com.example.rssreader;

import java.io.Serializable;

public class RssFeedModel implements Serializable {

    private String title;
    private String link;
    private String description;
    private String image;

    public RssFeedModel(String title, String link, String description, String image) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.image = image;
    }
    @Override
    public String toString() {
        return "RssFeedModel{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
