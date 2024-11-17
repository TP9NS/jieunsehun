package com.example.ourapp.post;

import com.example.ourapp.DTO.CategoryDTO;
import com.example.ourapp.DTO.PostDTO;
import com.example.ourapp.entity.Post;
import com.example.ourapp.entity.Category;
import com.example.ourapp.post.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public PostService(PostRepository postRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    public PostDTO savePost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setUsername(postDTO.getUsername());
        post.setContent(postDTO.getContent());
        post.setImageUrl(postDTO.getImageUrl());
        post.setLocation(postDTO.getLocation());
        post.setCreatedAt(LocalDateTime.now());
        post.setDetailedCategory(postDTO.getDetailedCategory());
     // // 카테고리 설정
        if (postDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(postDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID"));
            post.setCategory(category);
            post.setCategoryName(category.getName());
            if (category.getParentCategory() != null) {
                post.setParentCategoryName(category.getParentCategory().getName());
            }
        }

        Post savedPost = postRepository.save(post);

        System.out.println("Saved Post Parent Category Name: " + savedPost.getParentCategoryName());
        System.out.println("Saved Post Category Name: " + savedPost.getCategoryName());
        System.out.println("Saved Post Detailed Category: " + savedPost.getDetailedCategory());

        return new PostDTO(savedPost);
    }
    
 // 카테고리 계층 구조 조회
    public List<CategoryDTO> getAllCategoryHierarchy() {
        List<Category> categories = postRepository.findAllCategoriesWithHierarchy();
        Map<Long, CategoryDTO> categoryDTOMap = new HashMap<>();

        for (Category category : categories) {
            CategoryDTO categoryDTO = categoryDTOMap.computeIfAbsent(category.getId(), id -> new CategoryDTO(category));
            if (category.getParentCategory() != null) {
                CategoryDTO parentDTO = categoryDTOMap.computeIfAbsent(
                        category.getParentCategory().getId(),
                        id -> new CategoryDTO(category.getParentCategory())
                );
                parentDTO.addSubCategory(categoryDTO);
            }
        }

        return categoryDTOMap.values().stream()
                .filter(categoryDTO -> categoryDTO.getParentId() == null)
                .collect(Collectors.toList());
    }

    // 게시글 목록 조회
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

    // 카테고리 이름 목록 조회
    public List<String> getAllCategories() {
        return postRepository.findDistinctCategories();
    }


    public List<PostDTO> getPostsWithCategoryDetails() {
        List<Post> posts = postRepository.findAll();

        return posts.stream().map(post -> {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setUsername(post.getUsername());
            dto.setFormattedCreatedAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            // 카테고리 계층 구조 설정
            Category category = post.getCategory();
            if (category != null) {
                StringBuilder categoryStructure = new StringBuilder();

                // 최상위 카테고리부터 추가
                Category currentCategory = category;
                while (currentCategory != null) {
                    if (categoryStructure.length() > 0) {
                        categoryStructure.insert(0, " - ");
                    }
                    categoryStructure.insert(0, currentCategory.getName());
                    currentCategory = currentCategory.getParentCategory();
                }

                dto.setCategoryName(categoryStructure.toString());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    public List<PostDTO> getPostsFilteredByParentCategory(String parentCategoryName) {
        if (parentCategoryName == null || parentCategoryName.isEmpty()) {
            return getPostsWithCategoryDetails();
        }

        List<Post> posts = postRepository.findByParentCategoryName(parentCategoryName);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        System.out.println("Fetched Post from DB: " + post);
        return new PostDTO(post);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post not found");
        }
        postRepository.deleteById(id);
    }
}