package com.primaryenglish.service;

import com.primaryenglish.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class AiConversationService {

    private static final Logger logger = LoggerFactory.getLogger(AiConversationService.class);
    private final AiConfigResolver aiConfigResolver;

    public AiConversationService(AiConfigResolver aiConfigResolver) {
        this.aiConfigResolver = aiConfigResolver;
    }

    /**
     * 開始新對話：AI 選定主題，提出第一個問題
     */
    public Mono<Map<String, Object>> startConversation(User user, int grade, String difficulty, List<String> learnedWords) {
        logger.info("Starting conversation for user: {}, grade: {}, difficulty: {}", user.getUsername(), grade, difficulty);
        
        String topicPool = buildTopicPool(learnedWords);
        String systemPrompt = buildSystemPrompt(grade, difficulty);
        
        // 根據年級給予不同的主題傾向引導
        String gradeTopicGuide = buildGradeTopicGuide(grade);
        
        String userPrompt = "請先選擇一個有趣的主題（從我學過的單字相關主題中挑選），然後用英文跟我打招呼並提出第一個簡單的問題。\n\n" +
            "我學過的單字主題範圍：" + (topicPool.isEmpty() ? gradeTopicGuide : topicPool) +
            "\n\n輸出格式：\n" +
            "TOPIC: [繁體中文主題名稱]\n" +
            "ENGLISH: [英文問候+問題，2-3句，使用國小程度單字]\n" +
            "CHINESE: [中文翻譯]\n" +
            "\n注意：\n" +
            "1. ENGLISH 只能用國小" + grade + "年級學生聽得懂的單字\n" +
            "2. 問題要開放式，讓我有機會用英文回答\n" +
            "3. 用繁體中文寫 TOPIC 和 CHINESE";

        return callAi(user, systemPrompt, userPrompt)
            .doOnNext(response -> logger.debug("AI start conversation response: {}", response))
            .doOnError(error -> logger.error("AI start conversation failed: {}", error.getMessage(), error))
            .map(this::parseStartResponse);
    }

    /**
     * 繼續對話：使用者回答後，AI 回應
     */
    public Mono<Map<String, Object>> continueConversation(User user, String topic, int grade, String difficulty,
                                                            List<Map<String, Object>> history, String userMessage) {
        String systemPrompt = buildSystemPrompt(grade, difficulty);

        // 組裝 messages
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.<String, Object>of("role", "system", "content", systemPrompt));

        for (Map<String, Object> h : history) {
            String role = "user".equals(h.get("role")) ? "user" : "assistant";
            String content = h.containsKey("content") ? (String) h.get("content") : "";
            messages.add(Map.<String, Object>of("role", role, "content", content));
        }

        messages.add(Map.<String, Object>of("role", "user", "content", userMessage));

        // 額外提示：要求 AI 在回應中評價使用者的回答
        String evaluationPrompt = "\n\n請用以下格式回應：\n" +
            "ENGLISH: [用英文回應，先稱讚或修正，再繼續下一個問題。只用國小程度單字]\n" +
            "CHINESE: [中文翻譯]\n" +
            "HINT: [如果使用者回答有錯誤，簡短提示正確用法；如果很好，給予鼓勵。用繁體中文]\n" +
            "\n注意：\n" +
            "1. 保持對話在主題「" + topic + "」上\n" +
            "2. 每次回應要簡短，2-4句英文就好\n" +
            "3. 循序漸進增加一點點難度";

        Map<String, Object> lastMsg = new HashMap<>(messages.get(messages.size() - 1));
        lastMsg.put("content", userMessage + evaluationPrompt);
        messages.set(messages.size() - 1, lastMsg);

        return callAiRaw(user, messages)
            .map(this::parseContinueResponse);
    }

    // ========== AI 呼叫 ==========

    private Mono<String> callAi(User user, String systemPrompt, String userPrompt) {
        List<Map<String, Object>> messages = List.of(
            Map.<String, Object>of("role", "system", "content", systemPrompt),
            Map.<String, Object>of("role", "user", "content", userPrompt)
        );
        return callAiRaw(user, messages);
    }

    private Mono<String> callAiRaw(User user, List<Map<String, Object>> messages) {
        // 解析 AI 配置：如果系統強制預設，或使用者未設定，自動填入
        aiConfigResolver.resolve(user);
        
        String provider = user.getApiProvider() != null ? user.getApiProvider().trim().toLowerCase() : "";
        String apiKey = user.getApiKey() != null ? user.getApiKey().trim() : "";
        String model = user.getApiModel();
        
        logger.info("Calling AI provider: {}, model: {}", provider, model);

        return switch (provider) {
            case "openrouter" -> callOpenRouter(apiKey, model, messages);
            case "gemini"     -> callGemini(apiKey, model, messages);
            case "openai"     -> callOpenAI(apiKey, model, messages);
            case "github"     -> callGitHubCopilot(apiKey, model, messages);
            case "ollama"     -> callOllama(apiKey, model, messages);
            default           -> Mono.error(new RuntimeException("不支援的供應商: " + provider));
        };
    }

    private Mono<String> callOpenRouter(String key, String model, List<Map<String, Object>> messages) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "moonshotai/kimi-k2-6-free";
        logger.debug("Calling OpenRouter with model: {}", chosenModel);
        
        WebClient client = WebClient.builder()
            .baseUrl("https://openrouter.ai/api/v1")
            .defaultHeader("Authorization", "Bearer " + key)
            .defaultHeader("HTTP-Referer", "https://primary-english.local")
            .defaultHeader("X-Title", "Primary English")
            .build();

        Map<String, Object> body = Map.of(
            "model", chosenModel,
            "messages", messages,
            "temperature", 0.85,
            "max_tokens", 1500
        );

        return client.post()
            .uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("OpenRouter response: {}", response))
            .doOnError(error -> logger.error("OpenRouter API error: {}", error.getMessage(), error))
            .map(this::extractContent);
    }

    private Mono<String> callGemini(String key, String model, List<Map<String, Object>> messages) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "gemini-2.0-flash-lite-001";
        logger.debug("Calling Gemini with model: {}", chosenModel);

        // Gemini 不支援 system role，合併到第一條 user
        StringBuilder combined = new StringBuilder();
        for (Map<String, Object> m : messages) {
            String role = (String) m.get("role");
            String content = (String) m.get("content");
            if ("system".equals(role)) {
                combined.append("系統指令：").append(content).append("\n\n");
            } else {
                combined.append(content).append("\n\n");
            }
        }

        WebClient client = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

        Map<String, Object> content = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", combined.toString().trim())))
            ),
            "generationConfig", Map.of(
                "temperature", 0.85,
                "maxOutputTokens", 1500
            )
        );

        return client.post()
            .uri("/v1beta/models/" + chosenModel + ":generateContent?key=" + key)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(content)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("Gemini response: {}", response))
            .doOnError(error -> logger.error("Gemini API error: {}", error.getMessage(), error))
            .map(this::extractGeminiContent);
    }

    private Mono<String> callOpenAI(String key, String model, List<Map<String, Object>> messages) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "gpt-4o-mini";
        logger.debug("Calling OpenAI with model: {}", chosenModel);
        
        WebClient client = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization", "Bearer " + key)
            .build();

        Map<String, Object> body = Map.of(
            "model", chosenModel,
            "messages", messages,
            "temperature", 0.85,
            "max_tokens", 1500
        );

        return client.post()
            .uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("OpenAI response: {}", response))
            .doOnError(error -> logger.error("OpenAI API error: {}", error.getMessage(), error))
            .map(this::extractContent);
    }

    private Mono<String> callGitHubCopilot(String key, String model, List<Map<String, Object>> messages) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "gpt-4o";
        logger.debug("Calling GitHub Copilot with model: {}", chosenModel);
        
        WebClient client = WebClient.builder()
            .baseUrl("https://api.githubcopilot.com")
            .defaultHeader("Authorization", "Bearer " + key)
            .defaultHeader("Editor-Version", "vscode/1.96.0")
            .build();

        Map<String, Object> body = Map.of(
            "model", chosenModel,
            "messages", messages,
            "temperature", 0.85,
            "max_tokens", 1500
        );

        return client.post()
            .uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("GitHub Copilot response: {}", response))
            .doOnError(error -> logger.error("GitHub Copilot API error: {}", error.getMessage(), error))
            .map(this::extractContent);
    }

    @SuppressWarnings("unchecked")
    private Mono<String> callOllama(String key, String model, List<Map<String, Object>> messages) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "gemma4:e2b";
        String baseUrl = (key != null && !key.isBlank() && key.startsWith("http")) ? key.trim() : "http://10.0.0.186:11434";
        logger.debug("Calling Ollama at {} with model: {}", baseUrl, chosenModel);

        WebClient client = WebClient.builder()
            .baseUrl(baseUrl + "/api")
            .build();

        Map<String, Object> body = Map.of(
            "model", chosenModel,
            "messages", messages,
            "stream", false,
            "options", Map.of("temperature", 0.85)
        );
       
        return client.post()
            .uri("/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("Ollama response: {}", response))
            .doOnError(error -> logger.error("Ollama API error: {}", error.getMessage(), error))
            .map(r -> {
                Object msg = r.get("message");
                if (msg instanceof Map) {
                    Object content = ((Map<String, Object>) msg).get("content");
                    if (content != null) return content.toString();
                }
                Object content = r.get("response");
                if (content != null) return content.toString();
                throw new RuntimeException("Ollama 回傳格式錯誤: " + r);
            });
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message != null) return (String) message.get("content");
        }
        return String.valueOf(response.get("content") != null ? response.get("content") : response);
    }

    @SuppressWarnings("unchecked")
    private String extractGeminiContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                if (content != null) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            throw new RuntimeException("Invalid Gemini response format: 'text' field not found.");
        } catch (ClassCastException | NullPointerException e) {
            throw new RuntimeException("Gemini response parsing error: " + e.getMessage(), e);
        }
    }

    // ========== 提示詞 ==========

    private String buildSystemPrompt(int grade, String difficulty) {
        String diffDesc = switch (difficulty) {
            case "easy"   -> "非常基礎，只用最簡單的國小單字和短句";
            case "hard"   -> "稍微有挑戰，可用複合句，但仍是國小程度";
            default       -> "中等難度，適合國小學生理解";
        };

        return "你是一位親切、有耐心的台灣國小英文老師。你的任務是透過輕鬆的英文對話，幫助國小" + grade + "年級學生練習英文口說。\n\n" +
            "難度要求：" + diffDesc + "\n\n" +
            "對話原則：\n" +
            "1. 主題由你決定，選擇和學生生活相關、有趣的主題\n" +
            "2. 盡量使用學生已學過的單字\n" +
            "3. 每次只說 2-4 句英文，不要太長\n" +
            "4. 先稱讚學生的回答，再溫柔地修正錯誤（如果有的話）\n" +
            "5. 問開放式問題，讓學生有機會用完整句子回答\n" +
            "6. 語氣要像在和小朋友聊天，親切自然\n" +
            "7. 如果學生回答得很好，給予具體的稱讚\n" +
            "8. 回應後要繼續問下一個相關問題，保持對話流暢";
    }

    /**
     * 根據年級提供不同的主題建議，避免 AI 總是選擇「動物」
     */
    private String buildGradeTopicGuide(int grade) {
        return switch (grade) {
            case 3 -> "國小三四年級基礎主題（請優先選擇）：數字、顏色、動物、水果、家庭成員、基本動作（跑跳走）、情緒表情";
            case 4 -> "國小四年級主題（請優先選擇）：學校生活、食物飲料、交通工具、日常活動、天氣季節、身體部位、衣服配件";
            case 5 -> "國小五年級主題（請優先選擇）：運動比賽、旅遊景點、節日活動、購物場所、職業工作、自然環境、健康衛生";
            case 6 -> "國小六年級主題（請優先選擇）：未來夢想、科技產品、環保議題、社會公德、文化差異、故事創作、團隊合作";
            default -> "國小基礎英文（數字、顏色、動物、食物、家庭、學校、天氣、運動...）";
        };
    }

    /**
     * 根據已學單字，推測可能的主題池
     */
    private String buildTopicPool(List<String> learnedWords) {
        if (learnedWords == null || learnedWords.isEmpty()) {
            return "";
        }
        // 簡單歸類
        Map<String, List<String>> topics = new LinkedHashMap<>();
        topics.put("動物", new ArrayList<>());
        topics.put("食物", new ArrayList<>());
        topics.put("家庭", new ArrayList<>());
        topics.put("學校", new ArrayList<>());
        topics.put("顏色", new ArrayList<>());
        topics.put("數字", new ArrayList<>());
        topics.put("天氣", new ArrayList<>());
        topics.put("運動", new ArrayList<>());
        topics.put("身體", new ArrayList<>());
        topics.put("衣服", new ArrayList<>());

        for (String w : learnedWords) {
            String wl = w.toLowerCase();
            if (List.of("dog","cat","bird","fish","rabbit","elephant","lion","tiger","bear","monkey","panda","duck","chicken","pig","cow","horse","sheep","snake","frog","mouse","bee","butterfly").contains(wl)) topics.get("動物").add(w);
            else if (List.of("apple","banana","rice","bread","noodle","egg","milk","water","juice","cake","candy","chocolate","pizza","hamburger","ice cream","meat","fish","chicken","soup","tea","coffee").contains(wl)) topics.get("食物").add(w);
            else if (List.of("mother","father","sister","brother","grandmother","grandfather","family","parent","baby","uncle","aunt","cousin").contains(wl)) topics.get("家庭").add(w);
            else if (List.of("teacher","student","classroom","school","book","pencil","pen","eraser","ruler","bag","desk","chair","blackboard","homework","class","lesson","subject","math","english","chinese","science","music","art","sport").contains(wl)) topics.get("學校").add(w);
            else if (List.of("red","blue","green","yellow","orange","purple","pink","black","white","brown","gray","gold","silver").contains(wl)) topics.get("顏色").add(w);
            else if (List.of("one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","twenty","thirty","hundred","first","second","third").contains(wl)) topics.get("數字").add(w);
            else if (List.of("sunny","rainy","cloudy","windy","snowy","hot","cold","warm","cool","weather","sun","rain","snow","wind","cloud","storm","fog").contains(wl)) topics.get("天氣").add(w);
            else if (List.of("run","jump","swim","dance","sing","draw","play","ball","basketball","football","baseball","game","sport","exercise","walk","ride","skate","ski").contains(wl)) topics.get("運動").add(w);
            else if (List.of("head","eye","ear","nose","mouth","face","hair","hand","arm","leg","foot","body","tooth","finger","toe","neck","shoulder","knee").contains(wl)) topics.get("身體").add(w);
            else if (List.of("shirt","pants","dress","skirt","shoes","socks","hat","cap","coat","jacket","sweater","uniform","gloves","scarf","boots","shorts","t-shirt").contains(wl)) topics.get("衣服").add(w);
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : topics.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                if (sb.length() > 0) sb.append("、");
                sb.append(entry.getKey()).append("(").append(entry.getValue().size()).append("個)");
            }
        }
        return sb.toString();
    }

    // ========== 解析回應 ==========

    private Map<String, Object> parseStartResponse(String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("topic", extractBlock(content, "TOPIC"));
        result.put("english", extractBlock(content, "ENGLISH"));
        result.put("chinese", extractBlock(content, "CHINESE"));
        result.put("raw", content);
        return result;
    }

    private Map<String, Object> parseContinueResponse(String content) {
        Map<String, Object> result = new HashMap<>();
        result.put("english", extractBlock(content, "ENGLISH"));
        result.put("chinese", extractBlock(content, "CHINESE"));
        result.put("hint", extractBlock(content, "HINT"));
        result.put("raw", content);
        return result;
    }

    private String extractBlock(String content, String tag) {
        // 嘗試匹配標準格式：TAG: 內容（下一個標籤前結束，支援單行或換行）
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            tag + "\\s*[:：]\\s*(.*?)(?=\\s+[A-Z_]+[:：]|\\n+[A-Z_]+[:：]|$)", java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(content);
        if (m.find()) return m.group(1).trim();
        // 嘗試無冒號格式
        p = java.util.regex.Pattern.compile(
            tag + "\\s*\\n(.*?)(?=\\s+[A-Z_]+[:：]|\\n+[A-Z_]+[:：]|$)", java.util.regex.Pattern.DOTALL);
        m = p.matcher(content);
        if (m.find()) return m.group(1).trim();
        return "";
    }
}
