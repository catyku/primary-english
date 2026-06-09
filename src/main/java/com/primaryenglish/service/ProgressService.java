package com.primaryenglish.service;

import com.primaryenglish.entity.User;
import com.primaryenglish.entity.UserVocabProgress;
import com.primaryenglish.entity.Vocabulary;
import com.primaryenglish.repository.UserVocabProgressRepository;
import com.primaryenglish.repository.VocabularyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressService {

    private final UserVocabProgressRepository progressRepository;
    private final VocabularyRepository vocabRepository;

    public ProgressService(UserVocabProgressRepository progressRepository,
                           VocabularyRepository vocabRepository) {
        this.progressRepository = progressRepository;
        this.vocabRepository = vocabRepository;
    }

    @Transactional
    public UserVocabProgress markAsLearned(User user, Long vocabId) {
        Optional<UserVocabProgress> existing = progressRepository.findByUserIdAndVocabularyId(user.getId(), vocabId);

        UserVocabProgress progress;
        if (existing.isPresent()) {
            progress = existing.get();
            if (!progress.isLearned()) {
                progress.setLearned(true);
                progress.setLearnedAt(LocalDateTime.now());
            }
        } else {
            progress = new UserVocabProgress();
            progress.setUser(user);
            Vocabulary vocab = vocabRepository.findById(vocabId)
                    .orElseThrow(() -> new RuntimeException("單字不存在"));
            progress.setVocabulary(vocab);
            progress.setLearned(true);
            progress.setLearnedAt(LocalDateTime.now());
        }

        return progressRepository.save(progress);
    }

    @Transactional
    public UserVocabProgress recordReview(User user, Long vocabId) {
        Optional<UserVocabProgress> existing = progressRepository.findByUserIdAndVocabularyId(user.getId(), vocabId);

        UserVocabProgress progress;
        if (existing.isPresent()) {
            progress = existing.get();
            progress.setReviewCount(progress.getReviewCount() + 1);
            progress.setLastReviewed(LocalDateTime.now());
        } else {
            progress = new UserVocabProgress();
            progress.setUser(user);
            Vocabulary vocab = vocabRepository.findById(vocabId)
                    .orElseThrow(() -> new RuntimeException("單字不存在"));
            progress.setVocabulary(vocab);
            progress.setReviewCount(1);
            progress.setLastReviewed(LocalDateTime.now());
        }

        return progressRepository.save(progress);
    }

    public List<UserVocabProgress> getUserProgress(Long userId) {
        return progressRepository.findByUserId(userId);
    }

    public long getLearnedCount(Long userId) {
        Long count = progressRepository.countLearnedByUserId(userId);
        return count != null ? count : 0;
    }

    public long getLearnedCountByCategory(Long userId, Long categoryId) {
        Long count = progressRepository.countLearnedByUserIdAndCategoryId(userId, categoryId);
        return count != null ? count : 0;
    }

    public boolean isVocabLearned(Long userId, Long vocabId) {
        return progressRepository.findByUserIdAndVocabularyId(userId, vocabId)
                .map(UserVocabProgress::isLearned)
                .orElse(false);
    }
}
