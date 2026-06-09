package com.primaryenglish.repository;

import com.primaryenglish.entity.UserVocabProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabProgressRepository extends JpaRepository<UserVocabProgress, Long> {
    Optional<UserVocabProgress> findByUserIdAndVocabularyId(Long userId, Long vocabId);
    List<UserVocabProgress> findByUserId(Long userId);

    @Query("SELECT COUNT(u) FROM UserVocabProgress u WHERE u.user.id = ?1 AND u.isLearned = true")
    Long countLearnedByUserId(Long userId);

    @Query("SELECT COUNT(u) FROM UserVocabProgress u WHERE u.user.id = ?1 AND u.isLearned = true AND u.vocabulary.category.id = ?2")
    Long countLearnedByUserIdAndCategoryId(Long userId, Long categoryId);
}
