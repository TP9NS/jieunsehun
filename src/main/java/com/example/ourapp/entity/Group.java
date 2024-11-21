package com.example.ourapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`groups`")
@Getter
@Setter
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", length = 50, nullable = false)
    private String groupName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // 만든 사람

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members; // 그룹 구성원 및 권한 정보
    
    public String getGroupName() {
        return groupName;
    }
    
    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", createdBy=" + createdBy.getUsername() +  // createdBy가 User 객체라면 User의 username을 출력
                ", createdDate=" + createdDate +
                '}';
    }
}
