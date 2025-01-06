package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.participates.ParticipatesService;
import ru.job4j.cars.service.post.PostService;
import ru.job4j.cars.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final ParticipatesService participatesService;

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user) {
        var savedUser = userService.save(user);
        if (savedUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с таким логином уже существует.");
            return "users/register";
        }
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String login(Model model, @ModelAttribute User user, HttpServletRequest request) {
        var optionalUser = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (optionalUser.isEmpty()) {
            model.addAttribute("message", "Почта или пароль введены неверно.");
            return "users/login";
        }
        var session = request.getSession();
        session.setAttribute("user", optionalUser.get());
        return "redirect:/posts/myPosts";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    @GetMapping("/subscribe/{id}")
    public String subscribe(RedirectAttributes attributes, @PathVariable("id") Integer postId, HttpSession session) {
        var user = (User) session.getAttribute("user");
        var postOptional = postService.findById(postId);
        if (postOptional.isEmpty()) {
            attributes.addFlashAttribute("error", "Объявление не найдено. ");
            return "redirect:/errors/404";
        }
        var participates = participatesService.save(postOptional.get(), user);
        if (participates.isEmpty()) {
            attributes.addFlashAttribute("message", "Не удалось подписаться на объявление");
        } else {
            attributes.addFlashAttribute("message", "Вы успешно подписались на объявление");
        }
        return String.format("redirect:/posts/%s", postId);
    }
}
