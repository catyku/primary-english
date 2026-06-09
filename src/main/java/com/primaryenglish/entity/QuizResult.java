package com.primaryenglish.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "quiz_type", nullable = false, length = 20)
    private String quizType; // "listen" or "spell"

    @Column(name = "total_questions")
    private Integer totalQuestions = 0;

    @Column(name = "correct_count")
    private Integer correctCount = 0;

    @Column(name = "score")
    private Integer score = 0; // percentage 0-100

    @Column(name = "quiz_date")
    private LocalDateTime quizDate;

    @PrePersist
    protected void onCreate() {
        quizDate = LocalDateTime.now();
    }

    public QuizResult() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getQuizType() { return quizType; }
    public void setQuizType(String quizType) { this.quizType = quizType; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public LocalDateTime getQuizDate() { return quizDate; }
    public void setQuizDate(LocalDateTime quizDate) { this.quizDate = quizDate; }
}
