package com.example.ourapp.chat;



import com.example.ourapp.entity.GroupChat;
import com.example.ourapp.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/groupchat")
public class GroupChatController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupChatRepository groupChatRepository;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        String username = (String) session.getAttribute("username");
        String name = userRepository.findById(userId)
                .map(user -> user.getName())
                .orElse("Unknown User");
        Long groupId = Long.parseLong(request.get("groupId"));
        String message = request.get("message");

        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "로그인이 필요합니다."));
        }

        GroupChat chatMessage = new GroupChat(groupId, userId, username, name, message);
        groupChatRepository.save(chatMessage);

        // 새 메시지를 모든 활성 SSE 연결에 전송
        notifyEmitters(chatMessage);

        return ResponseEntity.ok(Map.of("message", "메시지가 전송되었습니다."));
    }

    // SSE 엔드포인트
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessages(@RequestParam Long groupId) {
        SseEmitter emitter = new SseEmitter(0L); // 연결 유지
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    // SSE로 메시지를 전송
    private void notifyEmitters(GroupChat chatMessage) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("chat-message")
                        .data(chatMessage));
            } catch (Exception e) {
                emitters.remove(emitter); // 전송 실패 시 emitter 제거
            }
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<GroupChat>> getChatHistory(@RequestParam Long groupId) {
        List<GroupChat> chatHistory = groupChatRepository.findByGroupIdOrderByTimestampAsc(groupId);
        return ResponseEntity.ok(chatHistory);
    }
}
