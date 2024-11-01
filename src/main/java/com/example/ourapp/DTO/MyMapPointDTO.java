package com.example.ourapp.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyMapPointDTO {
    private Long userId;
    private String category;
    private String locationName;
    private String locationAlias;
    private String locationDesc;
    private Double latitude;
    private Double longitude;
    private String address;
    private String phone;
}
