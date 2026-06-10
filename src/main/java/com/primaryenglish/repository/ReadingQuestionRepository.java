package com.primaryenglish.repository;

import com.primaryenglish.entity.ReadingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingQuestionRepository extends JpaRepository<ReadingQuestion, Long> {
    List<ReadingQuestion> findByArticleIdOrderByOrderNumAsc(Long articleId);
}
