package com.example.ourapp.main;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller

public class MainController {

    @GetMapping("/main")
    public String main() {
        return "main.html";
    }
}