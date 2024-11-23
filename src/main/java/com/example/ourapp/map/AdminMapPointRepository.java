package com.example.ourapp.map;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.AdminMapPoint;

@Repository
public interface AdminMapPointRepository extends JpaRepository<AdminMapPoint, Long> {
    @Query("SELECT DISTINCT topic FROM AdminMapPoint WHERE topic IS NOT NULL")
    List<String> findDistinctTopics();

    List<AdminMapPoint> findByTopic(String topic);
    List<AdminMapPoint> findByUserId(Long userId);
}
