package com.primaryenglish.controller;

import com.primaryenglish.entity.Category;
import com.primaryenglish.entity.Vocabulary;
import com.primaryenglish.repository.CategoryRepository;
import com.primaryenglish.repository.VocabularyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class QuizController {

    private final VocabularyRepository vocabularyRepository;
    private final CategoryRepository categoryRepository;

    public QuizController(VocabularyRepository vocabularyRepository, CategoryRepository categoryRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/quiz/listen")
    public String listenQuizPage(@RequestParam(name = "category", required = false) Long categoryId,
                                 @RequestParam(name = "grade", required = false) String grade,
                                 Model model) {
        List<Vocabulary> vocabularies = getVocabularies(categoryId, grade);
        model.addAttribute("vocabularies", vocabularies);
        model.addAttribute("quizType", "聽力測驗");
        model.addAttribute("quizIcon", "ti-headphones");
        return "quiz-listen";
    }

    @GetMapping("/quiz/spell")
    public String spellQuizPage(@RequestParam(name = "category", required = false) Long categoryId,
                                @RequestParam(name = "grade", required = false) String grade,
                                Model model) {
        List<Vocabulary> vocabularies = getVocabularies(categoryId, grade);
        model.addAttribute("vocabularies", vocabularies);
        model.addAttribute("quizType", "拼字測驗");
        model.addAttribute("quizIcon", "ti-pencil");
        return "quiz-spell";
    }

    private List<Vocabulary> getVocabularies(Long categoryId, String grade) {
        if (categoryId != null) {
            return vocabularyRepository.findByCategoryIdOrderByIdAsc(categoryId);
        } else if (grade != null) {
            return vocabularyRepository.findByGradeOrderByIdAsc(grade);
        } else {
            return vocabularyRepository.findAll();
        }
    }
}
