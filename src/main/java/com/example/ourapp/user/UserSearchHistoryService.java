package com.example.ourapp.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.SearchHistoryDTO;
import com.example.ourapp.entity.UserSearchHistory;
import com.example.ourapp.entity.UserSearchHistoryRepository;

@Service
public class UserSearchHistoryService {

    private final UserSearchHistoryRepository searchHistoryRepository;

    @Autowired
    public UserSearchHistoryService(UserSearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public void saveSearchHistory(Long userId, String locationName, double latitude, double longitude) {
        UserSearchHistory history = new UserSearchHistory(userId, locationName, latitude, longitude);
        searchHistoryRepository.save(history);
    }
    public List<SearchHistoryDTO> getSearchHistoryByUserId(Long userId) {
        List<UserSearchHistory> histories = searchHistoryRepository.findByUserId(userId);
        return histories.stream()
                        .map(history -> {
                            SearchHistoryDTO dto = new SearchHistoryDTO();
                            dto.setLocationName(history.getLocationName());
                            dto.setLatitude(history.getLatitude());
                            dto.setLongitude(history.getLongitude());
                            return dto;
                        })
                        .toList();
    }
}
