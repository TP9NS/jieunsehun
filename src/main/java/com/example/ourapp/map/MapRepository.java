package com.example.ourapp.map;


import com.example.ourapp.entity.UserSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MapRepository extends JpaRepository<UserSearchHistory, Long> {
    List<UserSearchHistory> findByUserIdAndSaveType(Long userId, UserSearchHistory.SaveType saveType);
}