package com.primaryenglish.controller;

import com.primaryenglish.entity.Category;
import com.primaryenglish.entity.Vocabulary;
import com.primaryenglish.repository.CategoryRepository;
import com.primaryenglish.repository.VocabularyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class VocabularyController {

    private final VocabularyRepository vocabularyRepository;
    private final CategoryRepository categoryRepository;

    public VocabularyController(VocabularyRepository vocabularyRepository, CategoryRepository categoryRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.categoryRepository = categoryRepository;
    }

    // 頁面：單字學習
    @GetMapping("/vocabulary")
    public String vocabularyPage(@RequestParam(name = "category", required = false) Long categoryId,
                                 @RequestParam(name = "grade", required = false) String grade,
                                 Model model) {
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        model.addAttribute("categories", categories);

        if (categoryId != null) {
            Optional<Category> currentCategory = categoryRepository.findById(categoryId);
            currentCategory.ifPresent(cat -> model.addAttribute("currentCategory", cat));
            List<Vocabulary> vocabularies = vocabularyRepository.findByCategoryIdOrderByIdAsc(categoryId);
            model.addAttribute("vocabularies", vocabularies);
        } else if (grade != null) {
            // Filter by grade - find grade categories
            List<Vocabulary> vocabularies = vocabularyRepository.findByGradeOrderByIdAsc(grade);
            model.addAttribute("vocabularies", vocabularies);
            model.addAttribute("currentGrade", grade);
        } else {
            List<Vocabulary> vocabularies = vocabularyRepository.findAll();
            model.addAttribute("vocabularies", vocabularies);
        }

        return "vocabulary";
    }

    // API：查詢所有單字（JSON）
    @GetMapping("/api/vocabularies")
    @ResponseBody
    public ResponseEntity<List<Vocabulary>> getAllVocabularies(
            @RequestParam(name = "category", required = false) Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(vocabularyRepository.findByCategoryIdOrderByIdAsc(categoryId));
        }
        return ResponseEntity.ok(vocabularyRepository.findAll());
    }

    // API：查詢單字詳情
    @GetMapping("/api/vocabularies/{id}")
    @ResponseBody
    public ResponseEntity<?> getVocabularyById(@PathVariable Long id) {
        Optional<Vocabulary> vocab = vocabularyRepository.findById(id);
        if (vocab.isPresent()) {
            return ResponseEntity.ok(vocab.get());
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Vocabulary not found");
        return ResponseEntity.notFound().build();
    }
}
