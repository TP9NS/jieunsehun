package com.example.ourapp.user;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ourapp.DTO.UserDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    @GetMapping("/login")
    public String login() {
        return "main.html";
    }

    @GetMapping("/signup")
    public String signup() {
    	 return "signup.html";
    }
    @PostMapping("/signup" )    // name값을 requestparam에 담아온다
    public String save(@ModelAttribute UserDTO userDTO) {
    	userService.save(userDTO);
        return "main.html";
    }
}