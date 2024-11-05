package com.example.ourapp.map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.AdminMapPoint;

@Repository
public interface AdminMapPointRepository extends JpaRepository<AdminMapPoint, Long> {
    // 추가적인 커스텀 쿼리가 필요하다면 여기에 메서드를 정의할 수 있습니다.
}
