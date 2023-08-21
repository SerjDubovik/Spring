package ru.pufr.controllers;

import ru.pufr.models.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.pufr.repo.PostRepository;

@Controller
public class MainController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/")
    public String home(final Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "home";
    }

}
