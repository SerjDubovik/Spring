package ru.pufr.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LessonsController {

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson1")
    public String lesson1() {
        return "Lessons/lesson1";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson2")
    public String lesson2() {
        return "Lessons/lesson2";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson3")
    public String lesson3() {
        return "Lessons/lesson3";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson4")
    public String lesson4() {
        return "Lessons/lesson4";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson5")
    public String lesson5() {
        return "Lessons/lesson5";
    }
    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson6")
    public String lesson6() {
        return "Lessons/lesson6";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson7")
    public String lesson7() {
        return "Lessons/lesson7";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson8")
    public String lesson8() {
        return "Lessons/lesson8";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson9")
    public String lesson9() {
        return "Lessons/lesson9";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson10")
    public String lesson10() {
        return "Lessons/lesson10";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson11")
    public String lesson11() {
        return "Lessons/lesson11";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson12")
    public String lesson12() {
        return "Lessons/lesson12";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson13")
    public String lesson13() {
        return "Lessons/lesson13";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson14")
    public String lesson14() {
        return "Lessons/lesson14";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson15")
    public String lesson15() {
        return "Lessons/lesson15";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson16")
    public String lesson16() {
        return "Lessons/lesson16";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson17")
    public String lesson17() {
        return "Lessons/lesson17";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson18")
    public String lesson18() {
        return "Lessons/lesson18";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson19")
    public String lesson19() {
        return "Lessons/lesson19";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson20")
    public String lesson20() {
        return "Lessons/lesson20";
    }

    @PreAuthorize("hasAuthority('developers:read')")
    @GetMapping("/lesson21")
    public String lesson21() {
        return "Lessons/lesson21";
    }

}
