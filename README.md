# 🌈 英文單字樂園（Primary English）

互動式國小英文單字學習系統，專為國小學童設計。透過**單字卡片**、**語音朗讀**、**聽力測驗**、**拼字測驗**與**個人學習進度追蹤**，讓孩子輕鬆快樂地學英文！

全部 200 個單字整理自 [Prepedu 國小英文單字](https://prepedu.com/zh-hant/blog/elementary-english-vocabulary)，涵蓋 20 個生活主題。

---

## ✨ 功能特色

### 📚 單字學習
- 依 **20 個主題分類**瀏覽單字卡片（動物、食物、顏色、學校、家庭…）
- 每個單字包含：英文、中文翻譯、KK 音標、英文例句、中文翻譯
- 分類卡片附有主題圖片，視覺豐富
- 聲音播放：點擊 🔊 喇叭按鈕，用瀏覽器 Web Speech API 朗讀

### 🎮 趣味測驗
| 測驗 | 玩法 |
|------|------|
| 🎧 **聽力測驗** | 聽到單字語音後，選出正確的中文意思 |
| ✏️ **拼字測驗** | 聽到單字語音後，輸入正確的英文拼字 |
| 🏆 **即時回饋** | 答對/答錯有動畫與音效回饋 |
| 📊 **錯誤檢討** | 測驗結束顯示答錯單字，方便複習 |

### 👤 使用者帳號（新增）
- **註冊帳號**：輸入使用者名稱、密碼（最少 4 碼）、暱稱
- **登入**：密碼使用 BCrypt 加密儲存，不存明文
- **Session 記憶**：登入後自動記住，除非主動登出
- **個人學習儀表板**：
  - 📚 已學習單字數統計
  - 🎯 完成測驗次數
  - ⭐ 平均成績（綠/黃/紅 三色標記）
  - 📋 最近測驗紀錄列表

### 🎨 活潑畫面設計
- Bootstrap 5 + Tabler Icons（不使用 inline onclick）
- 紫→粉漸層導航列，圓角卡片 + 陰影
- 懸停放大動畫、閃爍星星、顏色漸層標籤
- 響應式設計，手機/平板/桌機都能使用

---

## 🛠 技術架構

| 層級 | 技術 |
|------|------|
| 後端 | Spring Boot 3.4 + Java 17 |
| 資料庫 | **SQLite**（檔案型，持久化儲存） |
| ORM | Spring Data JPA + Hibernate SQLite Dialect |
| 安全性 | Spring Security（BCrypt 密碼加密） |
| 前端 | Thymeleaf + Bootstrap 5 + Tabler Icons |
| 語音 | Web Speech API（瀏覽器原生） |

---

## 🚀 快速啟動

### 1. 建置專案
```bash
mvn clean package -DskipTests
```

### 2. 啟動應用
```bash
java -jar target/primary-english-1.0.0.jar
```

### 3. 開啟瀏覽器
```
http://localhost:8080/
```

### 4. 開始使用
- 點「註冊」建立帳號（密碼至少 4 碼）
- 登入後可看到自己的學習紀錄

> **注意**：第一次啟動會自動建立 SQLite 資料庫檔案於 `/tmp/primaryenglish.db`，分類與單字資料會由 `data.sql` 載入。

---

## 📂 專案結構

```
primary-english/
├── pom.xml
├── src/main/java/com/primaryenglish/
│   ├── PrimaryEnglishApplication.java
│   ├── config/
│   │   └── SecurityConfig.java        # Spring Security 設定
│   ├── entity/
│   │   ├── Category.java                # 分類
│   │   ├── Vocabulary.java              # 單字
│   │   ├── User.java                    # 🆕 使用者
│   │   ├── UserVocabProgress.java       # 🆕 單字學習進度
│   │   └── QuizResult.java              # 🆕 測驗成績
│   ├── repository/
│   ├── service/
│   │   ├── UserService.java             # 🆕 BCrypt 密碼處理
│   │   ├── ProgressService.java         # 🆕 學習進度
│   │   └── QuizResultService.java       # 🆕 成績統計
│   └── controller/
│       ├── HomeController.java
│       ├── VocabularyController.java
│       ├── QuizController.java
│       └── UserController.java          # 🆕 登入/註冊/個人頁面
├── src/main/resources/
│   ├── application.properties           # SQLite 設定
│   ├── data.sql                        # 20 分類 + 200 單字
│   ├── static/
│   │   ├── css/
│   │   │   ├── custom.css             # 活潑樣式
│   │   │   ├── bootstrap.min.css      # 本機 Bootstrap
│   │   │   └── tabler-icons.min.css   # 本機 Tabler Icons
│   │   ├── js/
│   │   │   ├── speech.js              # Web Speech API
│   │   │   └── main.js                # 互動腳本
│   │   └── images/                    # 分類圖片
│   └── templates/
│       ├── fragments/
│       │   └── layout.html            # 共用佈局（有導航）
│       ├── index.html                   # 首頁/分類總覽
│       ├── vocabulary.html              # 單字卡片
│       ├── quiz-listen.html             # 聽力測驗
│       ├── quiz-spell.html              # 拼字測驗
│       ├── login.html                   # 🆕 登入
│       ├── register.html                # 🆕 註冊
│       └── profile.html                 # 🆕 學習儀表板
```

---

## 🗄 資料庫說明

- **SQLite**（非 H2），檔案位置：`/tmp/primaryenglish.db`
- `spring.jpa.hibernate.ddl-auto=update`：自動建表
- `data.sql` 在首次啟動時載入 20 個分類 + 200 個單字
- **使用者資料**（帳號/密碼雜湊/暱稱）自動保留在 SQLite 中

---

## 🗄 預設單字資料（Prepedu 國小英文單字）

| # | 主題 | 單字數 |
|---|------|--------|
| 1 | 🔢 數字 | 10 |
| 2 | 👤 人物 | 10 |
| 3 | 🧍 身體 | 10 |
| 4 | 👕 服飾 | 10 |
| 5 | 🐾 動物 | 10 |
| 6 | 🏠 家庭用品 | 10 |
| 7 | 🍎 食物飲品 | 10 |
| 8 | 📍 地方 | 10 |
| 9 | ⏰ 時間日期 | 10 |
| 10 | 🏃 動詞 | 10 |
| 11 | ✨ 形容詞 | 10 |
| 12 | ⬆️ 介係詞 | 10 |
| 13 | 🌤️ 自然天氣 | 10 |
| 14 | 🎒 學校學習 | 10 |
| 15 | ⚽ 運動嗜好 | 10 |
| 16 | 💼 職業 | 10 |
| 17 | 📏 尺寸數量 | 10 |
| 18 | 🎂 節慶 | 10 |
| 19 | 😊 情感思想 | 10 |
| 20 | 👨‍👩‍👧‍👦 家庭 | 10 |

---

## 🔊 語音播放說明

- 使用瀏覽器內建的 **Web Speech API**，無需 API Key
- 支援 Chrome、Edge、Safari、Firefox
- 語速已調為 **0.85 倍速**，適合國小學童

---

## 🧪 API 端點

| 方法 | 路徑 | 說明 |
|------|------|------|
| GET | `/api/vocabularies` | 所有單字（JSON） |
| GET | `/api/vocabularies?category={id}` | 依分類查詢 |
| GET | `/api/vocabularies/{id}` | 單字詳情 |
| POST | `/api/quiz/result` | 提交測驗結果 |

---

## ⚠️ 安全性注意事項

1. **生產環境請修改密碼規則**
   - 目前密碼最少 4 碼（方便兒童記憶）
   - 上線建議改為最少 6-8 碼

2. **SQLite 檔案權限**
   - 資料庫檔案位於本機 `/tmp/primaryenglish.db`
   - 部署時請確認該路徑可寫入，或修改 `application.properties` 中的 `spring.datasource.url`

3. **Spring Security 開發模式**
   - 開發時 Spring Boot 可能產生臨時密碼於 console log
   - 這不影響應用程式內建的自訂登入系統，但上線前建議檢視 SecurityConfig

4. **無 HTTPS**
   - 本地開發使用 HTTP，上線請啟用 HTTPS 保護密碼傳輸

---

## 📝 開發注意事項

- **無需安裝資料庫**：SQLite 為檔案型，自動建立
- **無需 Node/npm**：前端使用 Thymeleaf + 靜態 CSS，無需建置
- **圖片資源**：分類圖片位於 `static/images/`，部分分類有圖片，部分用 Tabler Icons
- **熱部署**：`spring-boot-devtools` 已加入，開發時自動重新載入

---

## 📄 授權

本專案單字內容整理自 [Prepedu 國小英文單字](https://prepedu.com/zh-hant/blog/elementary-english-vocabulary)，僅供教育用途使用。

---

Made for Kids 🌟 · 國小英文單字互動學習系統
