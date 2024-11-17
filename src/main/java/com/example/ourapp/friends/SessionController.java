package com.example.ourapp.friends;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class SessionController {

    @GetMapping("/session")
    public Map<String, String> getSessionId(HttpSession session) {
        return Collections.singletonMap("sessionId", session.getId());
    }
}