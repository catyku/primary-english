package com.primaryenglish.controller;

import com.primaryenglish.entity.User;
import com.primaryenglish.entity.UserVocabProgress;
import com.primaryenglish.service.AiConversationService;
import com.primaryenglish.service.ProgressService;
import com.primaryenglish.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AiConversationController {

    private final AiConversationService aiConversationService;
    private final UserService userService;
    private final ProgressService progressService;

    public AiConversationController(AiConversationService aiConversationService,
                                     UserService userService,
                                     ProgressService progressService) {
        this.aiConversationService = aiConversationService;
        this.userService = userService;
        this.progressService = progressService;
    }

    private User getCurrentUser(HttpSession session) {
        String username = (String) session.getAttribute("USER_NAME");
        if (username == null) return null;
        return userService.findByUsername(username).orElse(null);
    }

    /**
     * 前端互動頁面
     */
    @GetMapping("/ai-conversation")
    public String conversationPage(HttpSession session, Model model) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        boolean isOllama = "ollama".equalsIgnoreCase(user.getApiProvider() != null ? user.getApiProvider() : "");
        model.addAttribute("apiEnabled", Boolean.TRUE.equals(user.getApiEnabled())
                && user.getApiProvider() != null
                && (isOllama || user.getApiKey() != null));
        model.addAttribute("providers", com.primaryenglish.service.AiGenerationService.getProviders());
        return "ai-conversation";
    }

    /**
     * API: 開始新對話 — AI 訂主題並提出第一個問題
     */
    @PostMapping("/api/ai-conversation/start")
    @ResponseBody
    public ResponseEntity<?> startConversation(@RequestBody Map<String, Object> body,
                                                HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "請先登入"));
        }
        boolean isOllama = "ollama".equalsIgnoreCase(user.getApiProvider() != null ? user.getApiProvider() : "");
        if (!Boolean.TRUE.equals(user.getApiEnabled()) || (!isOllama && user.getApiKey() == null)) {
            return ResponseEntity.badRequest().body(Map.of("error", "請先到個人資料設定 AI API"));
        }

        // 年級與難度
        int grade = 3;
        if (body.containsKey("grade")) {
            Object gradeObj = body.get("grade");
            if (gradeObj instanceof Number) {
                grade = ((Number) gradeObj).intValue();
            } else if (gradeObj instanceof String) {
                try {
                    grade = Integer.parseInt((String) gradeObj);
                } catch (NumberFormatException e) {
                    grade = 3;
                }
            }
        }
        String difficulty = body.containsKey("difficulty") ? (String) body.get("difficulty") : "medium";

        // 取得使用者已學單字（若無則隨機取部分單字）
        List<String> learnedWords = getLearnedWords(user.getId());

        return aiConversationService.startConversation(user, grade, difficulty, learnedWords)
            .map(result -> ResponseEntity.ok((Map<String, Object>) result))
            .onErrorResume(e -> {
                Map<String, Object> err = new HashMap<>();
                err.put("error", e.getMessage());
                return reactor.core.publisher.Mono.just(ResponseEntity.badRequest().body(err));
            })
            .block();
    }

    /**
     * API: 繼續對話 — 使用者回答，AI 回應
     */
    @PostMapping("/api/ai-conversation/message")
    @ResponseBody
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> body,
                                          HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "請先登入"));
        }
        boolean isOllama2 = "ollama".equalsIgnoreCase(user.getApiProvider() != null ? user.getApiProvider() : "");
        if (!Boolean.TRUE.equals(user.getApiEnabled()) || (!isOllama2 && user.getApiKey() == null)) {
            return ResponseEntity.badRequest().body(Map.of("error", "請先到個人資料設定 AI API"));
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> history = body.containsKey("history")
            ? (List<Map<String, Object>>) body.get("history") : new ArrayList<>();
        String userMessage = body.containsKey("message") ? (String) body.get("message") : "";
        String topic = body.containsKey("topic") ? (String) body.get("topic") : "";
        int grade = body.containsKey("grade") ? ((Number) body.get("grade")).intValue() : 3;
        String difficulty = body.containsKey("difficulty") ? (String) body.get("difficulty") : "medium";

        return aiConversationService.continueConversation(user, topic, grade, difficulty, history, userMessage)
            .map(result -> ResponseEntity.ok((Map<String, Object>) result))
            .onErrorResume(e -> {
                Map<String, Object> err = new HashMap<>();
                err.put("error", e.getMessage());
                return reactor.core.publisher.Mono.just(ResponseEntity.badRequest().body(err));
            })
            .block();
    }

    /**
     * 取得使用者已學習的單字（回傳英文單字列表）
     */
    private List<String> getLearnedWords(Long userId) {
        try {
            List<UserVocabProgress> learned = progressService.getUserProgress(userId);
            if (learned != null && !learned.isEmpty()) {
                return learned.stream()
                    .map(p -> p.getVocabulary() != null ? p.getVocabulary().getEnglish() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // 若無學習進度則回空
        }
        return new ArrayList<>();
    }
}
