package com.example.ourapp.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ourapp.entity.UserSearchHistory.SaveType;

public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Long> {
    List<UserSearchHistory> findByUserId(Long userId);
    List<UserSearchHistory> findByUserIdAndSaveType(Long userId, UserSearchHistory.SaveType saveType);
}
