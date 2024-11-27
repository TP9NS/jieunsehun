package com.example.ourapp.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import com.example.ourapp.DTO.AdminMapPointDTO;
import com.example.ourapp.DTO.CategoryDTO;
import com.example.ourapp.DTO.GroupDTO;
import com.example.ourapp.DTO.MyMapPointDTO;
import com.example.ourapp.DTO.PostDTO;
import com.example.ourapp.entity.Comment;
import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.Post;
import com.example.ourapp.group.GroupService;
import com.example.ourapp.map.AdminMapPointService;
import com.example.ourapp.map.MyMapPointService;
import com.example.ourapp.post.CategoryService;
import com.example.ourapp.post.CommentService;
import com.example.ourapp.post.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final String OPENAI_API_KEY = "";
    @Autowired
    private MyMapPointService myMapPointService;
    @Autowired
    private AdminMapPointService adminMapPointService;
    @Autowired
    private PostService postService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private GroupService groupService;
    @GetMapping("/main")
    public String main(HttpSession session, Model model) {
        // 세션에서 사용자 권한과 사용자 ID 가져오기
        Integer userPermission = (Integer) session.getAttribute("permission");
        Long adminUserId = (Long) session.getAttribute("user_id");

        // 모든 장소 가져오기
        List<AdminMapPointDTO> adminMapPoints = adminMapPointService.getAllAdminMapPoints();

        // 주제(topic) 리스트 생성
        List<String> distinctTopics = adminMapPoints.stream()
            .map(AdminMapPointDTO::getTopic) // 주제 가져오기
            .filter(topic -> topic != null && !topic.isEmpty()) // 비어있지 않은 값만 필터링
            .distinct() // 중복 제거
            .sorted() // 정렬
            .collect(Collectors.toList());

        // 모델에 데이터 추가
        model.addAttribute("adminMapPoints", adminMapPoints); // 전체 장소
        model.addAttribute("distinctTopics", distinctTopics); // 고유 주제
        model.addAttribute("userPermission", userPermission);

        return "main.html";
    }


    @GetMapping("/map")
    public String map() {
       return "map.html";
    }
    @GetMapping("/index")
    public String index() {
       return "index.html";
    }
    
    @GetMapping("/board")
    public String board(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String parentCategoryName,
        Model model
    ) {
        // 페이징 설정: 정렬 기준은 createdAt 필드, 내림차순
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 게시글 데이터 가져오기
        Page<PostDTO> postsPage = postService.getPostsByPage(parentCategoryName, search, pageable);

        // 댓글 수 추가
        postsPage.getContent().forEach(post -> {
            int commentCount = commentService.countCommentsByPostId(post.getId());
            post.setCommentCount(commentCount);
        });

        model.addAttribute("posts", postsPage.getContent()); // 현재 페이지 게시글
        model.addAttribute("currentPage", postsPage.getNumber()); // 현재 페이지 번호
        model.addAttribute("totalPages", postsPage.getTotalPages()); // 전체 페이지 수
        model.addAttribute("totalElements", postsPage.getTotalElements()); // 전체 게시글 수
        model.addAttribute("parentCategoryName", parentCategoryName);
        model.addAttribute("search", search);

        // 카테고리 계층 데이터 추가
        List<CategoryDTO> categories = categoryService.getAllCategoryHierarchy();
        model.addAttribute("categories", categories);

        return "board.html";
    }

    @GetMapping("/board/view/{id}")
    public String viewPost(@PathVariable Long id, Model model, HttpSession session) {
        PostDTO post = postService.getPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }

        List<Comment> comments = commentService.findCommentsByPostId(id);

        // 세션에서 사용자 이름 가져오기
        String username = (String) session.getAttribute("username");

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("username", username); // 사용자 이름 모델에 추가

        return "view";
    }
    @PostMapping("/comments/add")
    public String addComment(@ModelAttribute Comment comment, @RequestParam("postId") Long postId, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        comment.setUsername(username);

        Post post = postService.getPostEntityById(postId);
        comment.setPost(post);

        commentService.saveComment(comment);

        return "redirect:/board/view/" + postId;
    }

    @PostMapping("/comments/edit")
    public String editComment(@RequestParam("commentId") Long commentId,
                              @RequestParam("content") String newContent,
                              HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        Comment comment = commentService.findCommentById(commentId);

        // 댓글 작성자 확인
        if (!comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        commentService.updateComment(commentId, newContent);

        return "redirect:/board/view/" + comment.getPost().getId();
    }
    
    @PostMapping("/comments/delete")
    public String deleteComment(@RequestParam("commentId") Long commentId,
                                HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        Comment comment = commentService.findCommentById(commentId);

        if (!comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        commentService.deleteComment(commentId);

        return "redirect:/board/view/" + comment.getPost().getId();
    }
    
    @GetMapping("/post")
    public String postForm(Model model) {
        model.addAttribute("post", new PostDTO());
        return "post.html";
    }
    
    @PostMapping("/post/save")
    public String savePost(
            @ModelAttribute PostDTO postDTO,
            @RequestParam("image") MultipartFile image,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String placeName) {
        try {
            postDTO.setLatitude(latitude);
            postDTO.setLongitude(longitude);
            postDTO.setAddress(address);
            postDTO.setPlaceName(placeName);

            postService.savePost(postDTO, image);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/post?error=upload";
          }

        return "redirect:/board";
    }

    @PostMapping("/post/delete")
    public String deletePost(@RequestParam Long id, HttpSession session) {
        PostDTO post = postService.getPostById(id);
        String sessionUsername = (String) session.getAttribute("username");

        if (!post.getUsername().equals(sessionUsername)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postService.deletePost(id);
        return "redirect:/board";
    }

    @GetMapping("/post/edit/{id}")
    public String editPost(@PathVariable Long id, Model model, HttpSession session) {
        PostDTO post = postService.getPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }

        String sessionUsername = (String) session.getAttribute("username");
        if (sessionUsername == null) {
            throw new IllegalStateException("로그인된 사용자 정보가 없습니다.");
        }

        if (!post.getUsername().equals(sessionUsername)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        List<CategoryDTO> categoryHierarchy = categoryService.getAllCategoryHierarchy();

        model.addAttribute("post", post);
        model.addAttribute("categoryHierarchy", categoryHierarchy);

        return "edit";
    }


    @PostMapping("/post/update")
    public String updatePost(
        @ModelAttribute PostDTO postDTO,
        @RequestParam("image") MultipartFile image,
        HttpSession session
    ) {
        String sessionUsername = (String) session.getAttribute("username");

        if (!postDTO.getUsername().equals(sessionUsername)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        postService.updatePost(postDTO, image);

        return "redirect:/board/view/" + postDTO.getId();
    }



    
    @PostMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<?> chatWithGPT(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        try {
            // OpenAI API 요청 생성
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.openai.com/v1/chat/completions";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);

            // API 요청에 필요한 JSON 바디 구성
            JSONObject body = new JSONObject();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", userMessage)));

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // OpenAI 응답 처리
            JSONObject responseBody = new JSONObject(response.getBody());
            String gptResponse = responseBody.getJSONArray("choices")
                                              .getJSONObject(0)
                                              .getJSONObject("message")
                                              .getString("content");

            // 클라이언트에 결과 반환
            return ResponseEntity.ok(Collections.singletonMap("response", gptResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
 // 새로운 저장 메서드 추가
    @PostMapping("/saveMyMapPoint")
    @ResponseBody
    public ResponseEntity<?> saveMyMapPoint(@RequestBody MyMapPointDTO myMapPointDTO,HttpSession session) {
        try {
            // MyMapPointDTO를 서비스로 전달하여 저장
           myMapPointDTO.setUserId((Long)session.getAttribute("user_id"));
           myMapPointDTO.setSearchTime(LocalDateTime.now());
            myMapPointService.saveMyMapPoint(myMapPointDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "Location saved successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
    @PostMapping("/saveAdminMapPoint")
    @ResponseBody
    public ResponseEntity<?> saveAdminMapPoint(@RequestBody AdminMapPointDTO adminMapPointDTO, HttpSession session) {
        try {
            // AdminMapPointDTO를 서비스로 전달하여 저장
            adminMapPointDTO.setUserId((Long) session.getAttribute("user_id"));
            adminMapPointDTO.setSearchTime(LocalDateTime.now());
            adminMapPointService.saveAdminMapPoint(adminMapPointDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "Location saved successfully with topic."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
    @GetMapping("/user/groups")
    @ResponseBody
    public List<GroupDTO> getUserGroups(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            throw new IllegalArgumentException("User not logged in");
        }

        List<Group> groups = groupService.findGroupsByUser(userId);
        return groups.stream()
                     .map(GroupDTO::new) // GroupDTO로 변환
                     .collect(Collectors.toList());
    }

}