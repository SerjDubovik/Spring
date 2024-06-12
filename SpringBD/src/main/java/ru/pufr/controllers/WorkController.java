package ru.pufr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WorkController {

    @GetMapping("/work")
    public String adminHome(Model model) {


        return "work";
    }
}
