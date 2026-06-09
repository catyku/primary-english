package com.primaryenglish.controller;

import com.primaryenglish.entity.Category;
import com.primaryenglish.repository.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final CategoryRepository categoryRepository;

    public HomeController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        model.addAttribute("categories", categories);
        return "index";
    }
}
