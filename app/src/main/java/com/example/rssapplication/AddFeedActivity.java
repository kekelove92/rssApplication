package com.example.rssapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rssapplication.controller.RssFeedController;
import com.example.rssapplication.controller.RssParserController;
import com.example.rssapplication.model.RssArticle;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class AddFeedActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText inputFeedName;
    private TextInputEditText inputFeedUrl;
    private Spinner spinnerCategory;
    private Button btnAddFeed;

    private RssFeedController feedController;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupToolbar();
        setupCategorySpinner();
        setupAddButton();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        inputFeedName = findViewById(R.id.input_feed_name);
        inputFeedUrl = findViewById(R.id.input_feed_url);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnAddFeed = findViewById(R.id.btn_add_feed);

        feedController = new RssFeedController(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Constants.CATEGORIES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupAddButton() {
        btnAddFeed.setOnClickListener(v -> validateAndAddFeed());
    }

    private void validateAndAddFeed() {
        String name = inputFeedName.getText() != null ? 
                inputFeedName.getText().toString().trim() : "";
        String url = inputFeedUrl.getText() != null ? 
                inputFeedUrl.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem().toString();

        // Validation
        if (name.isEmpty()) {
            inputFeedName.setError("Vui lòng nhập tên nguồn RSS");
            inputFeedName.requestFocus();
            return;
        }

        if (url.isEmpty()) {
            inputFeedUrl.setError("Vui lòng nhập URL RSS Feed");
            inputFeedUrl.requestFocus();
            return;
        }

        if (!feedController.isFeedUrlValid(url)) {
            inputFeedUrl.setError("URL không hợp lệ. Vui lòng nhập URL RSS Feed");
            inputFeedUrl.requestFocus();
            return;
        }

        // Disable button while processing
        btnAddFeed.setEnabled(false);
        btnAddFeed.setText("Đang kiểm tra...");

        // Verify RSS feed by trying to parse it
        RssParserController.parseRssFeed(url, 0, category, 
            new RssParserController.RssParserListener() {
                @Override
                public void onSuccess(List<RssArticle> articles) {
                    mainHandler.post(() -> {
                        // Add feed to database
                        long feedId = feedController.addFeed(name, url, category);
                        
                        if (feedId > 0) {
                            Toast.makeText(AddFeedActivity.this, 
                                    "Đã thêm nguồn RSS thành công! Tìm thấy " + 
                                    articles.size() + " bài viết.", 
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(AddFeedActivity.this, 
                                    "Lỗi: Nguồn RSS này đã tồn tại!", 
                                    Toast.LENGTH_SHORT).show();
                            btnAddFeed.setEnabled(true);
                            btnAddFeed.setText("Thêm nguồn RSS");
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    mainHandler.post(() -> {
                        Toast.makeText(AddFeedActivity.this, 
                                "Không thể tải RSS Feed: " + error, 
                                Toast.LENGTH_LONG).show();
                        btnAddFeed.setEnabled(true);
                        btnAddFeed.setText("Thêm nguồn RSS");
                    });
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

