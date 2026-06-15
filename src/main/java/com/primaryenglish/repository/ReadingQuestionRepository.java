package com.primaryenglish.repository;

import com.primaryenglish.entity.ReadingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReadingQuestionRepository extends JpaRepository<ReadingQuestion, Long> {
    List<ReadingQuestion> findByArticleIdOrderByOrderNumAsc(Long articleId);
}
