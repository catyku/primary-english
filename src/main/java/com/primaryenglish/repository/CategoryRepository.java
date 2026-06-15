package com.primaryenglish.repository;

import com.primaryenglish.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderBySortOrderAsc();
}
