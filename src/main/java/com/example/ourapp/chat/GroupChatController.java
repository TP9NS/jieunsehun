package com.example.ourapp.chat;



import com.example.ourapp.entity.GroupChat;
import com.example.ourapp.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groupchat")
public class GroupChatController {
	@Autowired
	private UserRepository userRepository;
    @Autowired
    private GroupChatRepository groupChatRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        String username = (String) session.getAttribute("username"); // 사용자 이름 저장
        Long groupId = Long.parseLong(request.get("groupId"));
        String message = request.get("message");
        String Name = userRepository.findById(userId)
                .map(user -> user.getName()) // 이름 컬럼을 반환
                .orElse("Unknown User"); // 사용자 정보가 없을 경우 기본값 설정
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "로그인이 필요합니다."));
        }
        System.out.println(Name);
        GroupChat chatMessage = new GroupChat(groupId, userId, username,Name, message);
        groupChatRepository.save(chatMessage);

        return ResponseEntity.ok(Map.of("message", "메시지가 전송되었습니다."));
    }

    @GetMapping("/history")
    public ResponseEntity<List<GroupChat>> getChatHistory(@RequestParam Long groupId) {
        List<GroupChat> chatHistory = groupChatRepository.findByGroupIdOrderByTimestampAsc(groupId);
       
        return ResponseEntity.ok(chatHistory);
    }
}
