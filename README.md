# 🌈 英文單字樂園（Primary English）

互動式國小英文單字學習系統，專為國小學童設計。透過**單字卡片**、**語音朗讀**、**聽力測驗**、**拼字測驗**與**個人學習進度追蹤**，讓孩子輕鬆快樂地學英文！

全部 **2,142 個單字**整理自 [Prepedu 國小英文單字](https://prepedu.com/zh-hant/blog/elementary-english-vocabulary)（主頁 200 個 + 內頁延伸 1,039 個）及[景新國小單字王](https://www.jsps.ntpc.edu.tw/p/406-1000-7356,r35.php)（3~6 年級 903 個新單字），涵蓋 24 個生活主題與年級分類。

---

## ✨ 功能特色

### 📚 單字學習
- 依 **20 個主題分類**瀏覽單字卡片（動物、食物、顏色、學校、家庭…）
- 每個單字包含：英文、中文翻譯、KK 音標、英文例句、中文翻譯
- 分類卡片附有主題圖片，視覺豐富
- 聲音播放：點擊 🔊 喇叭按鈕，用瀏覽器 Web Speech API 朗讀
- **🆕 TTS 語音設定面板**：
  - 語速調整（0.3x ~ 2.0x）
  - 音調調整（0.5 ~ 2.0）
  - 語音偏好（不限 / 女聲 / 男聲）
  - 設定自動儲存於瀏覽器 localStorage

### 🎮 趣味測驗
| 測驗 | 玩法 |
|------|------|
| 🎧 **聽力測驗** | 聽到單字語音後，選出正確的中文意思 |
| ✏️ **拼字測驗** | 聽到單字語音後，輸入正確的英文拼字 |
| 📖 **閱讀理解** | 🆕 閱讀短文後回答 5 題選擇題 |
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

### 🛠 管理後台（🆕 登入者專用）
- **單字管理**：新增、編輯、刪除、搜尋、主題篩選單字
- **文章管理**：新增、編輯、刪除閱讀理解文章，可同時編輯 5 題選擇題
- **前台即時生效**：管理後台修改後，前台單字庫與閱讀測驗立即更新
- 僅限**已登入使用者**使用，未登入會自動導向登入頁

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

### 4. 進入測驗
- 在單字學習頁點「聽力測驗」或「拼字測驗」
- 可按**主題**（數字、動物...）或**年級**（3~6年級）篩選題目
- 聽力測驗：播放英文 → 選中文
- 拼字測驗：聽中文 → 拼英文
- 不選分類則從全部 2,142 個單字隨機出題（最多 20 題）

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
│   │   ├── Vocabulary.java                # 單字
│   │   ├── Article.java                   # 🆕 閱讀文章
│   │   ├── ReadingQuestion.java           # 🆕 閱讀題目
│   │   ├── User.java                      # 🆕 使用者
│   │   ├── UserVocabProgress.java         # 🆕 單字學習進度
│   │   └── QuizResult.java                # 🆕 測驗成績
│   ├── repository/
│   ├── service/
│   │   ├── UserService.java             # 🆕 BCrypt 密碼處理
│   │   ├── ProgressService.java         # 🆕 學習進度
│   │   └── QuizResultService.java       # 🆕 成績統計
│   └── controller/
│       ├── HomeController.java
│       ├── VocabularyController.java
│       ├── QuizController.java
│       ├── ReadingController.java         # 🆕 閱讀測驗
│       ├── AdminController.java           # 🆕 管理後台
│       └── UserController.java            # 🆕 登入/註冊/個人頁面
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
│   │   ├── quiz-listen.html             # 聽力測驗
│   │   ├── quiz-spell.html              # 拼字測驗
│   │   ├── reading-list.html              # 🆕 閱讀列表
│   │   ├── reading-quiz.html              # 🆕 閱讀答題
│   │   ├── reading-result.html            # 🆕 閱讀結果
│   │   ├── login.html                     # 🆕 登入
│   │   ├── register.html                  # 🆕 註冊
│   │   ├── profile.html                   # 🆕 學習儀表板
│   │   └── admin/
│   │       ├── vocab-list.html            # 🆕 單字管理列表
│   │       ├── vocab-form.html            # 🆕 單字新增/編輯
│   │       ├── article-list.html          # 🆕 文章管理列表
│   │       └── article-form.html          # 🆕 文章新增/編輯
```

---

## 🗄 資料庫說明

- **SQLite**（非 H2），檔案位置：`/tmp/primaryenglish.db`
- `spring.jpa.hibernate.ddl-auto=update`：自動建表
- `data.sql` 在首次啟動時載入 20 個分類 + 200 個單字
- **使用者資料**（帳號/密碼雜湊/暱稱）自動保留在 SQLite 中

---

## 🗄 預設單字資料（Prepedu 國小英文單字 + 景新國小單字王）

### 主題分類（Prepedu）

| # | 主題 | 單字數 |
|---|------|--------|
| 1 | 🔢 數字 | 10 |
| 2 | 👤 人物 | 10 |
| 3 | 🧍 身體 | 105 |
| 4 | 👕 服飾 | 110 |
| 5 | 🐾 動物 | 205 |
| 6 | 🏠 家庭用品 | 13 |
| 7 | 🍎 食物飲品 | 76 |
| 8 | 📍 地方 | 76 |
| 9 | ⏰ 時間日期 | 10 |
| 10 | 🏃 動詞 | 90 |
| 11 | ✨ 形容詞 | 10 |
| 12 | ⬆️ 介係詞 | 10 |
| 13 | 🌤️ 自然天氣 | 11 |
| 14 | 🎒 學校學習 | 133 |
| 15 | ⚽ 運動嗜好 | 3 |
| 16 | 💼 職業 | 228 |
| 17 | 📏 尺寸數量 | 10 |
| 18 | 🎂 節慶 | 10 |
| 19 | 😊 情感思想 | 68 |
| 20 | 👨‍👩‍👧‍👦 家庭 | 81 |

### 年級分類（景新國小單字王）

| # | 主題 | 單字數 |
|---|------|--------|
| 21 | 🎒 國小3年級 | 139 |
| 22 | 📚 國小4年級 | 137 |
| 23 | ✏️ 國小5年級 | 315 |
| 24 | 🎓 國小6年級 | 312 |

**總計 2,142 個單字**：Prepedu 1,239 個 + 景新國小 903 個新單字。

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
