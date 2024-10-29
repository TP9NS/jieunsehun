package com.example.ourapp.user;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.ourapp.DTO.SearchHistoryDTO;
import com.example.ourapp.entity.UserSearchHistory;

@RestController
@RequestMapping("/search-history")
public class UserSearchHistoryController {

    private final UserSearchHistoryService searchHistoryService;

    @Autowired
    public UserSearchHistoryController(UserSearchHistoryService searchHistoryService) {
        this.searchHistoryService = searchHistoryService;
    }

    @PostMapping("/save")
    public void saveSearchHistory(@RequestBody SearchHistoryDTO searchHistory, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId != null) {
            searchHistoryService.saveSearchHistory(
                userId,
                searchHistory.getLocationName(),
                searchHistory.getLatitude(),
                searchHistory.getLongitude(),
                searchHistory.getSaveType()
            );
        } else {
            throw new RuntimeException("User not logged in");
        }
    }

    @GetMapping("/user")
    public List<SearchHistoryDTO> getUserSearchHistory(@RequestParam UserSearchHistory.SaveType saveType, HttpSession session) {
        System.out.println("Received saveType: " + saveType);
        Long userId = (Long) session.getAttribute("user_id");
        if (userId != null) {
            // 서비스에서 호출한 메서드가 locationName 포함한 DTO를 반환
            return searchHistoryService.getSearchHistoryByUserIdAndSaveType(userId, saveType);
        } else {
            throw new RuntimeException("User not logged in");
        }
    }

}
