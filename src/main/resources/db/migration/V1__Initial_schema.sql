-- V1__Initial_schema.sql
-- 初始化数据库表结构（幂等设计，可安全重复执行于已有数据库）

-- ============================================
-- 业务表
-- ============================================

CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    icon VARCHAR(50),
    image VARCHAR(100),
    name VARCHAR(50) NOT NULL,
    name_en VARCHAR(50),
    sort_order INTEGER
);

CREATE TABLE IF NOT EXISTS vocabularies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    chinese VARCHAR(100) NOT NULL,
    english VARCHAR(50) NOT NULL,
    example_cn VARCHAR(500),
    example_en VARCHAR(500),
    grade VARCHAR(10),
    image VARCHAR(100),
    phonetic VARCHAR(100),
    category_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created_at TIMESTAMP,
    display_name VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_login TIMESTAMP,
    password VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    api_enabled BOOLEAN,
    api_key VARCHAR(500),
    api_model VARCHAR(100),
    api_provider VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS user_vocab_progress (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    is_learned BOOLEAN NOT NULL DEFAULT FALSE,
    last_reviewed TIMESTAMP,
    learned_at TIMESTAMP,
    review_count INTEGER DEFAULT 0,
    user_id BIGINT NOT NULL,
    vocab_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS quiz_results (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    correct_count INTEGER,
    quiz_date TIMESTAMP,
    quiz_type VARCHAR(20) NOT NULL,
    score INTEGER,
    total_questions INTEGER,
    user_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS articles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    content VARCHAR(5000) NOT NULL,
    content_cn VARCHAR(1000),
    grade VARCHAR(20),
    level VARCHAR(50),
    title VARCHAR(200) NOT NULL,
    title_cn VARCHAR(100) NOT NULL,
    topic VARCHAR(50),
    word_count INTEGER,
    time_limit INTEGER
);

CREATE TABLE IF NOT EXISTS reading_questions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    correct_answer VARCHAR(1) NOT NULL,
    optiona VARCHAR(200) NOT NULL,
    optionb VARCHAR(200) NOT NULL,
    optionc VARCHAR(200) NOT NULL,
    optiond VARCHAR(200) NOT NULL,
    order_num INTEGER,
    question VARCHAR(500) NOT NULL,
    article_id BIGINT NOT NULL,
    explanation VARCHAR(500)
);

-- ============================================
-- 分类种子数据（如需补充基础分类）
-- ============================================

INSERT OR IGNORE INTO categories (id, name, name_en, icon, image, sort_order) VALUES
(1, '數字', 'Numbers', 'number', NULL, 1),
(2, '人物', 'People', 'user', NULL, 2),
(3, '身體', 'Body', 'heart', NULL, 3),
(4, '服飾', 'Clothes', 'shopping-bag', NULL, 4),
(5, '動物', 'Animals', 'bug', NULL, 5),
(6, '家庭用品', 'Household Items', 'home', NULL, 6),
(7, '食物飲品', 'Food & Drink', 'cookie', NULL, 7),
(8, '地方', 'Places', 'map-pin', NULL, 8),
(9, '時間日期', 'Time & Date', 'clock', NULL, 9),
(10, '動詞', 'Verbs', 'activity', NULL, 10),
(11, '形容詞', 'Adjectives', 'star', NULL, 11),
(12, '介係詞', 'Prepositions', 'arrow-right', NULL, 12),
(13, '自然天氣', 'Nature & Weather', 'cloud', NULL, 13),
(14, '學校學習', 'School & Study', 'book', NULL, 14),
(15, '運動嗜好', 'Sports & Hobbies', 'ball-basketball', NULL, 15),
(16, '職業', 'Jobs', 'briefcase', NULL, 16),
(17, '尺寸數量', 'Size & Quantity', 'resize', NULL, 17),
(18, '節慶', 'Festivals', 'confetti', NULL, 18),
(19, '情感思想', 'Emotions & Thoughts', 'mood-smile', NULL, 19),
(20, '家庭', 'Family', 'users', NULL, 20),
(21, '國小3年級', 'Grade 3', 'school', NULL, 21),
(22, '國小4年級', 'Grade 4', 'school', NULL, 22),
(23, '國小5年級', 'Grade 5', 'school', NULL, 23),
(24, '國小6年級', 'Grade 6', 'school', NULL, 24);
