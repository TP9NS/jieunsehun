package com.example.ourapp.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "group_chat")
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    

    // Constructors, getters, setters
    public GroupChat() {}

    public GroupChat(Long groupId, Long userId, String username, String name,String message,String imageUrl) {
        this.groupId = groupId;
        this.userId = userId;
        this.username = username;
        this.message = message;
        this.name = name;
        this.imageUrl = imageUrl;
        this.timestamp = LocalDateTime.now();
    }
}
