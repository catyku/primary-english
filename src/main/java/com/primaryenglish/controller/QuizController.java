package com.primaryenglish.controller;

import com.primaryenglish.entity.Vocabulary;
import com.primaryenglish.repository.VocabularyRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class QuizController {

    private final VocabularyRepository vocabularyRepository;


    public QuizController(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;

    }

    @GetMapping("/quiz/listen")
    public String listenQuizPage(@RequestParam(name = "category", required = false) Long categoryId,
                                 @RequestParam(name = "grade", required = false) String grade,
                                 Model model) {
        List<Vocabulary> vocabularies = getVocabularies(categoryId, grade);
        model.addAttribute("vocabularies", toSimpleMaps(vocabularies));
        model.addAttribute("quizType", "聽力測驗");
        model.addAttribute("quizIcon", "ti-headphones");
        return "quiz-listen";
    }

    @GetMapping("/quiz/spell")
    public String spellQuizPage(@RequestParam(name = "category", required = false) Long categoryId,
                                @RequestParam(name = "grade", required = false) String grade,
                                Model model) {
        List<Vocabulary> vocabularies = getVocabularies(categoryId, grade);
        model.addAttribute("vocabularies", toSimpleMaps(vocabularies));
        model.addAttribute("quizType", "拼字測驗");
        model.addAttribute("quizIcon", "ti-pencil");
        return "quiz-spell";
    }

    private List<Map<String, Object>> toSimpleMaps(List<Vocabulary> vocabularies) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Vocabulary v : vocabularies) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", v.getId());
            map.put("english", v.getEnglish());
            map.put("chinese", v.getChinese());
            map.put("phonetic", v.getPhonetic());
            map.put("exampleEn", v.getExampleEn());
            map.put("exampleCn", v.getExampleCn());
            map.put("grade", v.getGrade());
            map.put("image", v.getImage());
            result.add(map);
        }
        return result;
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
