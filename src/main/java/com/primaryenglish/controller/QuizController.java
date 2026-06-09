package com.primaryenglish.controller;

import com.primaryenglish.entity.Vocabulary;
import com.primaryenglish.repository.VocabularyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class QuizController {

    private final VocabularyRepository vocabularyRepository;

    public QuizController(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    // 頁面：聽力測驗
    @GetMapping("/quiz/listen")
    public String listenQuizPage(@RequestParam(name = "category", required = false) Long categoryId, Model model) {
        List<Vocabulary> allVocab = categoryId != null
            ? vocabularyRepository.findByCategoryIdOrderByIdAsc(categoryId)
            : vocabularyRepository.findAll();
        model.addAttribute("vocabulariesJson", toJsonList(allVocab));
        model.addAttribute("categoryId", categoryId);
        return "quiz-listen";
    }

    // 頁面：拼字測驗
    @GetMapping("/quiz/spell")
    public String spellQuizPage(@RequestParam(name = "category", required = false) Long categoryId, Model model) {
        List<Vocabulary> allVocab = categoryId != null
            ? vocabularyRepository.findByCategoryIdOrderByIdAsc(categoryId)
            : vocabularyRepository.findAll();
        model.addAttribute("vocabulariesJson", toJsonList(allVocab));
        model.addAttribute("categoryId", categoryId);
        return "quiz-spell";
    }

    // API：提交測驗結果（可擴展儲存進度）
    @PostMapping("/api/quiz/result")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitQuizResult(@RequestBody Map<String, Object> result) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Quiz result received");
        response.put("score", result.get("score"));
        response.put("total", result.get("total"));
        return ResponseEntity.ok(response);
    }

    private List<Map<String, Object>> toJsonList(List<Vocabulary> vocabularies) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Vocabulary v : vocabularies) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", v.getId());
            map.put("english", v.getEnglish());
            map.put("chinese", v.getChinese());
            map.put("phonetic", v.getPhonetic());
            map.put("exampleEn", v.getExampleEn());
            map.put("exampleCn", v.getExampleCn());
            map.put("categoryId", v.getCategory() != null ? v.getCategory().getId() : null);
            list.add(map);
        }
        return list;
    }
}
