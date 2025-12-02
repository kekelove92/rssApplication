package com.example.rssapplication.model;

import java.util.Date;

public class RssArticle {
    private int id;
    private int feedId;
    private String title;
    private String description;
    private String link;
    private Date pubDate;
    private String imageUrl;
    private boolean isSaved;
    private String category;

    public RssArticle() {
    }

    public RssArticle(int id, int feedId, String title, String description, String link, 
                      Date pubDate, String imageUrl, boolean isSaved, String category) {
        this.id = id;
        this.feedId = feedId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.imageUrl = imageUrl;
        this.isSaved = isSaved;
        this.category = category;
    }

    public RssArticle(int feedId, String title, String description, String link, 
                      Date pubDate, String imageUrl, String category) {
        this.feedId = feedId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.imageUrl = imageUrl;
        this.isSaved = false;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "RssArticle{" +
                "id=" + id +
                ", feedId=" + feedId +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", pubDate=" + pubDate +
                ", isSaved=" + isSaved +
                ", category='" + category + '\'' +
                '}';
    }
}

