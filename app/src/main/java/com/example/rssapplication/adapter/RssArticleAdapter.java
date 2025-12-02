package com.example.rssapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rssapplication.R;
import com.example.rssapplication.model.RssArticle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RssArticleAdapter extends RecyclerView.Adapter<RssArticleAdapter.ArticleViewHolder> {

    private Context context;
    private List<RssArticle> articles;
    private OnArticleClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnArticleClickListener {
        void onArticleClick(RssArticle article);
        void onSaveClick(RssArticle article);
        void onShareClick(RssArticle article);
    }

    public RssArticleAdapter(Context context, List<RssArticle> articles, OnArticleClickListener listener) {
        this.context = context;
        this.articles = articles != null ? articles : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rss_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        RssArticle article = articles.get(position);
        
        holder.titleTextView.setText(article.getTitle());
        holder.descriptionTextView.setText(article.getDescription());
        
        if (article.getPubDate() != null) {
            holder.dateTextView.setText(dateFormat.format(article.getPubDate()));
        } else {
            holder.dateTextView.setText("");
        }

        // Set save icon based on saved status
        if (article.isSaved()) {
            holder.saveIcon.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.saveIcon.setImageResource(android.R.drawable.btn_star_big_off);
        }

        // Load image using Glide
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            holder.thumbnailImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(holder.thumbnailImageView);
        } else {
            holder.thumbnailImageView.setVisibility(View.VISIBLE);
            holder.thumbnailImageView.setImageResource(R.drawable.placeholder_image);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onArticleClick(article);
            }
        });

        holder.saveIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaveClick(article);
            }
        });

        holder.shareIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShareClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void updateArticles(List<RssArticle> newArticles) {
        this.articles = newArticles != null ? newArticles : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addArticles(List<RssArticle> newArticles) {
        if (newArticles != null && !newArticles.isEmpty()) {
            this.articles.addAll(newArticles);
            notifyDataSetChanged();
        }
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTextView;
        ImageView saveIcon;
        ImageView shareIcon;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.article_thumbnail);
            titleTextView = itemView.findViewById(R.id.article_title);
            descriptionTextView = itemView.findViewById(R.id.article_description);
            dateTextView = itemView.findViewById(R.id.article_date);
            saveIcon = itemView.findViewById(R.id.article_save_icon);
            shareIcon = itemView.findViewById(R.id.article_share_icon);
        }
    }
}

