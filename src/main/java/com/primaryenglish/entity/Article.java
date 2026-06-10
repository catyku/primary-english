package com.primaryenglish.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;           // Article title

    @Column(nullable = false, length = 100)
    private String titleCn;         // Chinese title

    @Column(nullable = false, length = 5000)
    private String content;         // Article content

    @Column(length = 1000)
    private String contentCn;       // Chinese translation (optional)

    @Column(length = 50)
    private String level;           // Difficulty: easy, medium, hard

    @Column(length = 20)
    private String grade;           // Target grade: 3,4,5,6

    @Column(length = 50)
    private String topic;           // Topic

    @Column(name = "word_count")
    private Integer wordCount;      // Word count

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orderNum ASC")
    private List<ReadingQuestion> questions = new ArrayList<>();

    public Article() {}

    public Article(String title, String titleCn, String content, String contentCn,
                   String level, String grade, String topic, Integer wordCount) {
        this.title = title;
        this.titleCn = titleCn;
        this.content = content;
        this.contentCn = contentCn;
        this.level = level;
        this.grade = grade;
        this.topic = topic;
        this.wordCount = wordCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleCn() { return titleCn; }
    public void setTitleCn(String titleCn) { this.titleCn = titleCn; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getContentCn() { return contentCn; }
    public void setContentCn(String contentCn) { this.contentCn = contentCn; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }

    public List<ReadingQuestion> getQuestions() { return questions; }
    public void setQuestions(List<ReadingQuestion> questions) { this.questions = questions; }

    public void addQuestion(ReadingQuestion q) {
        q.setArticle(this);
        questions.add(q);
    }
}
