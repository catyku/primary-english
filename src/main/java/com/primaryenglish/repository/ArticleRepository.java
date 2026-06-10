package com.primaryenglish.repository;

import com.primaryenglish.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByGrade(String grade);
    List<Article> findByLevel(String level);
    List<Article> findByTopic(String topic);
    List<Article> findAllByOrderByGradeAscIdAsc();
}
