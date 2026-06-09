package com.primaryenglish.repository;

import com.primaryenglish.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserIdOrderByQuizDateDesc(Long userId);

    @Query("SELECT q.quizType, COUNT(q), AVG(q.score) FROM QuizResult q WHERE q.user.id = ?1 GROUP BY q.quizType")
    List<Object[]> getStatsByUserId(Long userId);

    @Query("SELECT AVG(q.score) FROM QuizResult q WHERE q.user.id = ?1")
    Double getAverageScoreByUserId(Long userId);
}
