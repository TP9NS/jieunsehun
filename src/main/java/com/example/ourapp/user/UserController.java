package com.example.ourapp.user;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        System.out.println("세션없어짐");
        return "redirect:/main";
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
    
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/user/login";
        }
        // UserDTO를 가져와서 모델에 추가
        UserDTO userDTO = userService.findUserById(userId);
        model.addAttribute("user", userDTO);
        model.addAttribute("editMode", true);

        return "mypage.html";
    }
    
    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDTO userDTO, HttpSession session) {
        userService.updateUser(userDTO);
        session.setAttribute("user", userDTO);
        return "redirect:/user/mypage";
    }
    
    @PostMapping("/checkid")
    @ResponseBody
    public Map<String, String> checkUsernameDuplicate(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        boolean isDuplicate = userService.checkUsernameDuplicate(username);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", isDuplicate ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");
        return response;
    }
    @GetMapping("/kakaoSignup")
    public String kakaoSignup(HttpSession session, Model model) {
        // 세션에서 카카오 정보를 가져와 뷰로 전달
        model.addAttribute("kakaoId", session.getAttribute("kakao_id"));
        model.addAttribute("nickname", session.getAttribute("nickname"));
        model.addAttribute("email",session.getAttribute("email"));
        return "kakaoSignup";  // 카카오 회원가입 페이지
    }
    @PostMapping("/kakaoSignup")
    public String saveKakaoUser(@ModelAttribute UserDTO userDTO, HttpSession session) {
        userDTO.setUsername(((String)session.getAttribute("kakao_id")));
        userDTO.setPassword("kakaologinUSER");
        
    	userService.save(userDTO);
        session.invalidate(); // 가입 후 세션 초기화
        return "redirect:/main";
    }
}