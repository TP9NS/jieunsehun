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
    private String detailedCategory;
    private String imageUrl;
    private String location;
    private LocalDateTime createdAt;
    private String formattedCreatedAt; 

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.username = post.getUsername();
        this.content = post.getContent();

        // 상위, 하위, 상세 카테고리 정보 설정
        this.parentCategoryName = post.getParentCategoryName(); // 상위 카테고리
        this.categoryName = post.getCategoryName(); // 하위 카테고리
        this.detailedCategory = post.getDetailedCategory(); // 상세 카테고리

        

        this.imageUrl = post.getImageUrl();
        this.location = post.getLocation();
        this.createdAt = post.getCreatedAt();

        // 디버깅용 로그
        System.out.println("Post ID: " + this.id);
        System.out.println("Title: " + this.title);
        System.out.println("Parent Category Name: " + this.parentCategoryName);
        System.out.println("Category Name: " + this.categoryName);
        System.out.println("Detailed Category: " + this.detailedCategory);
    }

    
    public String getDetailedCategory() {
        return detailedCategory;
    }

    public void setDetailedCategory(String detailedCategory) {
        this.detailedCategory = detailedCategory;
    }

    
    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", categoryId=" + categoryId +
            ", parentCategoryName='" + parentCategoryName + '\'' +
            ", categoryName='" + categoryName + '\'' +
            ", detailedCategory='" + detailedCategory + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public PostDTO() {}
}
