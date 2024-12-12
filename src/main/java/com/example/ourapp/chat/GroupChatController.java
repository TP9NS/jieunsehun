package com.example.ourapp.chat;



import com.example.ourapp.entity.GroupChat;
import com.example.ourapp.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendMessageWithImage(
            @RequestParam("groupId") Long groupId,
            @RequestParam("message") String message,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("user_id");
            String username = (String) session.getAttribute("username");
            String name = userRepository.findById(userId)
                    .map(user -> user.getName())
                    .orElse("Unknown User");

            if (userId == null) {
                return ResponseEntity.badRequest().body("로그인이 필요합니다.");
            }

            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                // 파일 저장 경로 설정
                String uploadDir = "C:\\Users\\pshcc\\eclipse-workspace\\jieunsehun\\src\\main\\resources\\static\\uploads\\";
                //String uploadDir = "C:\\Users\\82104\\git\\jieunsehun\\src\\main\\resources\\static\\uploads\\";
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);

                Files.createDirectories(filePath.getParent()); // 디렉토리 생성
                file.transferTo(filePath.toFile()); // 파일 저장

                // 클라이언트에서 접근 가능한 URL 생성
                imageUrl = "/uploads/" + fileName;
            }

            // 채팅 메시지 생성
            GroupChat chatMessage = new GroupChat(groupId, userId, username, name, message, imageUrl);
            groupChatRepository.save(chatMessage);

            // SSE를 통해 실시간으로 메시지 알림
            notifyEmitters(chatMessage);

            return ResponseEntity.ok(Map.of("message", "메시지가 전송되었습니다."));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메시지 전송 실패");
        }
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
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 파일 저장 경로 설정 (Spring Boot 정적 리소스 경로)
            String uploadDir = "C:\\Users\\pshcc\\eclipse-workspace\\jieunsehun\\src\\main\\resources\\static\\uploads\\";
        	//String uploadDir = "C:\\Users\\82104\\git\\jieunsehun\\src\\main\\resources\\static\\uploads\\";
        	String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            // 디렉토리 생성
            Files.createDirectories(filePath.getParent());

            // 파일 저장
            file.transferTo(filePath.toFile());

            // 클라이언트에서 접근 가능한 URL 생성
            String imageUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }
 }

