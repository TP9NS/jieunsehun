package com.example.ourapp.review;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ourapp.DTO.ReviewDTO;

@RestController
@RequestMapping("/api")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private HttpSession session; // HttpSession 주입

    @PostMapping("/saveReview")
    public ResponseEntity<Map<String, String>> saveReview(@RequestBody ReviewDTO reviewDTO, HttpSession session) {
        // 세션에서 userId 가져오기
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        // userId를 ReviewDTO에 설정
        reviewDTO.setUserId(userId);

        // 리뷰 저장
        reviewService.saveReview(reviewDTO);

        // 성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "리뷰가 저장되었습니다."));
    }


    @GetMapping("/reviews")
    public List<Map<String, Object>> getReviewsByLocation(@RequestParam String locationName) {
        // locationName 파라미터 확인
        System.out.println("Received request for reviews with locationName: " + locationName);

        List<ReviewDTO> reviews = reviewService.getReviewsByLocationName(locationName);

        // 리뷰 데이터 확인
        if (reviews.isEmpty()) {
            System.out.println("No reviews found for locationName: " + locationName);
        } else {
            System.out.println("Found " + reviews.size() + " reviews for locationName: " + locationName);
        }

        // 필요한 정보만 반환하도록 맵핑
        return reviews.stream().map(review -> {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("author", "사용자" + review.getUserId()); // 예제에서는 사용자 ID를 기반으로 작성자 표시
            reviewMap.put("rating", review.getRating());
            reviewMap.put("text", review.getReviewText());
            reviewMap.put("createdAt", review.getCreatedAt().toString()); // 리뷰 작성 날짜

            // 개별 리뷰 데이터 확인
            System.out.println("Mapped review: " + reviewMap);

            return reviewMap;
        }).collect(Collectors.toList());
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<String> updateReview(@PathVariable Long id, @RequestBody ReviewDTO updatedReview) {
        updatedReview.setId(id); // DTO에 ID 설정
        reviewService.updateReview(updatedReview);
        return ResponseEntity.ok("리뷰가 수정되었습니다.");
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }


}

