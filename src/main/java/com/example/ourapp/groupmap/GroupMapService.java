package com.example.ourapp.groupmap;


import com.example.ourapp.entity.GroupMapPoint;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}