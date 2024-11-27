package com.example.ourapp.map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.MyMapPointDTO;
import com.example.ourapp.entity.MyMapPoint;

@Service
public class MyMapPointService {

    private final MapRepository myMapPointRepository;

    @Autowired
    public MyMapPointService(MapRepository myMapPointRepository) {
        this.myMapPointRepository = myMapPointRepository;
    }

    public void saveMyMapPoint(MyMapPointDTO myMapPointDTO) {
        MyMapPoint myMapPoint = new MyMapPoint(); // DTO를 엔티티로 변환
        myMapPoint.setUserId(myMapPointDTO.getUserId());
        myMapPoint.setCategory(myMapPointDTO.getCategory());
        myMapPoint.setLocationName(myMapPointDTO.getLocationName());
        myMapPoint.setLocationAlias(myMapPointDTO.getLocationAlias());
        myMapPoint.setLocationDesc(myMapPointDTO.getLocationDesc());
        myMapPoint.setLatitude(myMapPointDTO.getLatitude());
        myMapPoint.setLongitude(myMapPointDTO.getLongitude());
        myMapPoint.setAddress(myMapPointDTO.getAddress());
        myMapPoint.setPhone(myMapPointDTO.getPhone());
        myMapPoint.setSearchTime(myMapPointDTO.getSearchTime());
        // markerColor 설정
        if (myMapPointDTO.getMarkerColor() == null || myMapPointDTO.getMarkerColor().isEmpty()) {
            myMapPoint.setMarkerColor("blue"); // 기본값 설정
        } else {
            myMapPoint.setMarkerColor(myMapPointDTO.getMarkerColor());
        }
        myMapPointRepository.save(myMapPoint);
    }
}
