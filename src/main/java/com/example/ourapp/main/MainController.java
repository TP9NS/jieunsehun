package com.example.ourapp.main;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.ourapp.DTO.MyMapPointDTO;
import com.example.ourapp.DTO.PostDTO;
import com.example.ourapp.map.AdminMapPointService;
import com.example.ourapp.map.MyMapPointService;
import com.example.ourapp.post.CategoryService;
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

    @GetMapping("/main")
    public String main() {
        return "main.html";
    }
    @GetMapping("/test")
    public String test() {
    	return "map_test.html";
    }
    @GetMapping("/map")
    public String map() {
    	return "map.html";
    }
    
    @GetMapping("/main_test")
    public String main_test() {
    	return "main_test.html";
    }
    @GetMapping("/main_test2")
    public String main_test2() {
    	return "main_test2.html";
    }
    
    @GetMapping("/board")
    public String board(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId, // Long 타입으로 변경
            Model model) {

        // 모든 게시글 가져오기
        List<PostDTO> posts = postService.getAllPosts();

        // 카테고리 필터
        if (categoryId != null) {
            posts = posts.stream()
                    .filter(post -> post.getCategoryId() != null && post.getCategoryId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        // 검색어 필터
        if (search != null && !search.isEmpty()) {
            posts = posts.stream()
                    .filter(post -> post.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                            post.getContent().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

     // 계층형 카테고리 데이터 추가
        List<CategoryDTO> categories = categoryService.getAllCategoryHierarchy();
        model.addAttribute("categories", categories);

        // JavaScript용 JSON 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String categoriesJson = objectMapper.writeValueAsString(categories); // JSON 변환
            model.addAttribute("categoriesJson", categoriesJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // 예외 처리
            model.addAttribute("categoriesJson", "[]"); // 기본 빈 배열 설정
        }
        model.addAttribute("posts", posts);
        return "board.html";
    }




    @GetMapping("/board/view/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        PostDTO post = postService.getPostById(id); // 게시글 조회
        model.addAttribute("post", post); // 모델에 게시글 데이터 추가
        return "view.html"; // 확장자 제외한 템플릿 이름
    }
    
    @GetMapping("/post")
    public String postForm(Model model) {
        model.addAttribute("post", new PostDTO()); // 빈 PostDTO 전달
        return "post.html";
    }
    
    @PostMapping("/post/save")
    public String savePost(@ModelAttribute PostDTO postDTO, @RequestParam("image") MultipartFile image) {
        if (!image.isEmpty()) {
            try {
                // 이미지 이름 가져오기
                String fileName = image.getOriginalFilename();

                // 이미지 이름만 저장
                postDTO.setImageUrl(fileName); // imageUrl 필드에 이미지 이름 저장
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/post?error=upload";
            }
        }

        postService.savePost(postDTO);
        return "redirect:/board";
    }

    
    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/board";
    }
    
    // OpenAI GPT와의 대화를 처리하는 엔드포인트
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

}
