package ru.pufr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.pufr.models.User;
import ru.pufr.repo.UserRepository;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/enter")
    public String adminHome(Model model) {
        return "adminkaAdmin";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/users")
    public String usersList (Model model){
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users-list";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/users/add")
    public String userListAdd(@RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String role,
                              @RequestParam String status,
                              Model model){



        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);  // для хранения в базе, пароль должен быть закодирован
        String hashedPassword = passwordEncoder.encode(password);

        String str = new String();          // !!!!!! тестовый код !!!! проверка добавления новой колонки в базу

        User user = new User(email, password, firstName, lastName, str, role, status);
        userRepository.save(user);
        return "redirect:/users";
    }

}
