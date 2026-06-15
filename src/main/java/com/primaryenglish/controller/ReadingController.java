package com.primaryenglish.controller;

import com.primaryenglish.entity.Article;
import com.primaryenglish.entity.ReadingQuestion;
import com.primaryenglish.repository.ArticleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/reading")
public class ReadingController {

    private final ArticleRepository articleRepo;
   

    public ReadingController(ArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
       
    }

    // 文章列表
    @GetMapping("")
    public String listArticles(
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String level,
            Model model) {

        List<Article> articles;
        if (grade != null && !grade.isEmpty()) {
            articles = articleRepo.findByGrade(grade);
        } else if (level != null && !level.isEmpty()) {
            articles = articleRepo.findByLevel(level);
        } else {
            articles = articleRepo.findAllByOrderByGradeAscIdAsc();
        }

        model.addAttribute("articles", articles);
        model.addAttribute("currentGrade", grade);
        model.addAttribute("currentLevel", level);
        return "reading-list";
    }

    // 閱讀文章 + 答題頁面
    @GetMapping("/{id}")
    public String readArticle(@PathVariable Long id, Model model) {
        Optional<Article> opt = articleRepo.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/reading";
        }
        Article article = opt.get();
        model.addAttribute("article", article);
        return "reading-quiz";
    }

    // 提交答案
    @PostMapping("/{id}/submit")
    @ResponseBody
    public Map<String, Object> submitAnswers(
            @PathVariable Long id,
            @RequestParam Map<String, String> params,
            HttpSession session) {

        Optional<Article> opt = articleRepo.findById(id);
        if (opt.isEmpty()) {
            return Map.of("error", "文章不存在");
        }
        Article article = opt.get();
        List<ReadingQuestion> questions = article.getQuestions();

        int score = 0;
        int total = questions.size();
        List<Map<String, Object>> results = new ArrayList<>();

        for (ReadingQuestion q : questions) {
            String userAnswer = params.getOrDefault("q_" + q.getId(), "").trim().toUpperCase();
            String correct = q.getCorrectAnswer().toUpperCase();
            boolean isCorrect = userAnswer.equals(correct);
            if (isCorrect) score++;

            Map<String, Object> r = new LinkedHashMap<>();
            r.put("questionId", q.getId());
            r.put("question", q.getQuestion());
            r.put("userAnswer", userAnswer);
            r.put("correctAnswer", correct);
            r.put("correctText", q.getOption(correct));
            r.put("isCorrect", isCorrect);
            results.add(r);
        }

        // 儲存到 session 方便結果頁顯示
        session.setAttribute("reading_result_" + id, results);
        session.setAttribute("reading_score_" + id, score);
        session.setAttribute("reading_total_" + id, total);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("score", score);
        response.put("total", total);
        response.put("percentage", Math.round((double) score / total * 100));
        response.put("results", results);
        return response;
    }

    // 結果頁面
    @GetMapping("/{id}/result")
    public String showResult(@PathVariable Long id, Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results =
            (List<Map<String, Object>>) session.getAttribute("reading_result_" + id);
        Integer score = (Integer) session.getAttribute("reading_score_" + id);
        Integer total = (Integer) session.getAttribute("reading_total_" + id);

        if (results == null) {
            return "redirect:/reading/" + id;
        }

        Optional<Article> opt = articleRepo.findById(id);
        if (opt.isPresent()) {
            model.addAttribute("article", opt.get());
        }
        model.addAttribute("results", results);
        model.addAttribute("score", score != null ? score : 0);
        model.addAttribute("total", total != null ? total : 0);
        model.addAttribute("percentage", total != null && total > 0
                ? Math.round((double) score / total * 100) : 0);
        return "reading-result";
    }
}
