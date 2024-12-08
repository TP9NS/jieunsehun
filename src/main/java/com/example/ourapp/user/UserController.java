package com.example.ourapp.user;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ourapp.DTO.PostDTO;
import com.example.ourapp.DTO.ReportDTO;
import com.example.ourapp.DTO.ReviewDTO;
import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.entity.AdminMapPoint;
import com.example.ourapp.entity.Report;
import com.example.ourapp.entity.User;
import com.example.ourapp.map.AdminMapPointRepository;
import com.example.ourapp.post.CommentService;
import com.example.ourapp.post.PostService;
import com.example.ourapp.post.ReportService;
import com.example.ourapp.review.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private AdminMapPointRepository adminMapPointRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, 
                        @RequestParam("password") String password, 
                        HttpSession session) {
        try {
            UserDTO userDTO = userService.login(username, password);

            // 정지 날짜 확인
            if (userDTO.getStopDate() != null && userDTO.getStopDate().isAfter(LocalDateTime.now())) {
                // 정지된 경우
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formattedStopDate = userDTO.getStopDate().format(formatter);
                session.setAttribute("error", "계정이 정지되었습니다. 정지 해제 날짜: " + formattedStopDate);
                return "redirect:/user/login";
            }

            // 정지가 아닌 경우 로그인 처리
            session.setAttribute("user_id", userDTO.getUser_id());
            session.setAttribute("username", userDTO.getUsername());
            session.setAttribute("permission", userDTO.getPermission()); // permission 값 설정

            // 세션 정보 출력
            System.out.println("Login successful!");
            System.out.println("User ID: " + session.getAttribute("user_id"));
            System.out.println("Username: " + session.getAttribute("username"));
            System.out.println("Permission: " + session.getAttribute("permission"));

            return "redirect:/main";
        } catch (IllegalArgumentException e) {
            session.setAttribute("error", e.getMessage());
            return "redirect:/user/login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        System.out.println("세션없어짐");
        return "redirect:/main";
    }

    @GetMapping("/signup")
    public String signup() {
    	 return "signup.html";
    }
    @PostMapping("/signup" )    // name값을 requestparam에 담아온다
    public String save(@ModelAttribute UserDTO userDTO) {
    	userService.save(userDTO);
        return "main.html";
    }
    
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        String username = (String) session.getAttribute("username");

        if (userId == null || username == null) {
            return "redirect:/user/login";
        }

        // UserDTO를 가져와서 모델에 추가
        UserDTO userDTO = userService.findUserById(userId);
        model.addAttribute("user", userDTO);
        model.addAttribute("editMode", true);

        // username으로 게시글 가져오기
        List<PostDTO> userPosts = postService.findPostsByUsername(username);
        model.addAttribute("userPosts", userPosts);

        // userId로 리뷰 가져오기
        List<ReviewDTO> userReviews = reviewService.getReviewsByUserId(userId);
        model.addAttribute("userReviews", userReviews);

        return "mypage.html";
    }



    
    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDTO userDTO, HttpSession session) {
        userService.updateUser(userDTO);
        session.setAttribute("user", userDTO);
        return "redirect:/user/mypage";
    }
    
    @GetMapping("/manage-locations")
    public String manageLocations(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        Integer permission = (Integer) session.getAttribute("permission");

        // 관리자가 아니라면 접근을 제한
        if (userId == null || permission == null || permission != 0) {
            return "redirect:/user/login";
        }

        // 모든 장소 데이터를 가져와서 모델에 추가
        List<AdminMapPoint> allLocations = adminMapPointRepository.findAll();
        model.addAttribute("locations", allLocations);

        return "manageLocations";
    }
    
    @GetMapping("/report")
    public String manageReports(HttpSession session, Model model) {
        // Check user permission
        Integer permission = (Integer) session.getAttribute("permission");

        if (permission == null || permission != 0) {
            return "redirect:/user/login";
        }

        // Fetch all post reports
        List<ReportDTO> postReports = reportService.getAllPostReports();
        System.out.println("Post Reports Retrieved: ");
        postReports.forEach(report -> {
            System.out.println("ID: " + report.getId() +
                               ", Reason: " + report.getReason() +
                               ", Reported By: " + report.getUsername() +
                               ", Target ID: " + report.getTargetId() +
                               ", Type: " + report.getType() +
                               ", Hidden: " + report.isHidden() +
                               ", Reported At: " + report.getReportedAt() +
                               ", Post ID: " + report.getPostId());
        });

        // Fetch all comment reports
        List<ReportDTO> commentReports = reportService.getAllCommentReports();
        System.out.println("Comment Reports Retrieved: ");
        commentReports.forEach(report -> {
            System.out.println("ID: " + report.getId() +
                               ", Reason: " + report.getReason() +
                               ", Reported By: " + report.getUsername() +
                               ", Target ID: " + report.getTargetId() +
                               ", Type: " + report.getType() +
                               ", Hidden: " + report.isHidden() +
                               ", Reported At: " + report.getReportedAt() +
                               ", Post ID: " + report.getPostId());
        });

        // Filter reports to show hidden items only for admins
        if (permission == 0) { // Admin user
            System.out.println("Admin viewing all reports, including hidden ones.");
        } else { // Non-admin users (additional safety check, should already redirect)
            postReports = postReports.stream()
                    .filter(report -> !report.isHidden())
                    .toList();
            commentReports = commentReports.stream()
                    .filter(report -> !report.isHidden())
                    .toList();
        }

        model.addAttribute("postReports", reportService.getVisiblePostReports());
        model.addAttribute("hiddenPostReports", reportService.getHiddenPostReports());
        model.addAttribute("commentReports", reportService.getVisibleCommentReports());
        model.addAttribute("hiddenCommentReports", reportService.getHiddenCommentReports());

        return "manageReport";
    }


    @DeleteMapping("/report/delete/{type}/{id}")
    public ResponseEntity<String> deleteReport(@PathVariable String type, @PathVariable Long id) {
        try {
            // 신고 삭제 서비스 호출
            reportService.deleteReport(type, id);
            return ResponseEntity.ok("신고가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("신고 삭제에 실패했습니다.");
        }
    }
    
    @PostMapping("/post/hide")
    public String hidePost(@RequestParam Long id, HttpSession session) {
        Integer permission = (Integer) session.getAttribute("permission");
        if (permission == null || permission != 0) {
            return "redirect:/user/login"; // 관리자가 아니면 로그인 페이지로 리다이렉트
        }
        postService.hidePost(id); // 게시글 숨김 처리 서비스 호출
        return "redirect:/board/view/" + id; // 숨긴 후 게시글로 리다이렉트
    }

    @PostMapping("/comments/hide")
    public String hideComment(@RequestParam Long commentId, HttpSession session) {
        Integer permission = (Integer) session.getAttribute("permission");
        if (permission == null || permission != 0) {
            return "redirect:/user/login"; // 관리자가 아니면 로그인 페이지로 리다이렉트
        }
        commentService.hideComment(commentId); // 댓글 숨김 처리 서비스 호출
        return "redirect:/board/view/" + commentService.getPostIdByCommentId(commentId); // 숨긴 후 게시글로 리다이렉트
    }

    @DeleteMapping("/post/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, HttpSession session) {
        Integer permission = (Integer) session.getAttribute("permission");
        if (permission == null || permission != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다."); // 관리자가 아닐 경우
        }
        try {
            postService.deletePost(postId); // 게시글 삭제 서비스 호출
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, HttpSession session) {
        Integer permission = (Integer) session.getAttribute("permission");
        if (permission == null || permission != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다."); // 관리자가 아닐 경우
        }
        try {
            commentService.deleteComment(commentId); // 댓글 삭제 서비스 호출
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류가 발생했습니다.");
        }
    }

    
    @PatchMapping("/post/unhide/{postId}")
    public ResponseEntity<String> unhidePost(@PathVariable Long postId, HttpSession session) {
        Integer permission = (Integer) session.getAttribute("permission");
        if (permission == null || permission != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다."); // 관리자가 아닐 경우
        }
        try {
            postService.unhidePost(postId); // 게시글 숨김 해제
            return ResponseEntity.ok("게시글 숨김 해제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 숨김 해제 중 오류가 발생했습니다.");
        }
    }

    @PatchMapping("/comment/unhide/{commentId}")
    public ResponseEntity<String> unhideComment(@PathVariable Long commentId, HttpSession session) {
        Integer permission = (Integer) session.getAttribute("permission");
        if (permission == null || permission != 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다."); // 관리자가 아닐 경우
        }
        try {
            commentService.unhideComment(commentId); // 댓글 숨김 해제
            return ResponseEntity.ok("댓글 숨김 해제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 숨김 해제 중 오류가 발생했습니다.");
        }
    }


    
    @PostMapping("/checkid")
    @ResponseBody
    public Map<String, String> checkUsernameDuplicate(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        boolean isDuplicate = userService.checkUsernameDuplicate(username);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", isDuplicate ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");
        return response;
    }
    
    @PostMapping("/findId")
    public ResponseEntity<String> findIdByNameAndEmail(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        Optional<User> user = userService.findByNameAndEmail(name, email);
        
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getUsername());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching user found.");
        }
    }

    @PostMapping("/findPass")
    public ResponseEntity<String> findPasswordByDetails(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String name = request.get("name");
        LocalDate birthdate = LocalDate.parse(request.get("birthdate"));
        String email = request.get("email");
        
        Optional<User> user = userService.findByUsernameAndNameAndBirthdateAndEmail(username, name, birthdate, email);
        
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getPassword()); // Consider security: send a reset link or temporary password instead
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching user found.");
        }
    }
    
    @GetMapping("/kakaoSignup")
    public String kakaoSignup(HttpSession session, Model model) {
        // 세션에서 카카오 정보를 가져와 뷰로 전달
        model.addAttribute("kakaoId", session.getAttribute("kakao_id"));
        model.addAttribute("nickname", session.getAttribute("nickname"));
        model.addAttribute("email",session.getAttribute("email"));
        return "kakaoSignup";  // 카카오 회원가입 페이지
    }
    @PostMapping("/kakaoSignup")
    public String saveKakaoUser(@ModelAttribute UserDTO userDTO, HttpSession session) {
        userDTO.setUsername(((String)session.getAttribute("kakao_id")));
        userDTO.setPassword("kakaologinUSER");
        
    	userService.save(userDTO);
        session.invalidate(); // 가입 후 세션 초기화
        return "redirect:/main";
    }
    @PostMapping("/clear-error")
    @ResponseBody
    public ResponseEntity<Void> clearError(HttpSession session) {
        session.removeAttribute("error");
        return ResponseEntity.ok().build();
    }
    
    // 사용자 이름 조회 API
    @GetMapping("/{userId}/name")
    public ResponseEntity<String> getUserName(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(user -> ResponseEntity.ok(user.getName())) // 이름 반환
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("사용자를 찾을 수 없습니다."));
    }
}