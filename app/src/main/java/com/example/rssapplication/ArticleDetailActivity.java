package com.example.rssapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rssapplication.controller.RssArticleController;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ArticleDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private WebView webView;
    private FloatingActionButton fabSave;
    private FloatingActionButton fabShare;

    private int articleId;
    private String articleLink;
    private String title;
    private boolean isSaved;

    private RssArticleController articleController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        articleController = new RssArticleController(this);

        initViews();
        setupToolbar();
        loadArticleData();
        setupWebView();
        setupButtons();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        webView = findViewById(R.id.web_view);
        fabSave = findViewById(R.id.fab_save);
        fabShare = findViewById(R.id.fab_share);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadArticleData() {
        Intent intent = getIntent();
        articleId = intent.getIntExtra("article_id", 0);
        title = intent.getStringExtra("article_title");
        articleLink = intent.getStringExtra("article_link");
        isSaved = intent.getBooleanExtra("article_is_saved", false);

        // Update save button icon
        updateSaveIcon();
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        
        // Load the article URL in WebView
        if (articleLink != null && !articleLink.isEmpty()) {
            webView.loadUrl(articleLink);
        }
    }

    private void setupButtons() {
        fabSave.setOnClickListener(v -> toggleSave());
        
        fabShare.setOnClickListener(v -> shareArticle());
    }

    private void toggleSave() {
        isSaved = !isSaved;
        articleController.toggleSaveArticle(articleId, isSaved);
        updateSaveIcon();
        
        String message = isSaved ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateSaveIcon() {
        if (isSaved) {
            fabSave.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            fabSave.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void shareArticle() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + articleLink);
        
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết qua"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

