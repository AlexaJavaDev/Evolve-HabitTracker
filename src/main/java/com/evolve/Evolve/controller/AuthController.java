package com.evolve.Evolve.controller;

import com.evolve.Evolve.repository.UserRepository;
import com.evolve.Evolve.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        // Проверка на существующего пользователя
        if (userRepository.findByUsername(email).isPresent()) {
            model.addAttribute("error", "Пользователь с таким email уже зарегистрирован");
            return "register";
        }
        // Проверка пароля (для надёжности)
        if (password.length() < 8) {
            model.addAttribute("error", "Пароль должен содержать не менее 8 символов");
            return "register";
        }
        userService.registerUser(email, password);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
}
