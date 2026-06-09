package com.primaryenglish.repository;

import com.primaryenglish.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    List<Vocabulary> findByCategoryId(Long categoryId);
    List<Vocabulary> findByCategoryIdOrderByIdAsc(Long categoryId);
    List<Vocabulary> findByGradeOrderByIdAsc(String grade);
}
