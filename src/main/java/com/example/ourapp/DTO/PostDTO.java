package com.example.ourapp.DTO;

import com.example.ourapp.entity.Post;
import com.example.ourapp.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Stack;

@Getter
@Setter
public class PostDTO {

    private Long id;
    private String title;
    private String username;
    private String content;
    private Long categoryId; // 하위 카테고리 ID
    private String categoryName; // 하위 카테고리 이름
    private String subCategoryName;
    private Long parentCategoryId; // 상위 카테고리 ID
    private String parentCategoryName; // 상위 카테고리 이름
    private String fullCategoryName;  // "중간 카테고리 - 세부 카테고리" 형식
    private String imageUrl;
    private String location;
    private LocalDateTime createdAt;
    private String formattedCreatedAt; 

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.username = post.getUsername();
        this.content = post.getContent();
     // 카테고리 정보 설정
        if (post.getCategory() != null) {
            this.categoryId = post.getCategory().getId();
            this.categoryName = post.getCategory().getName();

            Category currentCategory = post.getCategory();
            if (currentCategory.getParentCategory() != null) {
                this.subCategoryName = currentCategory.getParentCategory().getName();
                if (currentCategory.getParentCategory().getParentCategory() != null) {
                    this.parentCategoryName = currentCategory.getParentCategory().getParentCategory().getName();
                } else {
                    this.parentCategoryName = currentCategory.getParentCategory().getName();
                }
            }

            // fullCategoryName 생성
            StringBuilder categoryBuilder = new StringBuilder();
            if (this.parentCategoryName != null && !this.parentCategoryName.equals(this.subCategoryName)) {
                categoryBuilder.append(this.parentCategoryName);
            }
            if (this.subCategoryName != null) {
                if (categoryBuilder.length() > 0) categoryBuilder.append(" - ");
                categoryBuilder.append(this.subCategoryName);
            }
            if (this.categoryName != null) {
                if (categoryBuilder.length() > 0) categoryBuilder.append(" - ");
                categoryBuilder.append(this.categoryName);
            }

            this.fullCategoryName = categoryBuilder.toString();
        }

        this.imageUrl = post.getImageUrl();
        this.location = post.getLocation();
        this.createdAt = post.getCreatedAt();
    }
    
    public String getFullCategoryName() {
        return fullCategoryName; // 이미 생성자에서 설정된 값
    }

    
    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", categoryId=" + categoryId +
                ", fullCategoryName='" + fullCategoryName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public PostDTO() {}
}
