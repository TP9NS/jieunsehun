package com.example.ourapp.map;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ourapp.DTO.AdminMapPointDTO;
import com.example.ourapp.entity.AdminMapPoint;

@Service
public class AdminMapPointService {

    private final AdminMapPointRepository adminMapPointRepository;

    @Autowired
    public AdminMapPointService(AdminMapPointRepository adminMapPointRepository) {
        this.adminMapPointRepository = adminMapPointRepository;
    }

    @Transactional
    public void saveAdminMapPoint(AdminMapPointDTO adminMapPointDTO) {
        AdminMapPoint adminMapPoint = new AdminMapPoint();
        adminMapPoint.setUserId(adminMapPointDTO.getUserId());
        adminMapPoint.setCategory(adminMapPointDTO.getCategory());
        adminMapPoint.setLocationName(adminMapPointDTO.getLocationName());
        adminMapPoint.setLocationAlias(adminMapPointDTO.getLocationAlias());
        adminMapPoint.setLocationDesc(adminMapPointDTO.getLocationDesc());
        adminMapPoint.setLatitude(adminMapPointDTO.getLatitude());
        adminMapPoint.setLongitude(adminMapPointDTO.getLongitude());
        adminMapPoint.setAddress(adminMapPointDTO.getAddress());
        adminMapPoint.setPhone(adminMapPointDTO.getPhone());
        adminMapPoint.setSearchTime(adminMapPointDTO.getSearchTime());
        adminMapPoint.setTopic(adminMapPointDTO.getTopic()); // 주제 정보 추가

        adminMapPointRepository.save(adminMapPoint);
    }

    // 모든 관리자 장소 가져오기
    public List<AdminMapPointDTO> getAllAdminMapPoints() {
        List<AdminMapPoint> entities = adminMapPointRepository.findAll(); // 모든 데이터를 가져옴
        List<AdminMapPointDTO> dtos = new ArrayList<>();

        for (AdminMapPoint entity : entities) {
            AdminMapPointDTO dto = new AdminMapPointDTO();
            dto.setLocationName(entity.getLocationName());
            dto.setLocationAlias(entity.getLocationAlias());
            dto.setAddress(entity.getAddress());
            dto.setPhone(entity.getPhone());
            dto.setTopic(entity.getTopic());
            dto.setLatitude(entity.getLatitude());
            dto.setLongitude(entity.getLongitude());
            dto.setImageUrl(entity.getImageUrl());
            dtos.add(dto);
        }
        return dtos;
    }

}
