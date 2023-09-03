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

import java.util.UUID;

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


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/registration")
    public String getRegistration() {

        return "registration";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/registration")
    public String registration(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               Model model){


        Iterable<User> users = userRepository.findAll();

        for (User user : users) {

            if (user.getEmail().equals(email)) {             // проверим, есть ли в базе такой эмаил.
                String message = "Пользователь с такой почтой уже есть.";

                model.addAttribute("message", message);
                return "registration";                  // вернём пользователю ту же страницу, только с сообщением, что такое уже занято
            }
        }


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);  // для хранения в базе, пароль должен быть закодирован
        String hashedPassword = passwordEncoder.encode(password);

        String role = "ADMIN";
        String status = "ACTIVE";

        String uuid = UUID.randomUUID().toString();                     // создаём уникальный номер, для активации

        User user = new User(email, hashedPassword, firstName, lastName, uuid, role, status);
        userRepository.save(user);

        // так почему-то не работает
        //MailController mailController = new MailController();       // вызываем метод отправки писем из контроллера по обслуживанию писем
        //mailController.sendEmail();


        return "redirect:/";                         // при успешной добавке просто переведём на стартовую страницу. !!!!! тестово !!!!!
    }


}
