package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cars.dto.HistoryOwnersDto;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.dto.SearchDto;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.post.PostService;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final EngineService engineService;

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
    public String getPostById(@PathVariable("id") Integer id, Model model, HttpSession session) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Not Found.");
            return "errors/404";
        }
        var post = optionalPost.get();
        User currentUser = (User) session.getAttribute("user");
        Integer userIdByPost = postService.getUserIdByPostId(post.getId());
        var priseHistories = postService.getSortedPriceHistories(post.getPriceHistories());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userIdByPost", userIdByPost);
        model.addAttribute("price", priseHistories.get(priseHistories.size() - 1).getAfter());
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
        var isUpdated = postService.updateFiles(optionalPost.get(), files);
        return isUpdatedPost(attributes, isUpdated, id);
    }

    @PostMapping("/updateHistoryOwners/{id}")
    public String updateHistoryOwners(Model model,
                                      @PathVariable("id") Integer id,
                                      RedirectAttributes attributes,
                                      @ModelAttribute HistoryOwnersDto historyOwnersDto) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Not Found.");
            return "errors/404";
        }
        var isUpdated = postService.updateHistoryOwners(optionalPost.get(), historyOwnersDto);
        return isUpdatedPost(attributes, isUpdated, id);
    }

    @PostMapping("/updatePriceHistory/{id}")
    public String updatePriceHistory(Model model,
                                     @PathVariable("id") Integer id,
                                     RedirectAttributes attributes,
                                     @RequestParam("price") Long price) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Not Found.");
            return "errors/404";
        }
        var isUpdated = postService.updatePriceHistory(optionalPost.get(), price);
        return isUpdatedPost(attributes, isUpdated, id);
    }

    @PostMapping("/updateDescription/{id}")
    public String updateDescription(Model model,
                                    @PathVariable("id") Integer id,
                                    RedirectAttributes attributes,
                                    @RequestParam("description") String description) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Not Found.");
            return "errors/404";
        }
        var isUpdated = postService.updateDescription(optionalPost.get(), description);
        return isUpdatedPost(attributes, isUpdated, id);
    }

    @GetMapping("/updateStatus/{id}")
    public String updateStatus(Model model, @PathVariable("id") Integer id, RedirectAttributes attributes) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Not Found.");
            return "errors/404";
        }
        var isUpdated = postService.updateStatus(optionalPost.get());
        return isUpdatedPost(attributes, isUpdated, id);
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Integer id, Model model) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Not Found.");
            return "errors/404";
        }
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
