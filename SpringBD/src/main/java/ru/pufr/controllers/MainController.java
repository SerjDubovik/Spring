package ru.pufr.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {


    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/about")
    public String about (){
        return "about";
    }

    @GetMapping("/articles/article1")
    public String articles() {
        return "/articles/article1";
    }

}
