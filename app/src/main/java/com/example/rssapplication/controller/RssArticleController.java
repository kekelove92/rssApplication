package com.example.rssapplication.controller;

import android.content.Context;
import android.content.Intent;

import com.example.rssapplication.database.DatabaseHelper;
import com.example.rssapplication.model.RssArticle;

import java.util.List;

public class RssArticleController {

    private DatabaseHelper dbHelper;
    private Context context;

    public RssArticleController(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    public long addArticle(RssArticle article) {
        return dbHelper.addArticle(article);
    }

    public void addArticles(List<RssArticle> articles) {
        for (RssArticle article : articles) {
            dbHelper.addArticle(article);
        }
    }

    public List<RssArticle> getAllArticles() {
        return dbHelper.getAllArticles();
    }

    public List<RssArticle> getArticlesByCategory(String category) {
        return dbHelper.getArticlesByCategory(category);
    }

    public List<RssArticle> getSavedArticles() {
        return dbHelper.getSavedArticles();
    }

    public boolean toggleSaveArticle(int articleId, boolean isSaved) {
        return dbHelper.updateArticleSavedStatus(articleId, isSaved) > 0;
    }

    public boolean deleteArticle(int articleId) {
        return dbHelper.deleteArticle(articleId) > 0;
    }

    public void clearAllArticles() {
        dbHelper.clearAllArticles();
    }

    public void shareArticle(RssArticle article) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, 
                article.getTitle() + "\n\n" + article.getLink());
        
        Intent chooser = Intent.createChooser(shareIntent, "Chia sẻ bài viết qua");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooser);
    }
}

