package com.example.ourapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "my_map_point")
public class MyMapPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "address")
    private String address;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(nullable = false)
    private String category;

    @Column(name = "location_name", nullable = false)
    private String locationName;
    
    @Column(name = "location_alias", nullable = false)
    private String locationAlias;

    @Column(name = "location_desc", nullable = false, length = 500)
    private String locationDesc;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "search_time", nullable = false)
    private LocalDateTime searchTime;
    
    @Column(name = "marker_color", nullable = false)
    private String markerColor;

    public MyMapPoint() {}

    public MyMapPoint(Long userId, String category, String locationName, String locationDesc, Double latitude, Double longitude, String markerColor) {
        this.userId = userId;
        this.category = category;
        this.locationName = locationName;
        this.locationDesc = locationDesc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.searchTime = LocalDateTime.now();
        this.markerColor = markerColor;
    }
}
