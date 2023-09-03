package ru.pufr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.pufr.models.Status;
import ru.pufr.models.User;
import ru.pufr.repo.UserRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/enter")
    public String adminHome(Model model) {
        return "admin-page";
    }



    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/users")
    public String usersList (Model model){
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users-list";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/userAdd")
    public String  userAdd(Model model){
        return "user-add";
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

        String uuid = UUID.randomUUID().toString();                     // создаём уникальный номер, для активации

        User user = new User(email, hashedPassword, firstName, lastName, uuid, role, status);
        userRepository.save(user);

        return "redirect:/users";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/users/{id}")
    public String userDetales (@PathVariable(value = "id") long id, Model model) {

        if(!userRepository.existsById(id)){return "redirect:/users";}

        Optional<User> user = userRepository.findById(id);
        ArrayList<User> res = new ArrayList<>();
        user.ifPresent(res::add);
        model.addAttribute("user", res);

        return "user-details";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/user/edit/{id}")
    public String userUpdate(@PathVariable(value = "id") long id,  Model model) {

        if(!userRepository.existsById(id)){return "redirect:/users";}

        Optional<User> user = userRepository.findById(id);
        ArrayList<User> res = new ArrayList<>();
        user.ifPresent(res::add);
        model.addAttribute("user", res);

        return "user-edit";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @PostMapping("/user/edit/{id}")
    public String userUpdate(@PathVariable(value = "id") long id,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String role,
                              @RequestParam String status,
                              Model model){

        User user = userRepository.findById(id).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});

        String uuid = UUID.randomUUID().toString();                     // создаём уникальный номер, для активации

        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTestCell(uuid);
        user.setRole(role);
        user.setStatus(Status.valueOf(status));

        userRepository.save(user);

        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/user/delete/{id}")
    public String userDelete(@PathVariable(value = "id") long id, Model model) {

        User user = userRepository.findById(id).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});

        userRepository.delete(user);

        return "redirect:/users";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/user/resetPass/{id}")
    public String userResetPass(@PathVariable(value = "id") long id,  Model model) {

        if(!userRepository.existsById(id)){return "redirect:/users";}

        User user = userRepository.findById(id).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);  // для хранения в базе, пароль должен быть закодирован
        String hashedPassword = passwordEncoder.encode("password");

        user.setPassword(hashedPassword);

        userRepository.save(user);

        return "redirect:/users";
    }

}
