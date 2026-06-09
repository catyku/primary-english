package com.primaryenglish.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_vocab_progress")
public class UserVocabProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(nullable = false)
    private boolean isLearned = false;

    @Column(name = "learned_at")
    private LocalDateTime learnedAt;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "last_reviewed")
    private LocalDateTime lastReviewed;

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        if (isLearned && learnedAt == null) {
            learnedAt = LocalDateTime.now();
        }
    }

    public UserVocabProgress() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Vocabulary getVocabulary() { return vocabulary; }
    public void setVocabulary(Vocabulary vocabulary) { this.vocabulary = vocabulary; }

    public boolean isLearned() { return isLearned; }
    public void setLearned(boolean learned) { isLearned = learned; }

    public LocalDateTime getLearnedAt() { return learnedAt; }
    public void setLearnedAt(LocalDateTime learnedAt) { this.learnedAt = learnedAt; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public LocalDateTime getLastReviewed() { return lastReviewed; }
    public void setLastReviewed(LocalDateTime lastReviewed) { this.lastReviewed = lastReviewed; }
}
