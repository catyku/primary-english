# 國小英文單字互動學習系統 — 實作規劃書

> **專案名稱：** Primary English Vocabulary Learning System  
> **定位：** 國小學童英文單字互動學習網站（例句 + 語音 + 測驗）

---

## 一、需求分析

### 目標使用者
- 國小 1–6 年級學生
- 家長或老師協助選擇學習單元

### 核心功能

| 功能模組 | 說明 |
|---------|------|
| **單字瀏覽** | 依年級/主題分類瀏覽單字卡片（單字、中文、音標、例句） |
| **語音播放** | 點擊喇叭圖示，使用瀏覽器 Web Speech API 朗讀單字與例句 |
| **聽力測驗** | 聽到語音後選擇正確中文意思 |
| **拼字測驗** | 聽到語音後拼出正確英文單字 |
| **學習進度** | 記錄每次測驗分數（Session 或 H2 儲存） |

### 單字資料範例（首批 5 個主題，每主題 8–12 單字）
- **Animals（動物）** — dog, cat, bird, fish…
- **Colors（顏色）** — red, blue, green, yellow…
- **Food（食物）** — apple, bread, milk, rice…
- **Family（家人）** — father, mother, brother, sister…
- **School（學校）** — book, pencil, teacher, student…

---

## 二、技術選型

| 層級 | 技術 | 理由 |
|------|------|------|
| 後端 | Spring Boot 3.4 + Java 17 | 穩定、啟動快、內嵌容器 |
| 資料庫 | H2 Database (embedded) | 免安裝、開箱即用、自動建表 |
| ORM | Spring Data JPA | 簡化 CRUD，自動生成 SQL |
| 前端 | Thymeleaf + Bootstrap 5 | 無需 SPA 編譯，直接渲染 HTML |
| 語音 | Web Speech API (前端) | 瀏覽器原生 TTS，免 API Key、免費 |
| 音效 | Web Audio API 或外部音效 | 測驗答對/答錯音效回饋 |
| 圖示 | Font Awesome / Bootstrap Icons | 簡潔向量圖示 |

### 為什麼不用 Vue/SPA？
- 本專案以「內容呈現 + 簡單互動」為主，Thymeleaf 即可勝任
- 國小使用場景常見平板/舊電腦，SPA 載入較重
- 減少前後端分離的建置複雜度（無需 Node/npm）

---

## 三、專案結構

```
primary-english/
├── pom.xml
├── README.md
├── src/main/java/com/primaryenglish/
│   ├── PrimaryEnglishApplication.java
│   ├── config/
│   │   └── SecurityConfig.java          # 開放所有頁面（無登入）
│   ├── entity/
│   │   ├── Vocabulary.java              # 單字實體
│   │   └── Category.java                # 分類實體
│   ├── repository/
│   │   ├── VocabularyRepository.java    # JPA Repository
│   │   └── CategoryRepository.java
│   ├── controller/
│   │   ├── HomeController.java          # 首頁 + 分類列表
│   │   ├── VocabularyController.java    # 單字瀏覽頁
│   │   └── QuizController.java          # 測驗頁（API + 頁面）
│   └── dto/
│       └── QuizResult.java              # 測驗結果 DTO
├── src/main/resources/
│   ├── application.properties
│   ├── data.sql                         # 預設單字資料
│   ├── schema.sql（可選，H2 auto 可省略）
│   ├── static/
│   │   ├── css/
│   │   │   └── custom.css               # 客製化樣式（卡片、動畫）
│   │   ├── js/
│   │   │   ├── speech.js                # Web Speech API 封裝
│   │   │   ├── quiz.js                  # 測驗邏輯
│   │   │   └── main.js                  # 通用互動
│   │   └── img/
│   │       └── animals/                 # 單字配圖（可選，或用表情符號）
│   └── templates/
│       ├── fragments/
│       │   ├── layout.html              # 共用佈局（含 navbar、footer）
│       │   └── vocab-card.html          # 單字卡片 fragment
│       ├── index.html                   # 首頁（主題選擇）
│       ├── vocabulary.html              # 單字學習頁
│       ├── quiz-listen.html             # 聽力測驗
│       ├── quiz-spell.html              # 拼字測驗
│       └── result.html                  # 測驗結果
```

---

## 四、資料庫設計（H2）

### Entity: Category（分類）

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long (PK, auto) | 分類 ID |
| name | String | 分類名稱（e.g. "Animals", "顏色"）|
| icon | String | Bootstrap icon class |
| sortOrder | Integer | 排序 |

### Entity: Vocabulary（單字）

| 欄位 | 型別 | 說明 |
|------|------|------|
| id | Long (PK, auto) | 單字 ID |
| english | String | 英文單字 |
| chinese | String | 中文意思 |
| phonetic | String | KK 音標 |
| exampleEn | String | 英文例句 |
| exampleCn | String | 例句中文翻譯 |
| categoryId | Long (FK) | 所屬分類 |
| image | String | 圖片檔名（可選）|

---

## 五、API 設計

| 方法 | 路徑 | 說明 |
|------|------|------|
| GET | `/` | 首頁（主題列表）|
| GET | `/vocabulary?category={id}` | 單字學習頁 |
| GET | `/api/vocabularies` | 查詢所有單字（JSON）|
| GET | `/api/vocabularies?category={id}` | 依分類查詢單字 |
| GET | `/api/vocabularies/{id}` | 查詢單字詳情 |
| GET | `/quiz/listen` | 聽力測驗頁面 |
| GET | `/quiz/spell` | 拼字測驗頁面 |
| POST | `/api/quiz/result` | 提交測驗結果 |

---

## 六、前端互動設計

### 1. 單字卡片（Vocabulary Card）
- 正面：英文單字 + 音標 + 語音按鈕
- 反面（或展開）：中文意思 + 例句 + 例句語音按鈕
- 配色：柔和 pastel 色系，適合兒童

### 2. 語音功能（speech.js）
```javascript
function speak(text, lang = 'en-US') {
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = lang;
    utterance.rate = 0.85;  // 國小適中速度
    speechSynthesis.speak(utterance);
}
```
- 單字朗讀：`speak(word, 'en-US')`
- 例句朗讀：`speak(sentence, 'en-US')`
- 中文說明：`speak(translation, 'zh-TW')`（可選）

### 3. 聽力測驗流程
1. 隨機選 5 題
2. 播放語音（自動播放單字）
3. 顯示 4 個選項（含正確答案 + 3 個同分類干擾項）
4. 學生點選 → 立即顯示對錯 + 音效
5. 5 題後顯示總分與錯誤檢討

### 4. 拼字測驗流程
1. 隨機選 5 題
2. 播放語音
3. 顯示字母方塊（拖曳或點擊輸入）
4. 或直接輸入（簡化版）
5. 檢查拼字 → 顯示對錯

---

## 七、實作階段

### Phase 1：基礎建設（約 20 分鐘）
1. 建立 Maven 專案（pom.xml）
2. 設定 application.properties（H2、JPA、Thymeleaf）
3. 建立 entities + repositories
4. 建立 schema + data.sql（種子資料）

### Phase 2：核心功能（約 30 分鐘）
5. 建立 layout fragment（navbar + footer）
6. 首頁 Controller + 主題列表頁面
7. 單字瀏覽 Controller + 單字卡片頁面
8. 整合 Web Speech API（speech.js）

### Phase 3：測驗功能（約 25 分鐘）
9. 聽力測驗頁面 + 隨機出題邏輯
10. 拼字測驗頁面 + 輸入驗證
11. 測驗結果頁面 + 錯誤檢討

### Phase 4：優化與驗證（約 15 分鐘）
12. 自定義 CSS（卡片動畫、響應式）
13. 本地建置 + 啟動
14. Playwright 截圖驗證
15. 撰寫 README

---

## 八、驗收標準

- [ ] `mvn package` 成功建置 jar/war
- [ ] `java -jar` 啟動後可於 `http://localhost:8080/` 正常訪問
- [ ] 首頁顯示 5 個主題卡片
- [ ] 點選主題後顯示該主題所有單字（含英文、中文、音標、例句）
- [ ] 點擊喇叭按鈕可聽到語音（Web Speech API）
- [ ] 聽力測驗能隨機出 5 題，選擇後顯示對錯
- [ ] 拼字測驗能播放語音，輸入後檢查拼字
- [ ] Playwright 截圖驗證首頁、單字頁、測驗頁正常
