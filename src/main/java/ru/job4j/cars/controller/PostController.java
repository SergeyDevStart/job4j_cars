package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.dto.SearchDto;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.file.FileService;
import ru.job4j.cars.service.owner.OwnerService;
import ru.job4j.cars.service.post.PostService;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final EngineService engineService;
    private final FileService fileService;
    private final OwnerService ownerService;

    @GetMapping
    public String getPosts() {
        return "index";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("posts", postService.getPostCardDtoList(postService.findAll()));
        return "posts/list";
    }

    @GetMapping("/lastDay")
    public String getPostForTheLastDay(Model model) {
        model.addAttribute("posts", postService.getPostCardDtoList(postService.findAllLastDay()));
        return "posts/list";
    }

    @GetMapping("withFile")
    public String getPostWithFIle(Model model) {
        model.addAttribute("posts", postService.getPostCardDtoList(postService.findPostsWithFile()));
        return "posts/list";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        var ownerName = ownerService.getOwnerNameIfExist(user.getId());
        model.addAttribute("ownerName", ownerName);
        model.addAttribute("engines", engineService.findAll());
        model.addAttribute("categories", postService.getCategories());
        return "posts/create";
    }

    @PostMapping("/create")
    public String saveNewPost(@ModelAttribute PostCreateDto postDto,
                              @RequestParam("files") MultipartFile[] files,
                              HttpSession session,
                              Model model) {
        try {
            var user = (User) session.getAttribute("user");
            postDto.setUser(user);
            postService.create(postDto, files);
        } catch (Exception e) {
            model.addAttribute("error", "Не удалось создать объявление.");
            return "errors/404";
        }
        return "redirect:/index";
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Integer id, Model model) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Not Found.");
            return "errors/404";
        }
        var post = optionalPost.get();
        model.addAttribute("files", fileService.findAllByPostId(post.getId()));
        model.addAttribute("price",
                post.getPriceHistories().get(post.getPriceHistories().size() - 1).getAfter());
        model.addAttribute("post", post);
        return "posts/detail";
    }

    @GetMapping("/categories")
    public String getCategoriesPage(Model model) {
        model.addAttribute("posts", postService.getPostCardDtoList(postService.findAll()));
        model.addAttribute("engines", engineService.findAll());
        model.addAttribute("categories", postService.getCategories());
        return "posts/categories";
    }

    @PostMapping("/search")
    public String getSearchResult(@ModelAttribute SearchDto searchDto, Model model) {
        model.addAttribute("posts", postService.getPostCardDtoList(postService.findSearchResult(searchDto)));
        model.addAttribute("engines", engineService.findAll());
        model.addAttribute("categories", postService.getCategories());
        return "posts/categories";
    }

    @GetMapping("/update/{id}")
    public String updatePost(Model model, @PathVariable("id") Integer id) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Объявление не найдено. ");
            return "errors/404";
        }
        model.addAttribute("post", optionalPost.get());
        return "posts/update";
    }

    @PostMapping("/updateFiles/{id}")
    public String updateFiles(Model model,
                              @PathVariable("id") Integer id,
                              @RequestParam("files") MultipartFile[] files,
                              RedirectAttributes attributes) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Объявление не найдено. ");
            return "errors/404";
        }
        var isUpdated = postService.updateFiles(id, files);
        return isUpdatedPost(attributes, isUpdated, id);
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Integer id, Model model) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Not Found.");
            return "errors/404";
        }
        var filesToDelete = fileService.findAllByPostId(optionalPost.get().getId());
        fileService.deleteFiles(filesToDelete);
        postService.delete(optionalPost.get());
        return "redirect:/posts/categories";
    }

    private String isUpdatedPost(RedirectAttributes attributes, boolean isUpdated, Integer id) {
        if (!isUpdated) {
            attributes.addFlashAttribute("message", "Не удалось обновить объявление.");
        } else {
            attributes.addFlashAttribute("message", "Объявление успешно обновлено");
        }
        return String.format("redirect:/posts/update/%s", id);
    }
}
