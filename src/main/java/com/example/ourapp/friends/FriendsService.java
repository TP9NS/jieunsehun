package com.example.ourapp.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.FriendRequestDTO;
import com.example.ourapp.DTO.FriendRequestDetailDTO;
import com.example.ourapp.DTO.FriendsDTO;
import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.entity.FriendRequest;
import com.example.ourapp.entity.Friends;
import com.example.ourapp.entity.Message;
import com.example.ourapp.entity.User;
import com.example.ourapp.user.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private HttpSession httpSession; // 세션 주입
    
    @Autowired
    private MessageRepository messageRepository;

    /**
     * 현재 사용자의 친구 목록 조회
     */
    public List<Map<String, String>> getFriends() {
        Long currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Friends> friends = friendsRepository.findByUser(currentUser);

        // 디버그: 반환된 친구 목록 확인
        System.out.println("Friends retrieved: " + friends);

        return friends.stream()
                .map(friend -> {
                    User friendUser = friend.getFriend();
                    if (friendUser == null) {
                        System.out.println("Friend entity is null for friend: " + friend);
                        throw new IllegalStateException("Friend entity is null.");
                    }

                    Map<String, String> friendInfo = new HashMap<>();
                    friendInfo.put("id", String.valueOf(friendUser.getUserId()));
                    friendInfo.put("name", friendUser.getName());
                    friendInfo.put("email", friendUser.getEmail());
                    System.out.println("Friend Info: " + friendInfo); // 디버그: 친구 정보
                    return friendInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 친구 요청 전송
     */
    public void sendFriendRequest(Long receiverId) {
        System.out.println("Sending friend request to receiver ID: " + receiverId);
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        User sender = getCurrentUser();
        System.out.println("Current user: " + sender.getUserId());

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new IllegalArgumentException("Friend request already sent.");
        }

        friendRequestRepository.save(new FriendRequest(sender, receiver));
        System.out.println("Friend request saved successfully.");
    }


    /**
     * 친구 요청 수락
     */
    public void acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!request.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new IllegalStateException("Friend request is not pending.");
        }

        friendsRepository.save(new Friends(request.getSender(), request.getReceiver()));
        friendsRepository.save(new Friends(request.getReceiver(), request.getSender()));

        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);
    }
    public void declineFriendRequest(Long requestId) {
        // FriendRequest를 ID로 조회
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // 요청 상태가 PENDING인지 확인
        if (!request.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new IllegalStateException("Friend request is not pending.");
        }

        // 요청 상태를 DECLINED로 설정
        request.setStatus(FriendRequestStatus.DECLINED);
        friendRequestRepository.save(request);
    }

    public List<UserDTO> searchUsers(String query) {
        System.out.println("Executing search for: " + query); // 디버깅 로그 추가

        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(user -> new UserDTO(user.getUserId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }
    private User getCurrentUser() {
        Long currentUserId = getCurrentUserId();
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
    }

    /**
     * 세션에서 사용자 ID 가져오기
     */
    public Long getCurrentUserId() {
        Long userId = (Long) httpSession.getAttribute("user_id");
        if (userId == null) {
            throw new IllegalStateException("User is not logged in.");
        }
        return userId;
    }
    public List<FriendRequestDetailDTO> getPendingFriendRequests(Long receiverId) {
        // Receiver의 User 엔티티 조회
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Receiver와 Status를 기준으로 FriendRequest 조회
        return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequestStatus.PENDING)
                .stream()
                .map(request -> {
                    User sender = request.getSender();
                    // 요청 보낸 사용자의 정보를 FriendRequestDetailDTO로 변환하여 반환
                    return new FriendRequestDetailDTO(
                            request.getId(),        // FriendRequest ID
                            sender.getUserId(),     // Sender의 userId
                            sender.getName(),       // Sender의 이름
                            sender.getEmail()       // Sender의 이메일
                    );
                })
                .collect(Collectors.toList());
    }
    public void removeFriend(Long friendId) {
        // 현재 사용자 ID 가져오기
        Long currentUserId = getCurrentUserId();

        // 현재 사용자와 친구 관계 삭제
        Friends currentUserFriend = friendsRepository.findByUserAndFriend(
                userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("User not found")),
                userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Friend not found"))
        ).orElseThrow(() -> new IllegalArgumentException("Friend relationship not found"));
        friendsRepository.delete(currentUserFriend);

        // 친구의 입장에서의 관계 삭제
        Friends friendUserFriend = friendsRepository.findByUserAndFriend(
                userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Friend not found")),
                userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("User not found"))
        ).orElseThrow(() -> new IllegalArgumentException("Friend relationship not found"));
        friendsRepository.delete(friendUserFriend);
    }
    public Long getSenderIdByRequestId(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));
        return request.getSender().getUserId(); // 요청 보낸 사용자의 ID 반환
    }
    public Long getSenderIdFromRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));
        return request.getSender().getUserId();
    }
    
    public void sendMessage(String senderId, String receiverId, String content) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }

}
