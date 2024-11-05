package com.example.ourapp.map;
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
}
