package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.file.FileService;
import ru.job4j.cars.service.post.PostService;

@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService hibernatePostService;
    private final EngineService hibernateEngineService;
    private final FileService hibernateFileService;

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
        model.addAttribute("valuesForCreate", hibernatePostService.getValuesForCreate());
        return "posts/create";
    }

    @PostMapping("/create")
    public String saveNewPost(@ModelAttribute PostCreateDto postDto, @RequestParam MultipartFile file, Model model) {
        try {
            var fileDto = hibernateFileService.getNewFileDto(file.getOriginalFilename(), file.getBytes());
            hibernatePostService.create(postDto, fileDto);
        } catch (Exception e) {
            model.addAttribute("message", "Не удалось создать объявление.");
            return "errors/error";
        }
        return "redirect:/index";
    }
}
