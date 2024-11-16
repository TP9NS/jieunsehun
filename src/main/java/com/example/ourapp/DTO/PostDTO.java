package com.example.ourapp.DTO;

import com.example.ourapp.entity.Post;
import com.example.ourapp.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {

    private Long id;
    private String title;
    private String username;
    private String content;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private String location;
    private LocalDateTime createdAt;
    private String formattedCreatedAt; 

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.username = post.getUsername();
        this.content = post.getContent();
        if (post.getCategory() != null) {
            this.categoryId = post.getCategory().getId();
            this.categoryName = post.getCategory().getName();
        }
        this.imageUrl = post.getImageUrl();
        this.location = post.getLocation();
        this.createdAt = post.getCreatedAt();
    }

    public PostDTO() {}
}
