package com.primaryenglish.controller;

import com.primaryenglish.entity.*;
import com.primaryenglish.repository.*;
import com.primaryenglish.service.AiGenerationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private VocabularyRepository vocabRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private ArticleRepository articleRepo;
    @Autowired private ReadingQuestionRepository questionRepo;
    @Autowired private AiGenerationService aiService;

    // ===================== 單字管理 =====================

    @GetMapping("/vocabularies")
    public String listVocabularies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long category,
            Model model) {
        List<Vocabulary> vocabularies;
        if (keyword != null && !keyword.isEmpty()) {
            vocabularies = vocabRepo.findByEnglishContainingIgnoreCaseOrChineseContaining(keyword, keyword);
        } else if (category != null) {
            vocabularies = vocabRepo.findByCategoryId(category);
        } else {
            vocabularies = vocabRepo.findAllByOrderByIdDesc();
        }
        model.addAttribute("vocabularies", vocabularies);
        model.addAttribute("categories", categoryRepo.findAllByOrderBySortOrderAsc());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentCategory", category);
        return "admin/vocab-list";
    }

    @GetMapping("/vocabularies/new")
    public String newVocabulary(Model model) {
        model.addAttribute("vocabulary", new Vocabulary());
        model.addAttribute("categories", categoryRepo.findAllByOrderBySortOrderAsc());
        model.addAttribute("mode", "new");
        return "admin/vocab-form";
    }

    @GetMapping("/vocabularies/{id}/edit")
    public String editVocabulary(@PathVariable Long id, Model model) {
        Optional<Vocabulary> opt = vocabRepo.findById(id);
        if (opt.isEmpty()) return "redirect:/admin/vocabularies";
        model.addAttribute("vocabulary", opt.get());
        model.addAttribute("categories", categoryRepo.findAllByOrderBySortOrderAsc());
        model.addAttribute("mode", "edit");
        return "admin/vocab-form";
    }

    @PostMapping("/vocabularies")
    public String createVocabulary(@ModelAttribute Vocabulary vocab,
                                   @RequestParam Long categoryId,
                                   RedirectAttributes ra) {
        Category cat = categoryRepo.findById(categoryId).orElse(null);
        if (cat == null) {
            ra.addFlashAttribute("error", "分類不存在");
            return "redirect:/admin/vocabularies/new";
        }
        vocab.setCategory(cat);
        vocabRepo.save(vocab);
        ra.addFlashAttribute("success", "單字「" + vocab.getEnglish() + "」新增成功！");
        return "redirect:/admin/vocabularies";
    }

    @PostMapping("/vocabularies/{id}")
    public String updateVocabulary(@PathVariable Long id,
                                   @ModelAttribute Vocabulary vocab,
                                   @RequestParam Long categoryId,
                                   RedirectAttributes ra) {
        Optional<Vocabulary> opt = vocabRepo.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "單字不存在");
            return "redirect:/admin/vocabularies";
        }
        Category cat = categoryRepo.findById(categoryId).orElse(null);
        if (cat == null) {
            ra.addFlashAttribute("error", "分類不存在");
            return "redirect:/admin/vocabularies";
        }
        Vocabulary existing = opt.get();
        existing.setEnglish(vocab.getEnglish());
        existing.setChinese(vocab.getChinese());
        existing.setPhonetic(vocab.getPhonetic());
        existing.setExampleEn(vocab.getExampleEn());
        existing.setExampleCn(vocab.getExampleCn());
        existing.setCategory(cat);
        existing.setGrade(vocab.getGrade());
        existing.setImage(vocab.getImage());
        vocabRepo.save(existing);
        ra.addFlashAttribute("success", "單字「" + vocab.getEnglish() + "」更新成功！");
        return "redirect:/admin/vocabularies";
    }

    @PostMapping("/vocabularies/{id}/delete")
    public String deleteVocabulary(@PathVariable Long id, RedirectAttributes ra) {
        try {
            vocabRepo.deleteById(id);
            ra.addFlashAttribute("success", "單字已刪除");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "刪除失敗：" + e.getMessage());
        }
        return "redirect:/admin/vocabularies";
    }

    // ===================== 文章管理 =====================

    @GetMapping("/articles")
    public String listArticles(Model model) {
        model.addAttribute("articles", articleRepo.findAllByOrderByGradeAscIdAsc());
        return "admin/article-list";
    }

    @GetMapping("/articles/new")
    public String newArticle(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("mode", "new");
        return "admin/article-form";
    }

    @GetMapping("/articles/{id}/edit")
    public String editArticle(@PathVariable Long id, Model model) {
        Optional<Article> opt = articleRepo.findById(id);
        if (opt.isEmpty()) return "redirect:/admin/articles";
        model.addAttribute("article", opt.get());
        model.addAttribute("mode", "edit");
        return "admin/article-form";
    }

    @PostMapping("/articles")
    public String createArticle(@ModelAttribute Article article,
                                @RequestParam Map<String, String> params,
                                RedirectAttributes ra) {
        // Set word count automatically
        if (article.getWordCount() == null || article.getWordCount() == 0) {
            String[] words = article.getContent().trim().split("\\s+");
            article.setWordCount(words.length);
        }
        articleRepo.save(article);

        // Save questions from params
        saveQuestions(article, params);

        ra.addFlashAttribute("success", "文章「" + article.getTitle() + "」新增成功！");
        return "redirect:/admin/articles";
    }

    @PostMapping("/articles/{id}")
    public String updateArticle(@PathVariable Long id,
                                @ModelAttribute Article article,
                                @RequestParam Map<String, String> params,
                                RedirectAttributes ra) {
        Optional<Article> opt = articleRepo.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "文章不存在");
            return "redirect:/admin/articles";
        }
        Article existing = opt.get();
        existing.setTitle(article.getTitle());
        existing.setTitleCn(article.getTitleCn());
        existing.setContent(article.getContent());
        existing.setContentCn(article.getContentCn());
        existing.setLevel(article.getLevel());
        existing.setGrade(article.getGrade());
        existing.setTopic(article.getTopic());
        if (article.getWordCount() == null || article.getWordCount() == 0) {
            String[] words = article.getContent().trim().split("\\s+");
            existing.setWordCount(words.length);
        } else {
            existing.setWordCount(article.getWordCount());
        }
        articleRepo.save(existing);

        // Remove old questions and re-save
        questionRepo.deleteAll(existing.getQuestions());
        existing.getQuestions().clear();
        saveQuestions(existing, params);

        ra.addFlashAttribute("success", "文章「" + article.getTitle() + "」更新成功！");
        return "redirect:/admin/articles";
    }

    @PostMapping("/articles/{id}/delete")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes ra) {
        try {
            articleRepo.deleteById(id);
            ra.addFlashAttribute("success", "文章已刪除");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "刪除失敗：" + e.getMessage());
        }
        return "redirect:/admin/articles";
    }

    // ===================== AI 自動生成文章 =====================

    @GetMapping("/articles/generate")
    public String generateForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("USER");
        model.addAttribute("article", new Article());
        model.addAttribute("prompt", "");
        model.addAttribute("grade", 3);
        model.addAttribute("providers", AiGenerationService.getProviders());

        if (user != null) {
            model.addAttribute("userProvider", user.getApiProvider());
            model.addAttribute("userModel", user.getApiModel());
            model.addAttribute("apiEnabled", Boolean.TRUE.equals(user.getApiEnabled()));
            model.addAttribute("apiKeySet", user.getApiKey() != null && !user.getApiKey().isBlank());
        } else {
            model.addAttribute("apiEnabled", false);
            model.addAttribute("apiKeySet", false);
        }
        return "admin/article-generate";
    }

    @PostMapping("/articles/generate")
    public String doGenerate(@RequestParam String prompt,
                             @RequestParam(defaultValue = "3") int grade,
                             @RequestParam(defaultValue = "easy") String difficulty,
                             HttpSession session,
                             RedirectAttributes ra) {
        User user = (User) session.getAttribute("USER");
        if (user == null) {
            ra.addFlashAttribute("error", "請先登入");
            return "redirect:/login";
        }
        if (!Boolean.TRUE.equals(user.getApiEnabled())) {
            ra.addFlashAttribute("error", "請先在「個人資料」中啟用 AI 並設定 API Key");
            return "redirect:/profile";
        }
        if (user.getApiKey() == null || user.getApiKey().isBlank()) {
            ra.addFlashAttribute("error", "API Key 未設定，請在「個人資料」中設定");
            return "redirect:/profile";
        }
        if (prompt == null || prompt.trim().isEmpty()) {
            ra.addFlashAttribute("error", "請輸入提示詞（例如：我的寵物、去公園玩）");
            return "redirect:/admin/articles/generate";
        }

        try {
            Article article = aiService.generateArticle(user, prompt.trim(), grade, difficulty).block();
            if (article == null || article.getContent() == null || article.getContent().isBlank()) {
                ra.addFlashAttribute("error", "AI 生成失敗，請重試或更換提示詞");
                return "redirect:/admin/articles/generate";
            }

            article.setGrade(String.valueOf(grade));
            if (article.getWordCount() == null || article.getWordCount() == 0) {
                String[] words = article.getContent().trim().split("\\s+");
                article.setWordCount(words.length);
            }
            article.setTimeLimit(5);

            Article saved = articleRepo.save(article);

            List<ReadingQuestion> questions = new ArrayList<>(article.getQuestions());
            for (ReadingQuestion q : questions) {
                q.setArticle(saved);
                questionRepo.save(q);
            }

            ra.addFlashAttribute("success", "🤖 AI 已成功生成文章「" + saved.getTitle() + "」！共 " + questions.size() + " 題，請檢查內容後儲存。");
            return "redirect:/admin/articles/" + saved.getId() + "/edit";

        } catch (Exception e) {
            ra.addFlashAttribute("error", "AI 生成發生錯誤：" + e.getMessage());
            return "redirect:/admin/articles/generate";
        }
    }

    private void saveQuestions(Article article, Map<String, String> params) {
        int idx = 1;
        while (params.containsKey("q_question_" + idx)) {
            String q = params.get("q_question_" + idx);
            String a = params.getOrDefault("q_optA_" + idx, "");
            String b = params.getOrDefault("q_optB_" + idx, "");
            String c = params.getOrDefault("q_optC_" + idx, "");
            String d = params.getOrDefault("q_optD_" + idx, "");
            String ans = params.getOrDefault("q_answer_" + idx, "A");
            if (q != null && !q.isEmpty() && a != null && !a.isEmpty()) {
                ReadingQuestion rq = new ReadingQuestion(q, a, b, c, d, ans, idx);
                article.addQuestion(rq);
            }
            idx++;
        }
        articleRepo.save(article);
    }
}
