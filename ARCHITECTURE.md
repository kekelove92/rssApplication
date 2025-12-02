# Kiến trúc Ứng dụng RSS Reader

## Tổng quan

Ứng dụng được xây dựng theo mô hình **MVC (Model-View-Controller)** với các thành phần được tách biệt rõ ràng để dễ bảo trì và mở rộng.

```
┌─────────────────────────────────────────────┐
│              USER INTERFACE                  │
│  (Activities, Layouts, Adapters)            │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│             CONTROLLERS                      │
│  (Business Logic, RSS Parsing)              │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│         MODELS & DATABASE                    │
│  (Data Classes, SQLite)                     │
└─────────────────────────────────────────────┘
```

## Các thành phần chính

### 1. MODEL Layer

#### Models
Các class đại diện cho dữ liệu trong ứng dụng.

**RssFeed.java**
```java
- id: int
- name: String
- url: String
- category: String
```
Đại diện cho một nguồn RSS feed.

**RssArticle.java**
```java
- id: int
- feedId: int
- title: String
- description: String
- link: String
- pubDate: Date
- imageUrl: String
- isSaved: boolean
- category: String
```
Đại diện cho một bài báo từ RSS feed.

#### Database
**DatabaseHelper.java**

Quản lý SQLite database với 2 bảng chính:

1. **rss_feeds** - Lưu thông tin các nguồn RSS
2. **rss_articles** - Lưu các bài báo đã tải

**Các phương thức chính:**
- `addFeed()` - Thêm nguồn RSS mới
- `getAllFeeds()` - Lấy tất cả nguồn RSS
- `addArticle()` - Thêm bài báo
- `getAllArticles()` - Lấy tất cả bài báo
- `getArticlesByCategory()` - Lọc theo chuyên mục
- `updateArticleSavedStatus()` - Cập nhật trạng thái lưu

### 2. CONTROLLER Layer

Controllers xử lý business logic và điều phối giữa View và Model.

#### RssFeedController.java
```
Chức năng:
- Quản lý các nguồn RSS feeds
- Thêm/xóa/lấy feeds
- Validate URL RSS
- Lấy danh sách categories
```

**Phương thức chính:**
- `addFeed(name, url, category)` → long
- `getAllFeeds()` → List<RssFeed>
- `getAllCategories()` → List<String>
- `deleteFeed(id)` → boolean
- `isFeedUrlValid(url)` → boolean

#### RssArticleController.java
```
Chức năng:
- Quản lý các bài báo
- Lưu/xóa/lấy articles
- Share articles
- Toggle save status
```

**Phương thức chính:**
- `addArticle(article)` → long
- `getAllArticles()` → List<RssArticle>
- `getArticlesByCategory(category)` → List<RssArticle>
- `getSavedArticles()` → List<RssArticle>
- `toggleSaveArticle(id, isSaved)` → boolean
- `shareArticle(article)` → void

#### RssParserController.java
```
Chức năng:
- Parse RSS feeds từ URL
- Extract text và images từ HTML
- Chạy trên background thread
- Callback với kết quả
```

**Phương thức chính:**
- `parseRssFeed(url, feedId, category, listener)` → void (async)
- `extractPlainText(html)` → String
- `extractImageUrl(html)` → String

**Flow của RSS Parsing:**
```
URL → Rome Parser → SyndFeed → SyndEntry[]
                                    ↓
                              Jsoup Parser (HTML)
                                    ↓
                            RssArticle[] → Callback
```

### 3. VIEW Layer

#### Activities

**MainActivity.java**
```
Màn hình chính của app
━━━━━━━━━━━━━━━━━━━━━━
Chức năng:
- Hiển thị danh sách bài báo
- TabLayout để filter theo category
- SwipeRefresh để load tin mới
- FAB để thêm RSS feed
- Menu: Refresh, Saved, Clear

Components:
- Toolbar
- TabLayout
- RecyclerView (với RssArticleAdapter)
- SwipeRefreshLayout
- FloatingActionButton
```

**AddFeedActivity.java**
```
Màn hình thêm nguồn RSS
━━━━━━━━━━━━━━━━━━━━━━
Chức năng:
- Input: Tên nguồn
- Input: URL RSS Feed
- Spinner: Chọn category
- Validate và verify RSS URL
- Parse thử RSS trước khi lưu

Components:
- TextInputEditText (Name)
- TextInputEditText (URL)
- Spinner (Category)
- Button (Add Feed)
```

**ArticleDetailActivity.java**
```
Màn hình chi tiết bài báo
━━━━━━━━━━━━━━━━━━━━━━
Chức năng:
- Hiển thị ảnh, tiêu đề, mô tả
- WebView load full article
- Button mở browser
- FAB: Save/Unsave
- FAB: Share

Components:
- ImageView (Article image)
- TextView (Title, Description)
- WebView (Full content)
- Button (Open browser)
- FloatingActionButton (Save, Share)
```

#### Adapter

**RssArticleAdapter.java**
```
RecyclerView Adapter cho danh sách bài báo
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
ViewHolder chứa:
- ImageView: Thumbnail
- TextView: Title
- TextView: Description
- TextView: Date
- ImageView: Save icon
- ImageView: Share icon

Callbacks:
- onArticleClick(article)
- onSaveClick(article)
- onShareClick(article)
```

## Data Flow

### 1. Load RSS Feeds
```
User pulls to refresh
        ↓
MainActivity.refreshFeeds()
        ↓
Get all feeds from Database
        ↓
For each feed:
  RssParserController.parseRssFeed()
        ↓
  Rome parses XML → SyndFeed
        ↓
  Jsoup extracts HTML content
        ↓
  Create RssArticle objects
        ↓
  Callback to MainActivity
        ↓
RssArticleController.addArticles()
        ↓
Save to Database
        ↓
Reload articles from DB
        ↓
Update RecyclerView
```

### 2. Add New Feed
```
User clicks FAB (+)
        ↓
Open AddFeedActivity
        ↓
User inputs name, URL, category
        ↓
Click "Add Feed" button
        ↓
Validate inputs
        ↓
RssParserController.parseRssFeed() (verify)
        ↓
If successful:
  RssFeedController.addFeed()
        ↓
  Save to Database
        ↓
  Return to MainActivity
        ↓
  Refresh feeds automatically
```

### 3. View Article Detail
```
User clicks article in list
        ↓
Pass article data via Intent
        ↓
Open ArticleDetailActivity
        ↓
Display: image, title, description
        ↓
WebView loads article URL
        ↓
User can:
  - Save/Unsave (update DB)
  - Share (Intent.ACTION_SEND)
  - Open in browser (Intent.ACTION_VIEW)
```

### 4. Save/Unsave Article
```
User clicks star icon
        ↓
Toggle isSaved state
        ↓
RssArticleController.toggleSaveArticle()
        ↓
Update database
        ↓
Update UI (icon change)
        ↓
Show Toast confirmation
```

## Threading Model

### Main Thread (UI Thread)
- UI updates
- User interactions
- Database reads (quick queries)

### Background Threads
- RSS parsing (RssParserController)
- Network requests
- Heavy computations

```java
// Background task example in RssParserController
new Thread(() -> {
    // Parse RSS on background thread
    SyndFeed feed = input.build(new XmlReader(url));
    // ... parsing logic ...
    
    // Callback to main thread
    mainHandler.post(() -> {
        listener.onSuccess(articles);
    });
}).start();
```

## Database Schema

### Table: rss_feeds
```sql
CREATE TABLE rss_feeds (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    url TEXT NOT NULL UNIQUE,
    category TEXT NOT NULL
);
```

### Table: rss_articles
```sql
CREATE TABLE rss_articles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    feed_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    link TEXT NOT NULL UNIQUE,
    pub_date TEXT,
    image_url TEXT,
    is_saved INTEGER DEFAULT 0,
    category TEXT,
    FOREIGN KEY(feed_id) REFERENCES rss_feeds(id) ON DELETE CASCADE
);
```

### Indexes
```sql
-- Automatically created by UNIQUE constraint
CREATE UNIQUE INDEX idx_feeds_url ON rss_feeds(url);
CREATE UNIQUE INDEX idx_articles_link ON rss_articles(link);

-- For performance
CREATE INDEX idx_articles_category ON rss_articles(category);
CREATE INDEX idx_articles_saved ON rss_articles(is_saved);
```

## Design Patterns

### 1. MVC (Model-View-Controller)
- **Model**: RssFeed, RssArticle, DatabaseHelper
- **View**: Activities, Layouts, Adapter
- **Controller**: RssFeedController, RssArticleController, RssParserController

### 2. Singleton (Implicit)
- DatabaseHelper instance được tạo mỗi khi cần
- Có thể refactor thành Singleton pattern

### 3. Observer Pattern
- Callbacks trong RssParserController
- OnClickListener interfaces

### 4. Adapter Pattern
- RssArticleAdapter cho RecyclerView
- ArrayAdapter cho Spinner

## Dependencies

### Core Libraries
```gradle
// RSS Parsing
com.rometools:rome:1.19.0

// HTML Parsing
org.jsoup:jsoup:1.17.2

// UI Components
androidx.recyclerview:recyclerview:1.3.2
androidx.cardview:cardview:1.0.0
androidx.swiperefreshlayout:swiperefreshlayout:1.1.0
com.google.android.material:material:1.13.0
```

## Error Handling

### Network Errors
- Try-catch trong RssParserController
- Callback onError() với error message
- Toast notification cho user

### Database Errors
- CONFLICT_IGNORE cho duplicate entries
- Try-catch cho parse errors
- Validation trước khi insert

### UI Errors
- Null checks cho Views
- Safe navigation với optional chaining
- Default values và fallbacks

## Security Considerations

### Internet Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### WebView Security
```java
webView.getSettings().setJavaScriptEnabled(true);
webView.setWebViewClient(new WebViewClient());
```

### SQL Injection Prevention
- Sử dụng parameterized queries
- ContentValues cho inserts/updates
- Selection args cho where clauses

## Future Improvements

### Architecture
- [ ] Implement Repository pattern
- [ ] Add ViewModel với LiveData
- [ ] Migration sang MVVM/Clean Architecture
- [ ] Dependency Injection với Hilt/Dagger

### Features
- [ ] Image loading với Glide/Picasso
- [ ] Offline caching strategy
- [ ] Search functionality
- [ ] Export/Import OPML
- [ ] Background sync với WorkManager
- [ ] Push notifications

### Performance
- [ ] Pagination cho large datasets
- [ ] Image caching
- [ ] Database optimization
- [ ] Memory leak prevention

### Testing
- [ ] Unit tests cho Controllers
- [ ] Integration tests cho Database
- [ ] UI tests với Espresso
- [ ] Mock RSS feeds cho testing

---

Kiến trúc này cung cấp nền tảng vững chắc cho việc mở rộng và bảo trì ứng dụng trong tương lai.

