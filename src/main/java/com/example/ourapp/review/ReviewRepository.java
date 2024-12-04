package com.example.ourapp.review;
import com.example.ourapp.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByLocationName(String locationName); // 장소별 리뷰 검색
}
