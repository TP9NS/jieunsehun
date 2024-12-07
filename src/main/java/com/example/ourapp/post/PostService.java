package com.example.ourapp.post;

import com.example.ourapp.DTO.CategoryDTO;
import com.example.ourapp.DTO.PostDTO;
import com.example.ourapp.entity.Post;
import com.example.ourapp.entity.Report;
import com.example.ourapp.entity.Category;
import com.example.ourapp.post.CategoryRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReportRepository reportRepository;
    private final CategoryRepository categoryRepository;
    
    @Value("${image.upload.dir}") // 이미지 저장 경로를 application.properties에서 가져옴
    private String uploadDir;

    public PostService(PostRepository postRepository, CategoryRepository categoryRepository, ReportRepository reportRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.reportRepository = reportRepository;
    }
    
    public Page<PostDTO> getPostsByPage(String parentCategoryName, String search, Pageable pageable) {
        if ((parentCategoryName == null || parentCategoryName.isEmpty()) &&
            (search == null || search.isEmpty())) {
            // 필터 조건이 없으면 전체 게시글 가져오기
            return postRepository.findAll(pageable).map(PostDTO::new);
        }
        // 필터 조건이 있을 경우
        return postRepository
            .findByParentCategoryNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
                parentCategoryName != null ? parentCategoryName : "",
                search != null ? search : "",
                pageable
            )
            .map(PostDTO::new);
    }

    public PostDTO savePost(PostDTO postDTO, MultipartFile image) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setUsername(postDTO.getUsername());
        post.setContent(postDTO.getContent());
        post.setLocation(postDTO.getLocation());
        post.setCreatedAt(LocalDateTime.now());
        post.setDetailedCategory(postDTO.getDetailedCategory());
        post.setLatitude(postDTO.getLatitude());
        post.setLongitude(postDTO.getLongitude());
        post.setAddress(postDTO.getAddress());
        post.setPlaceName(postDTO.getPlaceName());

        // 이미지 저장
        if (!image.isEmpty()) {
            try {
                String fileName = saveImage(image); // 파일 저장 후 이름 반환
                post.setImageUrl(fileName); // 이미지 URL 설정
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image", e);
            }
        }

        // 카테고리 설정
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
        System.out.println("PostDTO Data:");
        System.out.println("Title: " + postDTO.getTitle());
        System.out.println("Username: " + postDTO.getUsername());
        System.out.println("Content: " + postDTO.getContent());
        System.out.println("Location: " + postDTO.getLocation());
        System.out.println("Latitude: " + postDTO.getLatitude());
        System.out.println("Longitude: " + postDTO.getLongitude());
        System.out.println("Address: " + postDTO.getAddress());
        System.out.println("Place Name: " + postDTO.getPlaceName());


        return new PostDTO(savedPost);
    }


    private String saveImage(MultipartFile image) throws IOException {
        String uploadDir = "C:/Users/82104/git/jieunsehun/src/main/resources/static/images/";
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFileName = image.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        File file = new File(directory, fileName);
        image.transferTo(file);
        System.out.println("Image saved to: " + file.getAbsolutePath());

        return fileName;
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
    
    public Post getPostEntityById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }
    public void updatePost(PostDTO postDTO, MultipartFile image) {
        // 기존 포스트 조회
        Post post = postRepository.findById(postDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 필드 업데이트
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setLocation(postDTO.getLocation());
        post.setLatitude(postDTO.getLatitude());
        post.setLongitude(postDTO.getLongitude());
        post.setPlaceName(postDTO.getPlaceName());
        post.setAddress(postDTO.getAddress());
        post.setDetailedCategory(postDTO.getDetailedCategory());

        // 이미지 처리
        if (!image.isEmpty()) {
            try {
                String fileName = saveImage(image); // 이미지 저장
                post.setImageUrl(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to update image", e);
            }
        }

        // 카테고리 설정
        if (postDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(postDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID"));
            post.setCategory(category);
            post.setCategoryName(category.getName());
            if (category.getParentCategory() != null) {
                post.setParentCategoryName(category.getParentCategory().getName());
            }
        }

        // 디버깅 로그 추가
        System.out.println("Updated Post Data:");
        System.out.println("Title: " + post.getTitle());
        System.out.println("Content: " + post.getContent());
        System.out.println("Parent Category Name: " + post.getParentCategoryName());
        System.out.println("Category Name: " + post.getCategoryName());
        System.out.println("Detailed Category: " + post.getDetailedCategory());
        System.out.println("Location: " + post.getLocation());
        System.out.println("Latitude: " + post.getLatitude());
        System.out.println("Longitude: " + post.getLongitude());
        System.out.println("Address: " + post.getAddress());
        System.out.println("Place Name: " + post.getPlaceName());
        System.out.println("Image URL: " + post.getImageUrl());

        // 저장
        postRepository.save(post);
    }


    public List<PostDTO> findPostsByUsername(String username) {
        List<Post> posts = postRepository.findByUsername(username);
        return posts.stream()
                .map(PostDTO::new) // PostDTO 생성자를 활용
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deletePost(Long postId) {
        // 게시글과 연관된 신고 기록 삭제
    	reportRepository.deleteByTargetId(postId, Report.ReportType.POST);


        // 게시글 삭제
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다: " + postId);
        }
        postRepository.deleteById(postId);
    }
    
    public void hidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        post.setHidden(true); // 게시글 숨김 처리
        postRepository.save(post); // 변경 사항 저장
    }
    public void unhidePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        post.setHidden(false); // 숨김 상태 해제
        postRepository.save(post);
    }

}