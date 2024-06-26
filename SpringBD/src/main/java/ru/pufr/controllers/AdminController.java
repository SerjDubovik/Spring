package ru.pufr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.pufr.models.*;
import ru.pufr.repo.BotRepository;
import ru.pufr.repo.UserRepository;

import java.io.File;
import java.util.*;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BotRepository botRepository;

    private final String defaultPath = "/home/serj/proj/dir/";
    //private final String defaultPath = "/test/";


    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/enter")
    public String adminHome(Model model) {

        String email = getCurrentUsername();         // тут мы узнаём кто авторизовался. дальше нужно пробежатся по списку

        User myUser = userRepository.findByEmail(email).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});
        String myRole = String.valueOf(myUser.getRole());
        String myStatus = String.valueOf(myUser.getStatus());

        if(myRole.equals("ADMIN") && myStatus.equals("ACTIVE")){

            Optional<User> user = userRepository.findByEmail(email);
            ArrayList<User> nik = new ArrayList<>();
            user.ifPresent(nik::add);

            model.addAttribute("nik", nik);
            return "admin-page";
        }

        if(myRole.equals("USER") && myStatus.equals("ACTIVE")){
            Optional<User> user = userRepository.findByEmail(email);
            ArrayList<User> nik = new ArrayList<>();
            user.ifPresent(nik::add);

            model.addAttribute("nik", nik);
            return "work";
        }

        return "home";
    }


    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/testTable")
    public String testTable(Model model) {
        Iterable<BotTable> botTables = botRepository.findAll();
        model.addAttribute("botTables", botTables);
        return "users-list";
    }


    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/users")
    public String usersList (Model model){
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users-list";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/Lessons")
    public String Lessons (Model model){
        return "lessons";
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
                              @RequestParam String pathline,
                              @RequestParam String role,
                              @RequestParam String status,
                              Model model){

        //defaultPath
        File dir = new File(defaultPath);

        Map<Integer, String> folders = new TreeMap<>();
        Integer count = 0;

        if(dir.isDirectory()) {

            for(File item : dir.listFiles())        // получаем все подпапки в указаной папке
            {
                if(item.isDirectory())
                {
                    folders.put(count++, item.getName());
                }
            }
        }

        if(!folders.containsValue(email)){          // если в указанной папке нет папки с именем email, то создадим её
            File dir1 = new File(defaultPath + email);
            dir1.mkdir();
            System.out.println("Folder has been created");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);  // для хранения в базе, пароль должен быть закодирован
        String hashedPassword = passwordEncoder.encode(password);

        String uuid = UUID.randomUUID().toString();                     // создаём уникальный номер, для активации

        User user = new User(email, hashedPassword, firstName, lastName, uuid, pathline, 0, role, status);
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
                              @RequestParam String pathline,
                              @RequestParam Integer counterEntry,
                              @RequestParam String role,
                              @RequestParam String status,
                              Model model){

        User user = userRepository.findById(id).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});

        String uuid = UUID.randomUUID().toString();                     // создаём уникальный номер, для активации

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);  // для хранения в базе, пароль должен быть закодирован
        String hashedPassword = passwordEncoder.encode(password);

        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTestCell(uuid);
        user.setPathline(pathline);
        user.setCounterEntry(counterEntry);
        user.setRole(Role.valueOf(role));
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


    public String getCurrentUsername() {        // метод показывает ник того, кто сейчас авторизовался в сессии
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

}
