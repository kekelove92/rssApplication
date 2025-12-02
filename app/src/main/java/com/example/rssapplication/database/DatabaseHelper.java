package com.example.rssapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rssapplication.model.RssArticle;
import com.example.rssapplication.model.RssFeed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rss_reader.db";
    private static final int DATABASE_VERSION = 1;

    // Table RSS Feeds
    private static final String TABLE_FEEDS = "rss_feeds";
    private static final String FEED_ID = "id";
    private static final String FEED_NAME = "name";
    private static final String FEED_URL = "url";
    private static final String FEED_CATEGORY = "category";

    // Table RSS Articles
    private static final String TABLE_ARTICLES = "rss_articles";
    private static final String ARTICLE_ID = "id";
    private static final String ARTICLE_FEED_ID = "feed_id";
    private static final String ARTICLE_TITLE = "title";
    private static final String ARTICLE_DESCRIPTION = "description";
    private static final String ARTICLE_LINK = "link";
    private static final String ARTICLE_PUB_DATE = "pub_date";
    private static final String ARTICLE_IMAGE_URL = "image_url";
    private static final String ARTICLE_IS_SAVED = "is_saved";
    private static final String ARTICLE_CATEGORY = "category";

    private static final SimpleDateFormat dateFormat = 
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFeedsTable = "CREATE TABLE " + TABLE_FEEDS + " (" +
                FEED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FEED_NAME + " TEXT NOT NULL, " +
                FEED_URL + " TEXT NOT NULL UNIQUE, " +
                FEED_CATEGORY + " TEXT NOT NULL" +
                ")";

        String createArticlesTable = "CREATE TABLE " + TABLE_ARTICLES + " (" +
                ARTICLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ARTICLE_FEED_ID + " INTEGER NOT NULL, " +
                ARTICLE_TITLE + " TEXT NOT NULL, " +
                ARTICLE_DESCRIPTION + " TEXT, " +
                ARTICLE_LINK + " TEXT NOT NULL UNIQUE, " +
                ARTICLE_PUB_DATE + " TEXT, " +
                ARTICLE_IMAGE_URL + " TEXT, " +
                ARTICLE_IS_SAVED + " INTEGER DEFAULT 0, " +
                ARTICLE_CATEGORY + " TEXT, " +
                "FOREIGN KEY(" + ARTICLE_FEED_ID + ") REFERENCES " + 
                TABLE_FEEDS + "(" + FEED_ID + ") ON DELETE CASCADE" +
                ")";

        db.execSQL(createFeedsTable);
        db.execSQL(createArticlesTable);

        // Insert default RSS feeds
        insertDefaultFeeds(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDS);
        onCreate(db);
    }

    private void insertDefaultFeeds(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        
        values.put(FEED_NAME, "VnExpress - Tin nổi bật");
        values.put(FEED_URL, "https://vnexpress.net/rss/tin-noi-bat.rss");
        values.put(FEED_CATEGORY, "Tin nổi bật");
        db.insert(TABLE_FEEDS, null, values);

        values.clear();
        values.put(FEED_NAME, "VnExpress - Thế giới");
        values.put(FEED_URL, "https://vnexpress.net/rss/the-gioi.rss");
        values.put(FEED_CATEGORY, "Thế giới");
        db.insert(TABLE_FEEDS, null, values);

        values.clear();
        values.put(FEED_NAME, "VnExpress - Thể thao");
        values.put(FEED_URL, "https://vnexpress.net/rss/the-thao.rss");
        values.put(FEED_CATEGORY, "Thể thao");
        db.insert(TABLE_FEEDS, null, values);
    }

    // ==================== RSS FEEDS METHODS ====================

    public long addFeed(RssFeed feed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FEED_NAME, feed.getName());
        values.put(FEED_URL, feed.getUrl());
        values.put(FEED_CATEGORY, feed.getCategory());
        
        long id = db.insert(TABLE_FEEDS, null, values);
        return id;
    }

    public RssFeed getFeed(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FEEDS, null, FEED_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        RssFeed feed = null;
        if (cursor != null && cursor.moveToFirst()) {
            feed = new RssFeed(
                    cursor.getInt(cursor.getColumnIndexOrThrow(FEED_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FEED_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FEED_URL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FEED_CATEGORY))
            );
            cursor.close();
        }
        return feed;
    }

    public List<RssFeed> getAllFeeds() {
        List<RssFeed> feeds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FEEDS, null, null, null, null, null, FEED_NAME);

        if (cursor.moveToFirst()) {
            do {
                RssFeed feed = new RssFeed(
                        cursor.getInt(cursor.getColumnIndexOrThrow(FEED_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FEED_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FEED_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FEED_CATEGORY))
                );
                feeds.add(feed);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return feeds;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả"); // Default category
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_FEEDS, new String[]{FEED_CATEGORY}, 
                null, null, null, null, FEED_CATEGORY, null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(FEED_CATEGORY));
                if (!category.equals("Tất cả") && !categories.contains(category)) {
                    categories.add(category);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public int deleteFeed(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FEEDS, FEED_ID + "=?", new String[]{String.valueOf(id)});
        return result;
    }

    // ==================== RSS ARTICLES METHODS ====================

    public long addArticle(RssArticle article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ARTICLE_FEED_ID, article.getFeedId());
        values.put(ARTICLE_TITLE, article.getTitle());
        values.put(ARTICLE_DESCRIPTION, article.getDescription());
        values.put(ARTICLE_LINK, article.getLink());
        values.put(ARTICLE_PUB_DATE, article.getPubDate() != null ? 
                dateFormat.format(article.getPubDate()) : null);
        values.put(ARTICLE_IMAGE_URL, article.getImageUrl());
        values.put(ARTICLE_IS_SAVED, article.isSaved() ? 1 : 0);
        values.put(ARTICLE_CATEGORY, article.getCategory());

        long id = db.insertWithOnConflict(TABLE_ARTICLES, null, values, 
                SQLiteDatabase.CONFLICT_IGNORE);
        return id;
    }

    public List<RssArticle> getAllArticles() {
        return getArticlesByCategory(null);
    }

    public List<RssArticle> getArticlesByCategory(String category) {
        List<RssArticle> articles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = null;
        String[] selectionArgs = null;
        
        if (category != null && !category.equals("Tất cả")) {
            selection = ARTICLE_CATEGORY + "=?";
            selectionArgs = new String[]{category};
        }

        Cursor cursor = db.query(TABLE_ARTICLES, null, selection, selectionArgs, 
                null, null, ARTICLE_PUB_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                RssArticle article = new RssArticle();
                article.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ARTICLE_ID)));
                article.setFeedId(cursor.getInt(cursor.getColumnIndexOrThrow(ARTICLE_FEED_ID)));
                article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_TITLE)));
                article.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_DESCRIPTION)));
                article.setLink(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_LINK)));
                
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_PUB_DATE));
                if (dateStr != null) {
                    try {
                        article.setPubDate(dateFormat.parse(dateStr));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                
                article.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_IMAGE_URL)));
                article.setSaved(cursor.getInt(cursor.getColumnIndexOrThrow(ARTICLE_IS_SAVED)) == 1);
                article.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_CATEGORY)));
                
                articles.add(article);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return articles;
    }

    public List<RssArticle> getSavedArticles() {
        List<RssArticle> articles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_ARTICLES, null, ARTICLE_IS_SAVED + "=?", 
                new String[]{"1"}, null, null, ARTICLE_PUB_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                RssArticle article = new RssArticle();
                article.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ARTICLE_ID)));
                article.setFeedId(cursor.getInt(cursor.getColumnIndexOrThrow(ARTICLE_FEED_ID)));
                article.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_TITLE)));
                article.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_DESCRIPTION)));
                article.setLink(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_LINK)));
                
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_PUB_DATE));
                if (dateStr != null) {
                    try {
                        article.setPubDate(dateFormat.parse(dateStr));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                
                article.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_IMAGE_URL)));
                article.setSaved(true);
                article.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(ARTICLE_CATEGORY)));
                
                articles.add(article);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return articles;
    }

    public int updateArticleSavedStatus(int id, boolean isSaved) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ARTICLE_IS_SAVED, isSaved ? 1 : 0);
        
        int result = db.update(TABLE_ARTICLES, values, ARTICLE_ID + "=?", 
                new String[]{String.valueOf(id)});
        return result;
    }

    public int deleteArticle(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ARTICLES, ARTICLE_ID + "=?", 
                new String[]{String.valueOf(id)});
        return result;
    }

    public void clearAllArticles() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARTICLES, null, null);
    }
    
    public void closeDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}

