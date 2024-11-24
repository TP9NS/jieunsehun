package com.example.ourapp.groupmap;


import com.example.ourapp.entity.GroupMapPoint;

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
}