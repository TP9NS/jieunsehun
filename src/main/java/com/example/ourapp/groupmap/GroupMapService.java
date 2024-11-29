package com.example.ourapp.groupmap;


import com.example.ourapp.DTO.GroupMapPointDTO;
import com.example.ourapp.entity.GroupMapPoint;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupMapService {

    private final GroupMapPointRepository groupMapPointRepository;

    @Autowired
    public GroupMapService(GroupMapPointRepository groupMapPointRepository) {
        this.groupMapPointRepository = groupMapPointRepository;
    }

    // 특정 그룹의 장소 데이터 가져오기
    public List<GroupMapPoint> getGroupMapPointsByGroupId(Long groupId) {
        return groupMapPointRepository.findByGroupId(groupId);
    }
    
    public void lockGroupMapPoint(Long pointId) {
    	 System.out.print("여기까지 ㅇㅋ3");
        GroupMapPoint point = groupMapPointRepository.findById(pointId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found"));

        if (point.isLocked()) {
            throw new IllegalStateException("이미 다른 사용자가 수정 중입니다.");
        }
       
        point.setLocked(true);
        groupMapPointRepository.save(point);
    }

    public void unlockGroupMapPoint(Long pointId) {
        GroupMapPoint point = groupMapPointRepository.findById(pointId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found"));

        point.setLocked(false);
        groupMapPointRepository.save(point);
    }
 // 모든 GroupMapPoint 데이터를 가져오기
    public List<GroupMapPointDTO> getAllGroupMapPoints() {
        List<GroupMapPoint> groupMapPoints = groupMapPointRepository.findAll(); // 데이터베이스에서 조회
        return groupMapPoints.stream().map(groupMapPoint -> {
            GroupMapPointDTO dto = new GroupMapPointDTO();
            dto.setUserId(groupMapPoint.getUserId());
            dto.setCategory(groupMapPoint.getCategory());
            dto.setLocationName(groupMapPoint.getLocationName());
            dto.setLocationAlias(groupMapPoint.getLocationAlias());
            dto.setLocationDesc(groupMapPoint.getLocationDesc());
            dto.setLatitude(groupMapPoint.getLatitude());
            dto.setLongitude(groupMapPoint.getLongitude());
            dto.setAddress(groupMapPoint.getAddress());
            dto.setPhone(groupMapPoint.getPhone());
            dto.setSearchTime(groupMapPoint.getSearchTime());
            dto.setMarkerColor(groupMapPoint.getMarkerColor());
            return dto;
        }).collect(Collectors.toList());
    }
}