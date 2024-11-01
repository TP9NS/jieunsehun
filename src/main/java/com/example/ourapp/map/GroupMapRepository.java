package com.example.ourapp.map;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ourapp.entity.GroupMapPoint;
import java.util.List;

public interface GroupMapRepository extends JpaRepository<GroupMapPoint, Long> {
    List<GroupMapPoint> findByUserId(Long userId);  // 특정 사용자의 group_map_point 목록 조회
}
