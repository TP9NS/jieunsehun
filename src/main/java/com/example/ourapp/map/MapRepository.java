package com.example.ourapp.map;

import com.example.ourapp.entity.MyMapPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MapRepository extends JpaRepository<MyMapPoint, Long> {
    List<MyMapPoint> findByUserId(Long userId);  // 특정 사용자의 위치 데이터를 조회하는 메서드
   
}
