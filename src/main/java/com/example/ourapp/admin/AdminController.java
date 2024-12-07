package com.example.ourapp.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ourapp.entity.User;
import com.example.ourapp.user.UserRepository;
import com.example.ourapp.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private  UserService userService;
	@Autowired
	private  UserRepository userRepository;
    @GetMapping("/manageuser")
    public String manageuser(HttpSession session, Model model) {
        // Check user permission
        Integer permission = (Integer) session.getAttribute("permission");

        if (permission == null || permission != 0) {
            return "redirect:/user/login";
        }
        return "manageUser";
        }
    @GetMapping("/searchUsers")
    @ResponseBody
    public List<User> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body("User deleted successfully");
    }
//유저 관리에서 정지
    @PostMapping("/suspendUser")
    public ResponseEntity<?> suspendUser(@RequestParam Long id, @RequestParam int days) {
        userService.suspendUser(id, days);
        return ResponseEntity.ok().body("User suspended successfully");
    }
    @PostMapping("/users/{userId}/unsuspend")
    public ResponseEntity<?> unsuspendUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(user -> {
                user.setDate(null); // 정지 해제: 정지 날짜를 null로 설정
                userRepository.save(user);
                return ResponseEntity.ok("정지가 해제되었습니다.");
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."));
    }
    //신고목록에서 정지
    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<?> suspendUser(@PathVariable Long userId, @RequestBody Map<String, Integer> requestBody) {
        int days = requestBody.get("days");

        return userRepository.findById(userId)
            .map(user -> {
                user.setDate(LocalDateTime.now().plusDays(days)); // 정지 기간 설정
                userRepository.save(user);
                return ResponseEntity.ok("사용자가 정지되었습니다.");
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."));
    }
}
