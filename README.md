# RSS Reader Application

Ứng dụng đọc báo RSS được xây dựng bằng Java cho Android với kiến trúc MVC.

## Tính năng

### ✨ Chức năng chính
- ✅ Thêm nguồn RSS Feed mới
- ✅ Hiển thị danh sách tin tức theo chuyên mục
- ✅ Xem chi tiết bài báo với WebView
- ✅ Lưu bài báo yêu thích
- ✅ Chia sẻ bài báo qua các ứng dụng khác
- ✅ Pull-to-refresh để cập nhật tin mới
- ✅ TabLayout để lọc theo chuyên mục

### 📚 Nguồn RSS mặc định
- VnExpress - Tin mới nhất
- VnExpress - Thế giới
- VnExpress - Thể thao
- VnExpress - Kinh doanh

## Công nghệ sử dụng

### Ngôn ngữ & Framework
- **Java** - Ngôn ngữ lập trình
- **Android SDK** - minSdk 24, targetSdk 34
- **Kiến trúc MVC** - Model-View-Controller pattern

### Thư viện
- **Rome 1.19.0** - RSS Parser
- **Jsoup 1.17.2** - HTML Parser
- **AndroidX RecyclerView** - Hiển thị danh sách
- **Material Components** - UI Components
- **SwipeRefreshLayout** - Pull to refresh
- **CardView** - Card UI

### Database
- **SQLite** - Local database
- 2 bảng chính:
  - `rss_feeds` - Lưu nguồn RSS
  - `rss_articles` - Lưu bài báo

## Cấu trúc Project

```
app/src/main/java/com/example/rssapplication/
├── model/
│   ├── RssFeed.java          # Model cho RSS Feed
│   └── RssArticle.java       # Model cho bài báo
├── database/
│   └── DatabaseHelper.java   # SQLite database helper
├── controller/
│   ├── RssFeedController.java        # Controller quản lý feeds
│   ├── RssArticleController.java     # Controller quản lý articles
│   └── RssParserController.java      # Controller parse RSS
├── adapter/
│   └── RssArticleAdapter.java        # RecyclerView adapter
├── MainActivity.java                 # Màn hình chính
├── AddFeedActivity.java             # Màn hình thêm RSS
└── ArticleDetailActivity.java       # Màn hình chi tiết
```

## Cài đặt

### Yêu cầu
- Android Studio Arctic Fox trở lên
- JDK 11+
- Android SDK 24+
- Gradle 8.11.2

### Các bước cài đặt

1. **Clone project**
```bash
git clone <repository-url>
cd rssapplication
```

2. **Mở project trong Android Studio**
- File → Open → Chọn thư mục project

3. **Sync Gradle**
- Android Studio sẽ tự động sync dependencies
- Hoặc click: File → Sync Project with Gradle Files

4. **Build & Run**
- Click nút Run (▶️) hoặc Shift+F10
- Chọn device/emulator

## Sử dụng

### 1. Xem tin tức
- Mở app, danh sách tin sẽ tự động hiển thị
- Vuốt xuống để refresh tin mới
- Click vào tab để lọc theo chuyên mục

### 2. Thêm nguồn RSS
- Click nút FAB (+) ở góc phải màn hình
- Nhập tên nguồn, URL RSS, và chuyên mục
- Click "Thêm nguồn RSS"

### 3. Xem chi tiết bài báo
- Click vào bài báo trong danh sách
- Xem nội dung trong WebView
- Click "Đọc bài viết đầy đủ" để mở trình duyệt

### 4. Lưu & Chia sẻ
- Click icon ⭐ để lưu bài báo
- Click icon 🔗 để chia sẻ
- Xem bài đã lưu: Menu → Đã lưu

## API & Nguồn RSS

### Format RSS hỗ trợ
- RSS 2.0
- Atom Feed

### Nguồn RSS gợi ý
```
VnExpress:
- Tin mới: https://vnexpress.net/rss/tin-moi-nhat.rss
- Thế giới: https://vnexpress.net/rss/the-gioi.rss
- Thể thao: https://vnexpress.net/rss/the-thao.rss
- Kinh doanh: https://vnexpress.net/rss/kinh-doanh.rss
```

## Kiến trúc MVC

### Model
- `RssFeed` - Đại diện cho nguồn RSS
- `RssArticle` - Đại diện cho bài báo

### View
- `MainActivity` - Hiển thị danh sách
- `AddFeedActivity` - Form thêm RSS
- `ArticleDetailActivity` - Chi tiết bài viết
- Layouts XML

### Controller
- `RssFeedController` - Business logic cho feeds
- `RssArticleController` - Business logic cho articles
- `RssParserController` - Parse RSS feeds

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

## Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Tính năng tương lai

- [ ] Hỗ trợ tải ảnh với Glide/Picasso
- [ ] Thêm chế độ Dark Mode
- [ ] Tìm kiếm bài viết
- [ ] Đọc offline
- [ ] Push notification cho tin mới
- [ ] Export/Import OPML

## Troubleshooting

### Lỗi không tải được RSS
- Kiểm tra kết nối Internet
- Đảm bảo URL RSS hợp lệ
- Thử refresh lại

### Lỗi build
- Clean project: Build → Clean Project
- Rebuild: Build → Rebuild Project
- Invalidate caches: File → Invalidate Caches / Restart

---

**Lưu ý:** Ứng dụng này được phát triển cho mục đích học tập và demo. Trong môi trường production, nên bổ sung thêm error handling, caching, và optimization.

