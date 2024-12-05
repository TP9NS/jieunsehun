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

        // 엔티티를 DTO로 변환
        return reviews.stream()
                      .map(review -> new ReviewDTO(
                          review.getUserId(),
                          review.getLocationName(),
                          review.getRating(),
                          review.getReviewText(),
                          review.getCreatedAt()
                      ))
                      .collect(Collectors.toList());
    }


}
