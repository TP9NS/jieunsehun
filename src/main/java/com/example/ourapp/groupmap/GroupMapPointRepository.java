package com.example.ourapp.groupmap;

import com.example.ourapp.entity.GroupMapPoint;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMapPointRepository extends JpaRepository<GroupMapPoint, Long> {
    List<GroupMapPoint> findByGroupId(Long groupId);
    
}