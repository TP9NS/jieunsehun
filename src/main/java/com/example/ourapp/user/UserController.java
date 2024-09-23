package com.example.ourapp.user;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ourapp.DTO.UserDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    @GetMapping("/login")
    public String login() {
        return "login.html";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        try {
            UserDTO userDTO = userService.login(username, password);
            // 로그인 성공 시 메인 페이지로 이동
            redirectAttributes.addFlashAttribute("user", userDTO);
            return "redirect:/main";  // 메인 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            // 로그인 실패 시 에러 메시지와 함께 로그인 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/login";
        }
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