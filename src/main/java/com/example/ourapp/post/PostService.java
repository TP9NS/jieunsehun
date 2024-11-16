package com.example.ourapp.post;

import com.example.ourapp.DTO.CategoryDTO;
import com.example.ourapp.DTO.PostDTO;
import com.example.ourapp.entity.Post;
import com.example.ourapp.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostDTO savePost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setUsername(postDTO.getUsername());
        post.setContent(postDTO.getContent());
        // Category 설정
        if (postDTO.getCategoryId() != null) {
            Category category = new Category();
            category.setId(postDTO.getCategoryId()); // Category ID 설정
            post.setCategory(category);
        } else {
            throw new IllegalArgumentException("Category ID must not be null. Please select a valid category.");
        }
        
        post.setImageUrl(postDTO.getImageUrl());
        post.setLocation(postDTO.getLocation());
        return new PostDTO(postRepository.save(post));
    }
    
    public List<CategoryDTO> getAllCategoryHierarchy() {
        List<Category> allCategories = postRepository.findAllCategoriesWithHierarchy();

        // 계층형 데이터로 변환
        Map<Long, CategoryDTO> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getId, category -> new CategoryDTO(category)));

        // 상위-하위 관계 설정
        allCategories.forEach(category -> {
            if (category.getParentCategory() != null) {
                CategoryDTO parent = categoryMap.get(category.getParentCategory().getId());
                if (parent != null) {
                    parent.addSubCategory(categoryMap.get(category.getId()));
                }
            }
        });

        // 최상위 카테고리만 반환
        return categoryMap.values().stream()
                .filter(category -> category.getParentId() == null)
                .collect(Collectors.toList());
    }

    // 전체 게시글 조회
    public List<PostDTO> getAllPosts() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return postRepository.findAll().stream()
                .map(post -> {
                    PostDTO dto = new PostDTO(post);
                    dto.setFormattedCreatedAt(post.getCreatedAt().format(formatter)); // 날짜 포맷
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public List<String> getAllCategories() {
        return postRepository.findDistinctCategories();
    }

    // ID로 게시글 조회
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return new PostDTO(post); // Entity -> DTO 변환
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post not found");
        }
        postRepository.deleteById(id);
    }
}
