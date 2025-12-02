package com.example.rssapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Arrays;

import com.example.rssapplication.adapter.RssArticleAdapter;
import com.example.rssapplication.controller.RssArticleController;
import com.example.rssapplication.controller.RssFeedController;
import com.example.rssapplication.controller.RssParserController;
import com.example.rssapplication.model.RssArticle;
import com.example.rssapplication.model.RssFeed;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RssArticleAdapter.OnArticleClickListener {

    private static final String PREFS_NAME = "RssAppPrefs";
    private static final String PREF_FIRST_LAUNCH = "first_launch";

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAddFeed;

    private RssArticleAdapter adapter;
    private RssFeedController feedController;
    private RssArticleController articleController;
    private MenuItem savedMenuItem;

    private List<String> categories;
    private String currentCategory = "Tất cả";
    private boolean isShowingSavedOnly = false;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        initControllers();
        setupToolbar();
        setupRecyclerView();
        setupTabLayout();
        setupSwipeRefresh();
        setupFab();

        loadArticles();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        fabAddFeed = findViewById(R.id.fab_add_feed);
    }

    private void initControllers() {
        feedController = new RssFeedController(this);
        articleController = new RssArticleController(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        adapter = new RssArticleAdapter(this, new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.removeAllTabs();
        
        categories = Arrays.asList(Constants.CATEGORIES);
        
        for (String category : categories) {
            tabLayout.addTab(tabLayout.newTab().setText(category));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentCategory = tab.getText().toString();
                loadArticles();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshFeeds);
    }

    private void setupFab() {
        fabAddFeed.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddFeedActivity.class);
            startActivity(intent);
        });
    }

    private void loadArticles() {
        if (isShowingSavedOnly) {
            isShowingSavedOnly = false;
            updateSavedMenuIcon();
        }
        
        List<RssArticle> articles;
        if (currentCategory.equals("Tất cả")) {
            articles = articleController.getAllArticles();
        } else {
            articles = articleController.getArticlesByCategory(currentCategory);
        }
        adapter.updateArticles(articles);
        
        // Auto-refresh on first launch
        if (isFirstLaunch() && articles.isEmpty()) {
            refreshFeeds();
            setFirstLaunchComplete();
        }
    }

    private void refreshFeeds() {
        swipeRefreshLayout.setRefreshing(true);
        
        List<RssFeed> feeds = feedController.getAllFeeds();
        
        if (feeds.isEmpty()) {
            mainHandler.post(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Chưa có nguồn RSS nào. Vui lòng thêm nguồn RSS!", 
                        Toast.LENGTH_LONG).show();
            });
            return;
        }

        final int[] completedFeeds = {0};
        final int totalFeeds = feeds.size();

        for (RssFeed feed : feeds) {
            RssParserController.parseRssFeed(feed.getUrl(), feed.getId(), feed.getCategory(), 
                new RssParserController.RssParserListener() {
                    @Override
                    public void onSuccess(List<RssArticle> articles) {
                        articleController.addArticles(articles);
                        
                        completedFeeds[0]++;
                        if (completedFeeds[0] == totalFeeds) {
                            mainHandler.post(() -> {
                                swipeRefreshLayout.setRefreshing(false);
                                loadArticles();
                                Toast.makeText(MainActivity.this, 
                                        "Đã tải xong tin tức!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        completedFeeds[0]++;
                        mainHandler.post(() -> {
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        });
                        
                        if (completedFeeds[0] == totalFeeds) {
                            mainHandler.post(() -> {
                                swipeRefreshLayout.setRefreshing(false);
                                loadArticles();
                            });
                        }
                    }
                });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload categories in case new feeds were added
        setupTabLayout();
        loadArticles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        savedMenuItem = menu.findItem(R.id.action_saved);
        updateSavedMenuIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_refresh) {
            refreshFeeds();
            return true;
        } else if (id == R.id.action_saved) {
            showSavedArticles();
            return true;
        } else if (id == R.id.action_clear) {
            clearAllArticles();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void updateSavedMenuIcon() {
        if (savedMenuItem != null) {
            if (isShowingSavedOnly) {
                savedMenuItem.setIcon(android.R.drawable.btn_star_big_on);
            } else {
                savedMenuItem.setIcon(android.R.drawable.btn_star_big_off);
            }
        }
    }

    private void showSavedArticles() {
        isShowingSavedOnly = !isShowingSavedOnly;
        
        if (isShowingSavedOnly) {
            List<RssArticle> savedArticles = articleController.getSavedArticles();
            adapter.updateArticles(savedArticles);
            Toast.makeText(this, "Hiển thị bài viết đã lưu", Toast.LENGTH_SHORT).show();
        } else {
            loadArticles();
            Toast.makeText(this, "Hiển thị tất cả bài viết", Toast.LENGTH_SHORT).show();
        }
        
        updateSavedMenuIcon();
    }

    private void clearAllArticles() {
        articleController.clearAllArticles();
        adapter.updateArticles(new ArrayList<>());
        Toast.makeText(this, "Đã xóa tất cả bài viết", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleClick(RssArticle article) {
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        intent.putExtra("article_id", article.getId());
        intent.putExtra("article_title", article.getTitle());
        intent.putExtra("article_link", article.getLink());
        intent.putExtra("article_description", article.getDescription());
        intent.putExtra("article_image_url", article.getImageUrl());
        intent.putExtra("article_is_saved", article.isSaved());
        startActivity(intent);
    }

    @Override
    public void onSaveClick(RssArticle article) {
        boolean newSavedStatus = !article.isSaved();
        articleController.toggleSaveArticle(article.getId(), newSavedStatus);
        article.setSaved(newSavedStatus);
        adapter.notifyDataSetChanged();
        
        String message = newSavedStatus ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClick(RssArticle article) {
        articleController.shareArticle(article);
    }

    private boolean isFirstLaunch() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(PREF_FIRST_LAUNCH, true);
    }

    private void setFirstLaunchComplete() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(PREF_FIRST_LAUNCH, false).apply();
    }
}
