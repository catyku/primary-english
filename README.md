# 🌈 英文單字樂園（Primary English）

互動式國小英文單字學習系統，專為國小學童設計。透過單字卡片、例句、語音朗讀與趣味測驗，讓孩子輕鬆快樂地學英文！

---

## ✨ 功能特色

| 功能 | 說明 |
|------|------|
| 📚 **單字學習** | 依主題（動物、顏色、食物、家人、學校）瀏覽單字卡片 |
| 🔊 **語音播放** | 點擊喇叭按鈕，使用瀏覽器 Web Speech API 朗讀單字與例句 |
| 🎧 **聽力測驗** | 聽到單字語音後，選出正確的中文意思 |
| ✏️ **拼字測驗** | 聽到單字語音後，輸入正確的英文拼字 |
| 🏆 **錯誤檢討** | 測驗結束後顯示答錯的單字，方便複習 |

---

## 🛠 技術架構

| 層級 | 技術 |
|------|------|
| 後端 | Spring Boot 3.4 + Java 17 |
| 資料庫 | H2 Database（嵌入式，免安裝） |
| ORM | Spring Data JPA |
| 前端 | Thymeleaf + Bootstrap 5 + Bootstrap Icons |
| 語音 | Web Speech API（瀏覽器原生，免費） |

---

## 🚀 快速啟動

### 1. 建置專案
```bash
cd /home/eric/GitHub/primary-english
mvn -DskipTests package
```

### 2. 啟動應用
```bash
java -jar target/primary-english-1.0.0.jar
```

### 3. 開啟瀏覽器
```
http://localhost:8080/
```

---

## 📂 專案結構

```
primary-english/
├── pom.xml                          # Maven 設定
├── src/main/java/com/primaryenglish/
│   ├── PrimaryEnglishApplication.java
│   ├── config/
│   │   └── SecurityConfig.java      # Spring Security 設定（全開放）
│   ├── entity/
│   │   ├── Category.java            # 分類實體
│   │   └── Vocabulary.java          # 單字實體
│   ├── repository/
│   │   ├── CategoryRepository.java
│   │   └── VocabularyRepository.java
│   ├── controller/
│   │   ├── HomeController.java      # 首頁
│   │   ├── VocabularyController.java # 單字頁 + API
│   │   └── QuizController.java      # 測驗頁 + API
│   └── dto/
│       └── QuizResult.java
├── src/main/resources/
│   ├── application.properties       # H2 / JPA / Thymeleaf 設定
│   ├── data.sql                    # 預設單字資料（37 個單字 / 5 主題）
│   ├── static/
│   │   ├── css/custom.css          # 客製化樣式
│   │   └── js/
│   │       ├── speech.js           # Web Speech API 封裝
│   │       └── main.js             # 通用互動腳本
│   └── templates/
│       ├── fragments/
│       │   └── layout.html         # 共用佈局
│       ├── index.html              # 首頁
│       ├── vocabulary.html         # 單字學習
│       ├── quiz-listen.html        # 聽力測驗
│       └── quiz-spell.html         # 拼字測驗
```

---

## 🗄 H2 資料庫控制台

開發模式下可透過以下網址查看資料庫：
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:primaryenglish`
- 帳號: `sa`
- 密碼: （空白）

---

## 📋 預設單字資料

| 主題 | 單字數 |
|------|--------|
| 🐾 動物 | 8 個 |
| 🎨 顏色 | 8 個 |
| 🍎 食物 | 8 個 |
| 👨‍👩‍👧‍👦 家人 | 6 個 |
| 🎒 學校 | 7 個 |

---

## 🔊 語音播放說明

本系統使用瀏覽器內建的 **Web Speech API** 進行語音朗讀，無需安裝任何套件或註冊 API Key。

- 支援瀏覽器：Chrome、Edge、Safari、Firefox
- 語音速度已調整為 **0.85 倍速**，適合國小學童聆聽
- 點擊單字卡片上的 🔊 喇叭按鈕即可播放

---

## 🧪 API 端點

| 方法 | 路徑 | 說明 |
|------|------|------|
| GET | `/api/vocabularies` | 查詢所有單字（JSON） |
| GET | `/api/vocabularies?category={id}` | 依分類查詢單字 |
| GET | `/api/vocabularies/{id}` | 查詢單字詳情 |
| POST | `/api/quiz/result` | 提交測驗結果 |

---

## 📝 開發注意事項

- **無需資料庫安裝**：H2 為嵌入式資料庫，啟動時自動建表並載入 `data.sql`
- **無需 Node/npm**：前端使用 Thymeleaf + CDN 資源，無需建置
- **熱部署**：`spring-boot-devtools` 已加入，開發時自動重新載入
- **Spring Security**：全開放模式（無登入），適合兒童直接使用

---

Made for Kids 🌟 · 國小英文單字互動學習系統
# primary-english
