package com.example.ourapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

@Controller
public class KakaoController {

    @Autowired
    private UserRepository userRepository;

    private String clientId = "7324f29455ad0dff17d06c69490caaf1";
    private String redirectUri = "http://localhost:8080/oauth/kakao/callback";
    private String tokenUri = "https://kauth.kakao.com/oauth/token";
    private String userInfoUri = "https://kapi.kakao.com/v2/user/me";

    @GetMapping("/oauth/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String requestBody = "grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&code=" + code;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, String.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JSONObject json = new JSONObject(response.getBody());
            String accessToken = json.getString("access_token");

            headers.clear();
            headers.add("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> userInfoRequest = new HttpEntity<>(headers);
            ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUri, HttpMethod.GET, userInfoRequest, String.class);

            if (userInfoResponse.getStatusCode().is2xxSuccessful() && userInfoResponse.getBody() != null) {
                JSONObject userInfo = new JSONObject(userInfoResponse.getBody());
                String kakaoId = userInfo.get("id").toString();
                String nickname = userInfo.getJSONObject("properties").getString("nickname");
                String email = userInfo.getJSONObject("kakao_account").getString("email");
                return userRepository.findByUsername(kakaoId)
                        .map(user -> {
                        	 if (user.getDate() != null && user.getDate().isAfter(LocalDateTime.now())) {
                                 session.setAttribute("error", "정지된 계정입니다. 정지 해제 날짜: " 
                                         + user.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                                 return "redirect:/user/login"; // 로그인 화면으로 리다이렉트
                             }
                            session.setAttribute("user_id", user.getUserId());
                            session.setAttribute("username", user.getUsername());
                            session.setAttribute("permission", user.getPermission());
                            return "redirect:/main";
                        })
                        .orElseGet(() -> {
                            session.setAttribute("kakao_id", kakaoId);
                            session.setAttribute("nickname", nickname);
                            session.setAttribute("email", email);
                            return "redirect:/user/kakaoSignup";
                        });
            }
        }
        return "redirect:/error"; 
    }
}
