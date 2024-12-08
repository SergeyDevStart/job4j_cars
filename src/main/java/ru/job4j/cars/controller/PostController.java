package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.post.PostService;

@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService hibernatePostService;
    private final EngineService hibernateEngineService;

    @GetMapping
    public String getPosts() {
        return "index";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("posts", hibernatePostService.getPostCardDtoList(hibernatePostService.findAll()));
        return "posts/list";
    }

    @GetMapping("/lastDay")
    public String getPostForTheLastDay(Model model) {
        model.addAttribute("posts", hibernatePostService.getPostCardDtoList(hibernatePostService.findAllLastDay()));
        return "posts/list";
    }

    @GetMapping("withFile")
    public String getPostWithFIle(Model model) {
        model.addAttribute("posts", hibernatePostService.getPostCardDtoList(hibernatePostService.findPostsWithFile()));
        return "posts/list";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("engines", hibernateEngineService.findAll());
        return "posts/create";
    }

    @PostMapping("/create")
    public String saveNewPost(@ModelAttribute PostCreateDto dto, Model model) {
        var savedPost = hibernatePostService.create(dto);
        if (savedPost.isEmpty()) {
            model.addAttribute("message", "Не удалось создать объявление.");
            return "errors/error";
        }
        return "redirect:/index";
    }
}
