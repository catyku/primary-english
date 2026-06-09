package com.primaryenglish.controller;

import com.primaryenglish.entity.User;
import com.primaryenglish.service.UserService;
import com.primaryenglish.service.ProgressService;
import com.primaryenglish.service.QuizResultService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final ProgressService progressService;
    private final QuizResultService quizResultService;

    public UserController(UserService userService, ProgressService progressService,
                          QuizResultService quizResultService) {
        this.userService = userService;
        this.progressService = progressService;
        this.quizResultService = quizResultService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("USER_NAME") != null;
    }

    private String getCurrentUsername(HttpSession session) {
        return (String) session.getAttribute("USER_NAME");
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model,
                            @RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout) {
        if (isLoggedIn(session)) {
            return "redirect:/";
        }
        if (error != null) {
            model.addAttribute("errorMsg", "帳號或密碼錯誤");
        }
        if (logout != null) {
            model.addAttribute("success", "已登出");
        }
        return "login";
    }

    @PostMapping("/do-login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "請輸入帳號和密碼");
            return "redirect:/login";
        }

        String cleanName = username.trim();
        User user = userService.findByUsername(cleanName).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "帳號不存在，請先註冊");
            return "redirect:/login";
        }

        if (!userService.checkPassword(password, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "密碼錯誤");
            return "redirect:/login";
        }

        // 寫入 Session
        session.setAttribute("USER_NAME", user.getUsername());
        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("DISPLAY_NAME", user.getDisplayName());
        userService.updateLastLogin(user.getId());

        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (isLoggedIn(session)) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/do-register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam(required = false) String displayName,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (username == null || username.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "請輸入使用者名稱");
            return "redirect:/register";
        }
        if (password == null || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "請輸入密碼");
            return "redirect:/register";
        }
        if (password.length() < 4) {
            redirectAttributes.addFlashAttribute("error", "密碼至少 4 個字元");
            return "redirect:/register";
        }

        String cleanName = username.trim();
        String cleanDisplay = (displayName != null && !displayName.isBlank()) ? displayName.trim() : cleanName;

        // 檢查帳號是否已存在
        if (userService.findByUsername(cleanName).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "帳號已存在，請直接登入");
            return "redirect:/login";
        }

        User user = userService.register(cleanName, password, cleanDisplay);

        // 自動登入
        session.setAttribute("USER_NAME", user.getUsername());
        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("DISPLAY_NAME", user.getDisplayName());
        userService.updateLastLogin(user.getId());

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = getCurrentUsername(session);
        if (username == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        long learnedCount = progressService.getLearnedCount(user.getId());
        long totalQuizCount = quizResultService.getTotalQuizCount(user.getId());
        Double avgScore = quizResultService.getAverageScore(user.getId());
        List<Object[]> quizStats = quizResultService.getUserStats(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("learnedCount", learnedCount);
        model.addAttribute("totalQuizCount", totalQuizCount);
        model.addAttribute("avgScore", avgScore != null ? Math.round(avgScore) : 0);
        model.addAttribute("quizStats", quizStats != null ? quizStats : new ArrayList<>());
        model.addAttribute("quizResults", quizResultService.getUserResults(user.getId()));

        return "profile";
    }
}
