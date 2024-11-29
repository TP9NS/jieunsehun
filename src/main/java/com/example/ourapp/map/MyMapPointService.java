package com.example.ourapp.map;

import java.util.List;
import java.util.stream.Collectors;

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
    
 // 모든 MyMapPoint 데이터를 가져오기
    public List<MyMapPointDTO> getAllMyMapPoints() {
        List<MyMapPoint> myMapPoints = myMapPointRepository.findAll(); // 데이터베이스에서 조회
        return myMapPoints.stream().map(myMapPoint -> {
            MyMapPointDTO dto = new MyMapPointDTO();
            dto.setUserId(myMapPoint.getUserId());
            dto.setCategory(myMapPoint.getCategory());
            dto.setLocationName(myMapPoint.getLocationName());
            dto.setLocationAlias(myMapPoint.getLocationAlias());
            dto.setLocationDesc(myMapPoint.getLocationDesc());
            dto.setLatitude(myMapPoint.getLatitude());
            dto.setLongitude(myMapPoint.getLongitude());
            dto.setAddress(myMapPoint.getAddress());
            dto.setPhone(myMapPoint.getPhone());
            dto.setSearchTime(myMapPoint.getSearchTime());
            dto.setMarkerColor(myMapPoint.getMarkerColor());
            return dto;
        }).collect(Collectors.toList());
    }
}
