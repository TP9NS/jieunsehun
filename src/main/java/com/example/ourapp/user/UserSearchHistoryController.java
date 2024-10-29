package com.example.ourapp.user;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.ourapp.DTO.SearchHistoryDTO;

@RestController
@RequestMapping("/search-history")
public class UserSearchHistoryController {

    private final UserSearchHistoryService searchHistoryService;

    @Autowired
    public UserSearchHistoryController(UserSearchHistoryService searchHistoryService) {
        this.searchHistoryService = searchHistoryService;
    }

    @PostMapping("/save")
    public void saveSearchHistory(@RequestBody SearchHistoryDTO searchHistory,
                                  HttpSession session) {
        // 세션에서 user_id를 가져온다
        Long userId = (Long) session.getAttribute("user_id");
        if (userId != null) {
            // 사용자 ID가 존재하면 검색 기록을 저장한다
            searchHistoryService.saveSearchHistory(userId, searchHistory.getLocationName(), 
                searchHistory.getLatitude(), searchHistory.getLongitude());
        } else {
            // 사용자 ID가 없을 경우 에러 처리
            throw new RuntimeException("User not logged in");
        }
    }
    @GetMapping("/user")
    public List<SearchHistoryDTO> getUserSearchHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId != null) {
            return searchHistoryService.getSearchHistoryByUserId(userId);
        } else {
            throw new RuntimeException("User not logged in");
        }
    }
}
