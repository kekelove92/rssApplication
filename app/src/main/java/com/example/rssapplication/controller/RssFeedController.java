package com.example.rssapplication.controller;

import android.content.Context;

import com.example.rssapplication.database.DatabaseHelper;
import com.example.rssapplication.model.RssFeed;

import java.util.List;

public class RssFeedController {

    private DatabaseHelper dbHelper;

    public RssFeedController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long addFeed(String name, String url, String category) {
        RssFeed feed = new RssFeed(name, url, category);
        return dbHelper.addFeed(feed);
    }

    public RssFeed getFeed(int id) {
        return dbHelper.getFeed(id);
    }

    public List<RssFeed> getAllFeeds() {
        return dbHelper.getAllFeeds();
    }

    public List<String> getAllCategories() {
        return dbHelper.getAllCategories();
    }

    public boolean deleteFeed(int id) {
        return dbHelper.deleteFeed(id) > 0;
    }

    public boolean isFeedUrlValid(String url) {
        return url != null && 
               (url.startsWith("http://") || url.startsWith("https://")) &&
               (url.contains(".rss") || url.contains("/rss") || url.contains("/feed"));
    }
}

