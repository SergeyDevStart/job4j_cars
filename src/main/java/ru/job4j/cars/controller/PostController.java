package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.dto.SearchDto;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.file.FileService;
import ru.job4j.cars.service.post.PostService;

import java.util.HashSet;
import java.util.Set;

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
        model.addAttribute("categories", hibernatePostService.getCategories());
        return "posts/create";
    }

    @PostMapping("/create")
    public String saveNewPost(@ModelAttribute PostCreateDto postDto, @RequestParam("files") MultipartFile[] files, Model model) {
        try {
            Set<FileDto> filesDtoSet = new HashSet<>();
            for (MultipartFile file : files) {
                var fileDto = hibernateFileService.getNewFileDto(file.getOriginalFilename(), file.getBytes());
                filesDtoSet.add(fileDto);
            }
            hibernatePostService.create(postDto, filesDtoSet);
        } catch (Exception e) {
            model.addAttribute("message", "Не удалось создать объявление.");
            return "errors/404";
        }
        return "redirect:/index";
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Integer id, Model model) {
        var optionalPost = hibernatePostService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("message", "Not Found.");
            return "errors/404";
        }
        var post = optionalPost.get();
        model.addAttribute("files", hibernatePostService.getSortedFiles(post.getFiles()));
        model.addAttribute("price",
                post.getPriceHistories().get(post.getPriceHistories().size() - 1).getAfter());
        model.addAttribute("post", post);
        return "posts/detail";
    }

    @GetMapping("/categories")
    public String getCategoriesPage(Model model) {
        model.addAttribute("posts", hibernatePostService.getPostCardDtoList(hibernatePostService.findAll()));
        model.addAttribute("engines", hibernateEngineService.findAll());
        model.addAttribute("categories", hibernatePostService.getCategories());
        return "posts/categories";
    }

    @PostMapping("/search")
    public String getSearchResult(@ModelAttribute SearchDto searchDto, Model model) {
        model.addAttribute("posts", hibernatePostService.getPostCardDtoList(hibernatePostService.findSearchResult(searchDto)));
        model.addAttribute("engines", hibernateEngineService.findAll());
        model.addAttribute("categories", hibernatePostService.getCategories());
        return "posts/categories";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Integer id, Model model) {
        var optionalPost = hibernatePostService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("message", "Not Found.");
            return "errors/404";
        }
        var filesToDelete = hibernatePostService.getSortedFiles(optionalPost.get().getFiles());
        hibernateFileService.deleteFiles(filesToDelete);
        hibernatePostService.delete(optionalPost.get());
        return "redirect:/posts/categories";
    }
}
