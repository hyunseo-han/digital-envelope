package com.ddwu.hospital_reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "JSP를 사용하는 Spring MVC 예제입니다!");
        return "home"; // → /WEB-INF/views/home.jsp 렌더링됨
    }
}
