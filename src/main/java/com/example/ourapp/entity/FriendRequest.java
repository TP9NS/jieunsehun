package com.example.ourapp.entity;

import com.example.ourapp.friends.FriendRequestStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // 요청을 보낸 사용자

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver; // 요청을 받은 사용자

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status; // 요청 상태 (PENDING, ACCEPTED, DECLINED)

    public FriendRequest(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = FriendRequestStatus.PENDING;
    }
}
