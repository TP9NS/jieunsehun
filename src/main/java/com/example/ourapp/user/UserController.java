package com.example.ourapp.user;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    public String login(@RequestParam("username") String username, 
                        @RequestParam("password") String password, 
                        HttpSession session) {
        try {
            UserDTO userDTO = userService.login(username, password);

            // 로그인 성공 시 세션에 필요한 사용자 정보 저장
            System.out.println(userDTO.getUser_id());
            session.setAttribute("user_id", userDTO.getUser_id());
            session.setAttribute("username", userDTO.getUsername());
            session.setAttribute("permission", userDTO.getPermission());

            // 메인 페이지로 리다이렉트
            return "redirect:/main";  
        } catch (IllegalArgumentException e) {
            // 로그인 실패 시 에러 메시지와 함께 로그인 페이지로 리다이렉트
            session.setAttribute("error", e.getMessage());
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