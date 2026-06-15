package com.primaryenglish.repository;

import com.primaryenglish.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByGrade(String grade);
    List<Article> findByLevel(String level);
    List<Article> findByTopic(String topic);
    List<Article> findAllByOrderByGradeAscIdAsc();
}
