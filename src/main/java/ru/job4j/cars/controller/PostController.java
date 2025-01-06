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
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.post.PostService;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

@Controller
@AllArgsConstructor
@RequestMapping(value = {"/", "/index", "/posts"})
public class PostController {
    private final PostService postService;
    private final EngineService engineService;

    @GetMapping
    public String getAll(Model model) {
        return showPosts(model, postService::findAll);
    }

    @GetMapping("/lastDay")
    public String getPostForTheLastDay(Model model) {
        return showPosts(model, postService::findAllLastDay);
    }

    @GetMapping("withFile")
    public String getPostWithFIle(Model model) {
        return showPosts(model, postService::findPostsWithFile);
    }

    @GetMapping("/myPosts")
    public String getMyPosts(Model model, HttpSession session) {
        User user = getCurrentUser(session);
        return showPosts(model, () -> postService.findAllByUserId(user.getId()));
    }

    @GetMapping("/mySubscriptions")
    public String getMySubscriptions(Model model, HttpSession session) {
        User user = getCurrentUser(session);
        return showPosts(model, () -> postService.findAllPostsBySubscriptions(user.getId()));
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("engines", engineService.findAll());
        model.addAttribute("categories", postService.getCategories());
        return "posts/create";
    }

    @PostMapping("/create")
    public String saveNewPost(@ModelAttribute PostCreateDto postDto,
                              @RequestParam("files") MultipartFile[] files,
                              HttpSession session,
                              Model model) {
        User user = getCurrentUser(session);
        postDto.setUser(user);
        var savedUser = postService.create(postDto, files);
        if (savedUser.isEmpty()) {
            model.addAttribute("error", "Не удалось создать пост.");
            return "errors/404";
        }
        return "redirect:/posts/myPosts";
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Integer id, Model model, HttpSession session) {
        var optionalPost = findPostOrHandlerError(id, model);
        if (optionalPost.isEmpty()) {
            return "errors/404";
        }
        var post = optionalPost.get();
        User currentUser = getCurrentUser(session);
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
        return showCategories(model, postService::findAll);
    }

    @PostMapping("/search")
    public String getSearchResult(@ModelAttribute SearchDto searchDto, Model model) {
        return showCategories(model, () -> postService.findSearchResult(searchDto));
    }

    @GetMapping("/update/{id}")
    public String updatePost(Model model, @PathVariable("id") Integer id) {
        var optionalPost = findPostOrHandlerError(id, model);
        if (optionalPost.isEmpty()) {
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
        var optionalPost = findPostOrHandlerError(id, model);
        if (optionalPost.isEmpty()) {
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
        var optionalPost = findPostOrHandlerError(id, model);
        if (optionalPost.isEmpty()) {
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
        var optionalPost = findPostOrHandlerError(id, model);
        if (optionalPost.isEmpty()) {
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
        var optionalPost = findPostOrHandlerError(id, model);
        if (optionalPost.isEmpty()) {
            return "errors/404";
        }
        var isUpdated = postService.updateDescription(optionalPost.get(), description);
        return isUpdatedPost(attributes, isUpdated, id);
    }

    @GetMapping("/updateStatus/{id}")
    public String updateStatus(Model model, @PathVariable("id") Integer id, RedirectAttributes attributes) {
        var optionalPost = findPostOrHandlerError(id, model);
        if (optionalPost.isEmpty()) {
            return "errors/404";
        }
        var isUpdated = postService.updateStatus(optionalPost.get());
        return isUpdatedPost(attributes, isUpdated, id);
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Integer id, Model model) {
        var optionalPost = findPostOrHandlerError(id, model);
        if (optionalPost.isEmpty()) {
            return "errors/404";
        }
        postService.delete(optionalPost.get());
        return "redirect:/posts/myPosts";
    }

    private String isUpdatedPost(RedirectAttributes attributes, boolean isUpdated, Integer id) {
        if (!isUpdated) {
            attributes.addFlashAttribute("message", "Не удалось обновить объявление.");
        } else {
            attributes.addFlashAttribute("message", "Объявление успешно обновлено");
        }
        return String.format("redirect:/posts/update/%s", id);
    }

    private Optional<Post> findPostOrHandlerError(Integer id, Model model) {
        var optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            model.addAttribute("error", "Unknown error");
        }
        return optionalPost;
    }

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    private String showPosts(Model model, Supplier<Collection<Post>> supplier) {
        model.addAttribute("posts", postService.getPostCardDtoList(supplier.get()));
        return "posts/list";
    }

    private String showCategories(Model model, Supplier<Collection<Post>> supplier) {
        model.addAttribute("posts", postService.getPostCardDtoList(supplier.get()));
        model.addAttribute("engines", engineService.findAll());
        model.addAttribute("categories", postService.getCategories());
        return "posts/categories";
    }
}
