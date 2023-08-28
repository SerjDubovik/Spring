package ru.pufr.controllers;


import ru.pufr.models.Post;
import ru.pufr.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class BlogController {

    @Autowired
    private PostRepository postRepository;

    // переменные из проперти файла. содержат данные для подключения базы данных

    @Value("${spring.datasource.url}")
    private String urlDB;

    @Value("${spring.datasource.username}")
    private String usernameDB;

    @Value("${spring.datasource.password}")
    private String passwordDB;

    @GetMapping("/blog")
    public String blog (Model model){
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "home";
    }



    @GetMapping("/blog/add")
    public String blogAdd (Model model){
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "blog-add";
    }


    @PostMapping("/blog/add")
    public String blogPostAdd(@RequestParam String title,
                              @RequestParam String anons,
                              @RequestParam String full_text, Model model){
        Post post = new Post(title, anons, full_text);
        postRepository.save(post);

        try{    // создание таблицы в БД для коментов. уникальная для каждой статьи
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            // команда создания таблицы
            String Comments = "Comments" + "_" + title;
            String sqlCommand = "CREATE TABLE "+ Comments +" (Id INT PRIMARY KEY AUTO_INCREMENT, " +
                                                       "NikName VARCHAR(255), " +
                                                       "Comm    VARCHAR(255))";

            try (Connection conn = DriverManager.getConnection(urlDB, usernameDB, passwordDB)){

                Statement statement = conn.createStatement();
                // создание таблицы
                statement.executeUpdate(sqlCommand);

                System.out.println("Database has been created!");
            }
        }
        catch(Exception ex){

            System.out.println("Connection failed...");

            System.out.println(ex);
        }

        return "redirect:/blog";
    }



    @GetMapping("/about")
    public String about (Model model){
        model.addAttribute("title", "О нас");
        return "about";
    }

    @GetMapping("/blog/{id}")
    public String blogDetails (@PathVariable(value = "id") long id, Model model){
        if(!postRepository.existsById(id)){
            return "redirect:/blog";
        }

        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "blog-details";
    }


    @GetMapping("/blog/{id}/edit")
    public String blogEdit (@PathVariable(value = "id") long id, Model model){
        if(!postRepository.existsById(id)){
            return "redirect:/blog";
        }

        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "blog-edit";
    }


    @PostMapping("/blog/{id}/edit")
    public String blogPostUpdate(@PathVariable(value = "id") long id,
                                 @RequestParam String title,
                                 @RequestParam String anons,
                                 @RequestParam String full_text, Model model){

        //Post post = postRepository.findById(id).orElseThrow(null);
        Post post = postRepository.findById(id).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});

        post.setTitle(title);
        post.setAnons(anons);
        post.setFull_text(full_text);
        postRepository.save(post);
        return "redirect:/blog";
    }



    @PostMapping("/blog/{id}/remove")
    public String blogPostDelete(@PathVariable(value = "id") long id,Model model){

        Post post = postRepository.findById(id).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});
        postRepository.delete(post);
        return "redirect:/blog";
    }



    @GetMapping("/auth/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/auth/success")
    public String getSuccessPage(){
        return "success";
    }


}
