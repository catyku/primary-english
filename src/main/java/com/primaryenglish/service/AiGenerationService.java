package com.primaryenglish.service;

import com.primaryenglish.entity.Article;
import com.primaryenglish.entity.User;
import com.primaryenglish.entity.ReadingQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(AiGenerationService.class);

    public Mono<Article> generateArticle(User user, String prompt, int grade, String difficulty) {
        logger.info("Starting article generation - provider: {}, model: {}, grade: {}, difficulty: {}", 
            user != null ? user.getApiProvider() : "null", 
            user != null ? user.getApiModel() : "null", 
            grade, difficulty);
        
        if (user == null || !Boolean.TRUE.equals(user.getApiEnabled())) {
            logger.warn("Article generation failed: user is null or API not enabled");
            return Mono.error(new RuntimeException("請先在個人資料中設定 AI API Key"));
        }
        if (user.getApiProvider() == null || user.getApiKey() == null) {
            logger.warn("Article generation failed: provider or API key not set");
            return Mono.error(new RuntimeException("API 供應商或金鑰未設定"));
        }

        String provider = user.getApiProvider().trim().toLowerCase();
        String apiKey = user.getApiKey().trim();
        String model = user.getApiModel();

        String systemPrompt = buildSystemPrompt(grade, difficulty);
        logger.debug("System prompt built for grade {}: {}", grade, systemPrompt.substring(0, Math.min(100, systemPrompt.length())));

        return switch (provider) {
            case "openrouter" -> callOpenRouter(apiKey, model, systemPrompt, prompt, difficulty);
            case "gemini"     -> callGemini(apiKey, model, systemPrompt, prompt, difficulty);
            case "openai"     -> callOpenAI(apiKey, model, systemPrompt, prompt, difficulty);
            case "github"     -> callGitHubCopilot(apiKey, model, systemPrompt, prompt, difficulty);
            case "ollama"     -> callOllama(apiKey, model, systemPrompt, prompt, difficulty);
            default           -> {
                logger.error("Unsupported provider: {}", provider);
                yield Mono.error(new RuntimeException("不支援的供應商: " + provider));
            }
        };
    }

    // ========== OpenRouter ==========
    private Mono<Article> callOpenRouter(String key, String model, String system, String prompt, String difficulty) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "moonshotai/kimi-k2-6-free";
        logger.info("Calling OpenRouter API with model: {}", chosenModel);

        WebClient client = WebClient.builder()
            .baseUrl("https://openrouter.ai/api/v1")
            .defaultHeader("Authorization", "Bearer " + key)
            .defaultHeader("HTTP-Referer", "https://primary-english.local")
            .defaultHeader("X-Title", "Primary English")
            .build();

        Map<String, Object> body = Map.of(
            "model", chosenModel,
            "messages", List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.8,
            "max_tokens", 2000
        );

        return client.post()
            .uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("OpenRouter response received: {}", response))
            .doOnError(error -> logger.error("OpenRouter API call failed: {}", error.getMessage(), error))
            .map(r -> parseResponse(extractContent(r), difficulty));
    }

    // ========== Gemini ==========
    @SuppressWarnings("unchecked")
    private String extractGeminiContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("Missing 'candidates' in Gemini response");
            }
            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            if (content == null) {
                throw new RuntimeException("Missing 'content' in Gemini candidate");
            }
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("Missing 'parts' in Gemini content");
            }
            Map<String, Object> textPart = parts.get(0);
            String text = (String) textPart.get("text");
            if (text == null) {
                throw new RuntimeException("Missing 'text' in Gemini part");
            }
            return text;
        } catch (ClassCastException e) {
            throw new RuntimeException("Invalid Gemini response structure: " + e.getMessage());
        }
    }

    private Mono<Article> callGemini(String key, String model, String system, String prompt, String difficulty) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "gemini-2.0-flash-lite-001";
        logger.info("Calling Gemini API with model: {}", chosenModel);

        WebClient client = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

        Map<String, Object> content = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", system + "\n\n使用者主題：" + prompt)
                ))
            ),
            "generationConfig", Map.of(
                "temperature", 0.8,
                "maxOutputTokens", 2000
            )
        );

        return client.post()
            .uri("/v1beta/models/" + chosenModel + ":generateContent?key=" + key)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(content)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("Gemini response received: {}", response))
            .doOnError(error -> logger.error("Gemini API call failed: {}", error.getMessage(), error))
            .map(r -> parseResponse(extractGeminiContent(r), difficulty));
    }

    // ========== OpenAI ==========
    private Mono<Article> callOpenAI(String key, String model, String system, String prompt, String difficulty) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "gpt-4o-mini";
        logger.info("Calling OpenAI API with model: {}", chosenModel);

        WebClient client = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization", "Bearer " + key)
            .build();

        Map<String, Object> body = Map.of(
            "model", chosenModel,
            "messages", List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.8,
            "max_tokens", 2000
        );

        return client.post()
            .uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("OpenAI response received: {}", response))
            .doOnError(error -> logger.error("OpenAI API call failed: {}", error.getMessage(), error))
            .map(r -> parseResponse(extractContent(r), difficulty));
    }

    // ========== GitHub Copilot ==========
    private Mono<Article> callGitHubCopilot(String key, String model, String system, String prompt, String difficulty) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "gpt-4o";
        logger.info("Calling GitHub Copilot API with model: {}", chosenModel);

        WebClient client = WebClient.builder()
            .baseUrl("https://api.githubcopilot.com")
            .defaultHeader("Authorization", "Bearer " + key)
            .defaultHeader("Editor-Version", "vscode/1.96.0")
            .build();

        Map<String, Object> body = Map.of(
            "model", chosenModel,
            "messages", List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.8,
            "max_tokens", 2000
        );

        return client.post()
            .uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("GitHub Copilot response received: {}", response))
            .doOnError(error -> logger.error("GitHub Copilot API call failed: {}", error.getMessage(), error))
            .map(r -> parseResponse(extractContent(r), difficulty));
    }

    // ========== 通用解析 ==========
    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        logger.debug("Extracting content from response: {}", response);
        // OpenAI/OpenRouter format
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message != null) {
                String content = (String) message.get("content");
                logger.debug("Extracted content from choices[0].message.content");
                return content;
            }
        }
        // Fallback
        Object content = response.get("content");
        if (content != null) {
            logger.debug("Extracted content from response.content");
            return String.valueOf(content);
        }
        logger.warn("No content found in response, returning full response as string");
        return String.valueOf(response);
    }

    // ========== 提示詞 ==========
    private String buildSystemPrompt(int grade, String difficulty) {
        String diffDesc = switch (difficulty) {
            case "easy"   -> "非常基礎，只用國小程度的單字和簡單句型";
            case "hard"   -> "稍微有挑戰，使用一些複合句和稍難單字";
            default       -> "中等難度，適合國小學生閱讀";
        };

        return "你是一位專門為台灣國小" + grade + "年級學生編寫英文閱讀教材的老師。"
            + "請根據使用者提供的主題，生成一篇英文短文和 5 題選擇題。"
            + "\n\n難度要求：" + diffDesc
            + "\n\n輸出格式（嚴格遵守，使用繁體中文）："
            + "\nTITLE: [文章標題（英文）]"
            + "\nTITLE_CN: [標題中文翻譯]"
            + "\nCONTENT: [英文短文內容，約 100-200 字]"
            + "\nTRANSLATION: [中文翻譯]"
            + "\n\nQUESTION_1:"
            + "\n[題目文字]"
            + "\nA) [選項A]"
            + "\nB) [選項B]"
            + "\nC) [選項C]"
            + "\nD) [選項D]"
            + "\nANSWER: [A/B/C/D]"
            + "\nEXPLANATION: [為什麼選這個答案的簡短說明]"
            + "\n\n（依此類推 QUESTION_2 到 QUESTION_5）"
            + "\n\n注意："
            + "1. 選項必須以 A) B) C) D) 開頭"
            + "2. ANSWER 後面必須是大寫 A/B/C/D"
            + "3. EXPLANATION 用繁體中文說明"
            + "4. 題目要測驗理解力，不是單純背單字";
    }

    // ========== 解析 AI 回傳為 Article ==========
    private Article parseResponse(String content, String difficulty) {
        Article article = new Article();
        article.setTitle(extractBlock(content, "TITLE"));
        article.setTitleCn(extractBlock(content, "TITLE_CN"));
        article.setContent(extractBlock(content, "CONTENT"));
        article.setContentCn(extractBlock(content, "TRANSLATION"));
        article.setLevel(difficulty);
        article.setTimeLimit(5);
        if (article.getContent() != null) {
            article.setWordCount(article.getContent().trim().split("\\s+").length);
        }

        // Parse questions
        for (int i = 1; i <= 5; i++) {
            String qBlock = extractBlock(content, "QUESTION_" + i);
            if (!qBlock.isEmpty()) {
                ReadingQuestion q = parseQuestion(qBlock, i);
                article.addQuestion(q);
            }
        }
        return article;
    }

    private String extractBlock(String content, String tag) {
        Pattern p = Pattern.compile(tag + "\\s*[:：]\\s*(.*?)(?=\\n\\n[A-Z_]+[:：]|$)", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if (m.find()) return m.group(1).trim();
        // Try without colon
        p = Pattern.compile(tag + "\\s*\\n(.*?)(?=\\n[A-Z_]+[:：]|$)", Pattern.DOTALL);
        m = p.matcher(content);
        if (m.find()) return m.group(1).trim();
        return "";
    }

    private ReadingQuestion parseQuestion(String block, int orderNum) {
        ReadingQuestion q = new ReadingQuestion();
        q.setOrderNum(orderNum);

        String[] lines = block.split("\\n");
        StringBuilder questionText = new StringBuilder();
        int optStart = -1;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.matches("^[AaBbCcDd]\\).*")) {
                if (optStart < 0) optStart = i;
                continue;
            }
            if (line.matches("^ANSWER\\s*[:：]")) break;
            if (line.matches("^EXPLANATION\\s*[:：]")) break;
            if (!line.isEmpty()) {
                if (questionText.length() > 0) questionText.append(" ");
                questionText.append(line);
            }
        }
        q.setQuestion(questionText.toString().trim());

        // Extract options
        if (optStart >= 0) {
            for (int i = optStart; i < lines.length && i < optStart + 4; i++) {
                String line = lines[i].trim();
                if (line.matches("^[Aa]\\).*")) q.setOptionA(line.replaceFirst("^[Aa]\\)", "").trim());
                else if (line.matches("^[Bb]\\).*")) q.setOptionB(line.replaceFirst("^[Bb]\\)", "").trim());
                else if (line.matches("^[Cc]\\).*")) q.setOptionC(line.replaceFirst("^[Cc]\\)", "").trim());
                else if (line.matches("^[Dd]\\).*")) q.setOptionD(line.replaceFirst("^[Dd]\\)", "").trim());
            }
        }

        // Extract answer
        Pattern ansPattern = Pattern.compile("ANSWER\\s*[:：]\\s*([A-Da-d])");
        Matcher ansMatcher = ansPattern.matcher(block);
        if (ansMatcher.find()) {
            q.setCorrectAnswer(ansMatcher.group(1).toUpperCase());
        } else {
            q.setCorrectAnswer("A");
        }

        // Extract explanation
        Pattern expPattern = Pattern.compile("EXPLANATION\\s*[:：]\\s*(.*)", Pattern.DOTALL);
        Matcher expMatcher = expPattern.matcher(block);
        if (expMatcher.find()) {
            q.setExplanation(expMatcher.group(1).trim());
        }

        return q;
    }

    // ========== Ollama ==========
    @SuppressWarnings("unchecked")
    private String extractOllamaContent(Map<String, Object> response) {
        try {
            Object msg = response.get("message");
            if (msg instanceof Map) {
                Object content = ((Map<String, Object>) msg).get("content");
                if (content != null) {
                    return content.toString();
                }
            }
            // Fallback for /api/generate format
            Object content = response.get("response");
            if (content != null) {
                return content.toString();
            }
            throw new RuntimeException("Ollama 回傳格式錯誤: " + response);
        } catch (ClassCastException e) {
            throw new RuntimeException("Invalid Ollama response structure: " + e.getMessage());
        }
    }

    private Mono<Article> callOllama(String key, String model, String system, String prompt, String difficulty) {
        String chosenModel = (model != null && !model.isBlank()) ? model : "gemma4:e2b";
        String baseUrl = (key != null && !key.isBlank() && key.startsWith("http")) ? key.trim() : "http://10.0.0.186:11434";
        logger.info("Calling Ollama API at {} with model: {}", baseUrl, chosenModel);

        WebClient client = WebClient.builder()
            .baseUrl(baseUrl + "/api")
            .build();

        Map<String, Object> body = Map.of(
            "model", chosenModel,
            "messages", List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", prompt)
            ),
            "stream", false,
            "options", Map.of("temperature", 0.8)
        );

        return client.post()
            .uri("/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .doOnNext(response -> logger.debug("Ollama response received: {}", response))
            .doOnError(error -> logger.error("Ollama API call failed: {}", error.getMessage(), error))
            .map(r -> parseResponse(extractOllamaContent(r), difficulty));
    }

    // ========== 預設模型列表 ==========
    public static List<Map<String, String>> getProviders() {
        return List.of(
            Map.of("id", "ollama",   "name", "🦙 Ollama (本地)",    "defaultModel", "gemma4:e2b",
                   "url", "http://10.0.0.186:11434"),
            Map.of("id", "openrouter", "name", "🌐 OpenRouter", "defaultModel", "moonshotai/kimi-k2-6-free",
                   "url", "https://openrouter.ai/keys"),
            Map.of("id", "gemini", "name", "🔷 Google Gemini", "defaultModel", "gemini-2.0-flash-lite-001",
                   "url", "https://aistudio.google.com/app/apikey"),
            Map.of("id", "openai", "name", "🤖 OpenAI", "defaultModel", "gpt-4o-mini",
                   "url", "https://platform.openai.com/api-keys"),
            Map.of("id", "github", "name", "🐙 GitHub Copilot", "defaultModel", "gpt-4o",
                   "url", "https://github.com/settings/copilot")
        );
    }
}
