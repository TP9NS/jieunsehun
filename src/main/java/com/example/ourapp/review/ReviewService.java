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
                .map(review -> {
                    ReviewDTO dto = new ReviewDTO();
                    dto.setUserId(review.getUserId());
                    dto.setLocationName(review.getLocationName());
                    dto.setRating(review.getRating());
                    dto.setReviewText(review.getReviewText());
                    dto.setCreatedAt(review.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
