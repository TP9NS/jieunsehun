package com.example.ourapp.review;

import com.example.ourapp.DTO.ReviewDTO;
import com.example.ourapp.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review saveReview(ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setUserId(reviewDTO.getUserId());
        review.setLocationName(reviewDTO.getLocationName());
        review.setRating(reviewDTO.getRating());
        review.setReviewText(reviewDTO.getReviewText());
        return reviewRepository.save(review);
    }

    public List<ReviewDTO> getReviewsByLocationName(String locationName) {
        return reviewRepository.findByLocationName(locationName)
                .stream()
                .map(review -> new ReviewDTO(
                        review.getReviewId(), // reviewId 추가
                        review.getUserId(),
                        review.getLocationName(),
                        review.getRating(),
                        review.getReviewText(),
                        review.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


    public List<ReviewDTO> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);

        return reviews.stream()
                .map(review -> new ReviewDTO(
                        review.getReviewId(), // reviewId
                        review.getUserId(), // userId
                        review.getLocationName(), // locationName
                        review.getRating(), // rating
                        review.getReviewText(), // reviewText
                        review.getCreatedAt() // createdAt
                ))
                .collect(Collectors.toList());
    }


    public ReviewDTO getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(review -> new ReviewDTO(
                        review.getReviewId(), // reviewId
                        review.getUserId(), // userId
                        review.getLocationName(), // locationName
                        review.getRating(), // rating
                        review.getReviewText(), // reviewText
                        review.getCreatedAt() // createdAt
                ))
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
    }

    public void updateReview(ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewDTO.getId()) // reviewId로 검색
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        // 수정할 필드 업데이트
        review.setRating(reviewDTO.getRating());
        review.setReviewText(reviewDTO.getReviewText());
        reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("리뷰를 찾을 수 없습니다.");
        }
        reviewRepository.deleteById(id);
    }
}
