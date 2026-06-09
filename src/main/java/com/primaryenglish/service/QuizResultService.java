package com.primaryenglish.service;

import com.primaryenglish.entity.QuizResult;
import com.primaryenglish.entity.User;
import com.primaryenglish.repository.QuizResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;

    public QuizResultService(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    public QuizResult saveResult(User user, String quizType, int totalQuestions, int correctCount) {
        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setQuizType(quizType);
        result.setTotalQuestions(totalQuestions);
        result.setCorrectCount(correctCount);
        int score = totalQuestions > 0 ? (int) Math.round((double) correctCount / totalQuestions * 100) : 0;
        result.setScore(score);
        return quizResultRepository.save(result);
    }

    public List<QuizResult> getUserResults(Long userId) {
        return quizResultRepository.findByUserIdOrderByQuizDateDesc(userId);
    }

    public List<Object[]> getUserStats(Long userId) {
        return quizResultRepository.getStatsByUserId(userId);
    }

    public Double getAverageScore(Long userId) {
        return quizResultRepository.getAverageScoreByUserId(userId);
    }

    public long getTotalQuizCount(Long userId) {
        return quizResultRepository.findByUserIdOrderByQuizDateDesc(userId).size();
    }
}
