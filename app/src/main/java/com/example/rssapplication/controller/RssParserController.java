package com.example.rssapplication.controller;

import android.util.Log;

import com.example.rssapplication.model.RssArticle;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RssParserController {

    private static final String TAG = "RssParserController";

    public interface RssParserListener {
        void onSuccess(List<RssArticle> articles);
        void onError(String error);
    }

    public static void parseRssFeed(String feedUrl, int feedId, String category, 
                                   RssParserListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(feedUrl);
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(url));

                List<RssArticle> articles = new ArrayList<>();

                for (SyndEntry entry : feed.getEntries()) {
                    RssArticle article = new RssArticle();
                    article.setFeedId(feedId);
                    article.setTitle(entry.getTitle());
                    article.setLink(entry.getLink());
                    article.setPubDate(entry.getPublishedDate());
                    article.setCategory(category);

                    // Parse description to get plain text and image
                    String description = "";
                    String imageUrl = null;

                    if (entry.getDescription() != null) {
                        String descriptionHtml = entry.getDescription().getValue();
                        
                        // Use Jsoup to parse HTML and extract image
                        Document doc = Jsoup.parse(descriptionHtml);
                        
                        // Extract image from <img> tag or <enclosure>
                        Element imgElement = doc.selectFirst("img");
                        if (imgElement != null) {
                            imageUrl = imgElement.attr("src");
                        }
                        
                        // Get plain text description
                        description = doc.text();
                        if (description.length() > 200) {
                            description = description.substring(0, 200) + "...";
                        }
                    }

                    // Try to get image from enclosures if not found in description
                    if (imageUrl == null && entry.getEnclosures() != null && 
                        !entry.getEnclosures().isEmpty()) {
                        imageUrl = entry.getEnclosures().get(0).getUrl();
                    }

                    article.setDescription(description);
                    article.setImageUrl(imageUrl);

                    articles.add(article);
                }

                Log.d(TAG, "Successfully parsed " + articles.size() + " articles from " + feedUrl);
                listener.onSuccess(articles);

            } catch (Exception e) {
                Log.e(TAG, "Error parsing RSS feed: " + feedUrl, e);
                listener.onError("Lỗi khi tải RSS: " + e.getMessage());
            }
        }).start();
    }

    public static String extractPlainText(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        Document doc = Jsoup.parse(html);
        return doc.text();
    }

    public static String extractImageUrl(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }
        Document doc = Jsoup.parse(html);
        Element imgElement = doc.selectFirst("img");
        if (imgElement != null) {
            return imgElement.attr("src");
        }
        return null;
    }
}

