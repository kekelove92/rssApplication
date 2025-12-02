# Hướng dẫn Build và Chạy Ứng dụng RSS Reader

## Yêu cầu hệ thống

### Phần mềm cần thiết
1. **Android Studio** (Arctic Fox trở lên)
   - Download: https://developer.android.com/studio
   
2. **Java Development Kit (JDK) 11+**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Hoặc sử dụng OpenJDK: https://adoptium.net/

3. **Android SDK**
   - Minimum SDK: 24 (Android 7.0)
   - Target SDK: 36
   - Compile SDK: 36

### Phần cứng đề xuất
- RAM: Tối thiểu 8GB (khuyến nghị 16GB)
- Ổ cứng trống: 10GB+
- CPU: 64-bit processor

## Các bước Build

### 1. Cài đặt môi trường

#### Bước 1.1: Cài JDK
```bash
# Kiểm tra JDK đã cài chưa
java -version

# Nếu chưa có, tải về và cài JDK 11+
# macOS với Homebrew:
brew install openjdk@11

# Set JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
```

#### Bước 1.2: Cài Android Studio
1. Tải Android Studio từ https://developer.android.com/studio
2. Cài đặt theo hướng dẫn
3. Mở Android Studio lần đầu và download SDK components

### 2. Mở Project

#### Bước 2.1: Clone hoặc mở project
```bash
# Nếu từ Git
git clone <repository-url>
cd rssapplication

# Hoặc mở trực tiếp thư mục project
```

#### Bước 2.2: Mở trong Android Studio
1. Mở Android Studio
2. File → Open
3. Chọn thư mục `rssapplication`
4. Click OK

### 3. Sync Dependencies

#### Bước 3.1: Gradle Sync
Android Studio sẽ tự động sync khi mở project. Nếu không:
1. File → Sync Project with Gradle Files
2. Hoặc click icon "Sync" trên toolbar
3. Đợi quá trình tải dependencies hoàn tất

#### Bước 3.2: Kiểm tra dependencies
Các thư viện sẽ được tải tự động:
- Rome 1.19.0 (RSS Parser)
- Jsoup 1.17.2 (HTML Parser)
- AndroidX libraries
- Material Components

### 4. Build Project

#### Bước 4.1: Clean Build
```bash
# Trong terminal
cd /path/to/rssapplication
./gradlew clean build

# Hoặc trong Android Studio
Build → Clean Project
Build → Rebuild Project
```

#### Bước 4.2: Kiểm tra lỗi
- Xem tab "Build" ở dưới cùng
- Nếu có lỗi, đọc log và fix
- Thường gặp:
  - Thiếu SDK → Cài trong SDK Manager
  - Lỗi dependencies → Sync lại Gradle
  - Lỗi Java version → Đảm bảo dùng JDK 11+

### 5. Chạy ứng dụng

#### Bước 5.1: Chuẩn bị Device/Emulator

**Option A: Sử dụng thiết bị thật**
1. Bật "Developer Options" trên Android device
2. Bật "USB Debugging"
3. Kết nối device với máy tính qua USB
4. Chấp nhận debug authorization

**Option B: Sử dụng Android Emulator**
1. Tools → Device Manager
2. Create Device → Chọn device model
3. Chọn System Image (Android 7.0+)
4. Finish → Launch emulator

#### Bước 5.2: Run App
1. Chọn device/emulator từ dropdown
2. Click nút Run (▶️) hoặc Shift+F10
3. Đợi app build và install
4. App sẽ tự động mở

### 6. Debug và Test

#### Debug Mode
```bash
# Run in debug mode
./gradlew installDebug

# Hoặc trong Android Studio
Run → Debug 'app' (Shift+F9)
```

#### Xem Logs
1. View → Tool Windows → Logcat
2. Filter theo tag: "RssParserController", "MainActivity", etc.

## Các lệnh Gradle hữu ích

```bash
# Clean project
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install APK to device
./gradlew installDebug

# Run tests
./gradlew test

# Check dependencies
./gradlew dependencies

# Lint check
./gradlew lint
```

## Cấu trúc Output

### Debug APK
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK
```
app/build/outputs/apk/release/app-release.apk
```

## Troubleshooting

### Lỗi: "Unable to locate Java Runtime"
```bash
# Cài JDK và set JAVA_HOME
export JAVA_HOME=/path/to/jdk
export PATH=$JAVA_HOME/bin:$PATH
```

### Lỗi: "SDK location not found"
Tạo file `local.properties`:
```
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
```

### Lỗi: "Failed to resolve dependencies"
```bash
# Xóa cache và rebuild
rm -rf ~/.gradle/caches/
./gradlew clean build --refresh-dependencies
```

### Lỗi: "Manifest merger failed"
Kiểm tra AndroidManifest.xml và đảm bảo không có conflict

### App crash khi chạy
1. Kiểm tra Logcat để xem stack trace
2. Đảm bảo có Internet permission
3. Test với RSS URL hợp lệ

## Build Release APK

### Bước 1: Tạo Keystore
```bash
keytool -genkey -v -keystore my-release-key.jks \
  -alias rss-app -keyalg RSA -keysize 2048 -validity 10000
```

### Bước 2: Config Signing
Thêm vào `app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "your-store-password"
            keyAlias = "rss-app"
            keyPassword = "your-key-password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### Bước 3: Build
```bash
./gradlew assembleRelease
```

APK sẽ ở: `app/build/outputs/apk/release/app-release.apk`

## Kiểm tra App

### Checklist trước khi release
- [ ] App chạy không crash
- [ ] Thêm RSS feed thành công
- [ ] Load và hiển thị tin tức
- [ ] Click vào bài viết xem chi tiết
- [ ] Lưu và bỏ lưu bài viết
- [ ] Chia sẻ bài viết
- [ ] Filter theo category
- [ ] Pull to refresh
- [ ] WebView load đúng nội dung
- [ ] Open in browser hoạt động

### Performance Testing
1. Test với nhiều RSS feeds
2. Test với kết nối Internet chậm
3. Test trên nhiều Android versions
4. Test trên nhiều screen sizes

## Tối ưu Build

### Giảm APK size
```kotlin
// Trong build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### Enable multidex nếu cần
```kotlin
android {
    defaultConfig {
        multiDexEnabled = true
    }
}
```

## Tài liệu tham khảo

- Android Developer Guide: https://developer.android.com/guide
- Gradle Build Tool: https://gradle.org/
- Rome RSS Library: https://rometools.github.io/rome/
- Jsoup Parser: https://jsoup.org/

## Hỗ trợ

Nếu gặp vấn đề:
1. Kiểm tra Logcat
2. Clean và rebuild project
3. Invalidate caches: File → Invalidate Caches / Restart
4. Kiểm tra Android Studio version
5. Kiểm tra JDK version

---

**Chúc bạn build thành công! 🎉**

