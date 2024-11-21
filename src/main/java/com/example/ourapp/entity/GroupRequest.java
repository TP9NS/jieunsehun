package com.example.ourapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class GroupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 신청한 사용자

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // 신청한 그룹

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // 신청 상태 (PENDING, ACCEPTED, REJECTED)

    // Enum 정의
    public enum RequestStatus {
        대기, 수락, 거절
    }
 // 기본 생성자
    public GroupRequest() {
    }

    // Getter 및 Setter 메서드

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GroupRequest{" +
                "id=" + id +
                ", user=" + user.getUsername() +  // user의 username을 출력
                ", group=" + group.getGroupName() + // group의 groupName을 출력
                ", status=" + status +
                '}';
    }
}
