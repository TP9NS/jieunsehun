package com.example.ourapp.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.SearchHistoryDTO;
import com.example.ourapp.entity.UserSearchHistory;
import com.example.ourapp.entity.UserSearchHistory.SaveType;
import com.example.ourapp.entity.UserSearchHistoryRepository;

@Service
public class UserSearchHistoryService {

    private final UserSearchHistoryRepository searchHistoryRepository;

    @Autowired
    public UserSearchHistoryService(UserSearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public void saveSearchHistory(Long userId, String locationName, double latitude, double longitude, SaveType saveType) {
        UserSearchHistory history = new UserSearchHistory();
        history.setUserId(userId);
        history.setLocationName(locationName);
        history.setLatitude(latitude);
        history.setLongitude(longitude);
        history.setSaveType(saveType); // Directly set the enum

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
                    dto.setSaveType(history.getSaveType());
                    
                    return dto;
                })
                .toList();
    }
    
    public List<SearchHistoryDTO> getSearchHistoryByUserIdAndSaveType(Long userId, UserSearchHistory.SaveType saveType) {
        List<UserSearchHistory> histories = searchHistoryRepository.findByUserIdAndSaveType(userId, saveType);

        // 로그 추가: histories 리스트 확인
        System.out.println("Retrieved histories for userId: " + userId + ", saveType: " + saveType + ", histories: " + histories);

        List<SearchHistoryDTO> dtoList = histories.stream()
                .map(history -> {
                    SearchHistoryDTO dto = new SearchHistoryDTO();
                    dto.setLocationName(history.getLocationName());
                    dto.setLatitude(history.getLatitude());
                    dto.setLongitude(history.getLongitude());
                    dto.setSaveType(history.getSaveType()); // enum 값을 직접 설정
                    return dto;
                })
                .toList();
        
     // 로그 추가: 변환된 dtoList 확인
        System.out.println("Converted DTO list: " + dtoList.stream().map(SearchHistoryDTO::toString).collect(Collectors.toList()));

        
        return dtoList;
    }

}
