package com.example.ourapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 현재 사용자

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend; // 친구로 등록된 사용자

    public Friends(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }
}
