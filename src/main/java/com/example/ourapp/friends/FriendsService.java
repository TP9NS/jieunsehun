package com.example.ourapp.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.FriendRequestDTO;
import com.example.ourapp.DTO.FriendsDTO;
import com.example.ourapp.DTO.UserDTO;
import com.example.ourapp.entity.FriendRequest;
import com.example.ourapp.entity.Friends;
import com.example.ourapp.entity.User;
import com.example.ourapp.user.UserRepository;

import jakarta.servlet.http.HttpSession;

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

    /**
     * 현재 사용자의 친구 목록 조회
     */
    public List<Map<String, String>> getFriends() {
        Long currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Friends 테이블에서 현재 사용자의 친구 목록을 조회하고 이름과 이메일을 포함한 Map으로 반환
        return friendsRepository.findByUser(currentUser).stream()
                .map(friend -> {
                    User friendUser = friend.getFriend(); // 친구 User 엔티티
                    Map<String, String> friendInfo = new HashMap<>();
                    friendInfo.put("id", String.valueOf(friendUser.getUserId())); // 친구 ID
                    friendInfo.put("name", friendUser.getName()); // 친구 이름
                    friendInfo.put("email", friendUser.getEmail()); // 친구 이메일
                    return friendInfo;
                })
                .collect(Collectors.toList());
    }
    /**
     * 친구 요청 전송
     */
    public void sendFriendRequest(Long receiverId) {
        User currentUser = getCurrentUser();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (friendsRepository.findByUserAndFriend(currentUser, receiver).isPresent()) {
            throw new IllegalStateException("Already friends with this user.");
        }

        FriendRequest request = new FriendRequest(currentUser, receiver);
        friendRequestRepository.save(request);
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
    public List<UserDTO> getPendingFriendRequests(Long receiverId) {
        // Receiver의 User 엔티티 조회
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Receiver와 Status를 기준으로 FriendRequest 조회
        return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequestStatus.PENDING)
                .stream()
                .map(request -> {
                    User sender = request.getSender();
                    // 요청 보낸 사용자의 정보를 UserDTO로 변환하여 반환
                    return new UserDTO(
                            sender.getUserId(), // sender의 userId
                            sender.getName(),   // sender의 이름
                            sender.getEmail()   // sender의 이메일
                    );
                })
                .collect(Collectors.toList());
    }



}
