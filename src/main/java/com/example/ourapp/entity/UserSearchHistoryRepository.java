package com.example.ourapp.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSearchHistoryRepository extends JpaRepository<UserSearchHistory, Long> {
    List<UserSearchHistory> findByUserId(Long userId);
}
