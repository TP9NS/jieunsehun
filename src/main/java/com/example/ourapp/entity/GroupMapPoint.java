package com.example.ourapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "group_map_point")
public class GroupMapPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private String category;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "location_desc", nullable = false, length = 500)
    private String locationDesc;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "search_time", nullable = false)
    private LocalDateTime searchTime;

    // Constructors, Getters and Setters
    public GroupMapPoint() {}

    public GroupMapPoint(Long userId, Long groupId, String category, String locationName, String locationDesc, Double latitude, Double longitude) {
        this.userId = userId;
        this.groupId = groupId;
        this.category = category;
        this.locationName = locationName;
        this.locationDesc = locationDesc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.searchTime = LocalDateTime.now();
    }

    // getters and setters
}
