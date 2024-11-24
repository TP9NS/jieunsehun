package com.example.ourapp.map;

import com.example.ourapp.entity.AdminMapPoint;
import com.example.ourapp.entity.MyMapPoint;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
public class MapController {

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private AdminMapPointRepository adminMapPointRepository;

    @GetMapping("/mymap")
    public String getMyMapPage(HttpSession session) {
        System.out.println(session.getAttribute("user_id"));
        return "mymap";
    }
    

    
    @GetMapping("/api/topics")
    @ResponseBody
    public List<String> getUniqueTopics() {
        return adminMapPointRepository.findDistinctTopics();
    }

    @GetMapping("/api/places")
    @ResponseBody
    public List<AdminMapPoint> getPlacesByTopic(@RequestParam String topic) {
        return adminMapPointRepository.findByTopic(topic);
    }


    @GetMapping("/api/mymap")
    @ResponseBody
    public List<MyMapPoint> getMyMapPoints(HttpSession session) {
        Long user_id = (Long) session.getAttribute("user_id");
        System.out.println(user_id + "asasd");
        List<MyMapPoint> points = mapRepository.findByUserId(user_id);
        points.forEach(point -> System.out.println(point.getLocationDesc()));
        return points;
    }

    // 삭제 엔드포인트
    @DeleteMapping("/api/mymap/delete")
    @ResponseBody
    public ResponseEntity<String> deleteMyMapPoint(@RequestParam("id") Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        
        Optional<MyMapPoint> point = mapRepository.findById(id);
        if (point.isPresent() && point.get().getUserId().equals(userId)) {
            mapRepository.deleteById(id);
            return ResponseEntity.ok("Location deleted successfully");
        } else {
            return ResponseEntity.status(403).body("Unauthorized to delete this location");
        }
    }

    @PutMapping("/api/mymap/update")
    @ResponseBody
    public ResponseEntity<String> editMyMapPoint(
            @RequestParam("id") Long id,
            @RequestBody MyMapPoint updatedPoint,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        
        Optional<MyMapPoint> existingPoint = mapRepository.findById(id);
        if (existingPoint.isPresent() && existingPoint.get().getUserId().equals(userId)) {
            MyMapPoint point = existingPoint.get();
            point.setLocationAlias(updatedPoint.getLocationAlias());
            point.setLocationDesc(updatedPoint.getLocationDesc());
            mapRepository.save(point);
            return ResponseEntity.ok("Location updated successfully");
        } else {
            return ResponseEntity.status(403).body("Unauthorized to edit this location");
        }
    }
 // ========== 관리자 엔드포인트 추가 ==========

    // 관리자가 모든 장소 조회
    @GetMapping("/admin/api/allmap")
    @ResponseBody
    public List<AdminMapPoint> getAllMapPoints() {
        return adminMapPointRepository.findAll();  // 모든 장소 반환
    }

    // 관리자 삭제 엔드포인트
    @DeleteMapping("/admin/api/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAnyMapPoint(@RequestParam("id") Long id) {
        if (adminMapPointRepository.existsById(id)) {
        	adminMapPointRepository.deleteById(id);
            return ResponseEntity.ok("Location deleted by admin successfully");
        } else {
            return ResponseEntity.status(404).body("Location not found");
        }
    }

    @PutMapping("/admin/api/update")
    @ResponseBody
    public ResponseEntity<String> editAnyMapPoint(@RequestBody AdminMapPoint updatedPoint) {
        Long id = updatedPoint.getId(); // @RequestBody로 받은 id 사용

        Optional<AdminMapPoint> existingPoint = adminMapPointRepository.findById(id);
        if (existingPoint.isPresent()) {
            AdminMapPoint point = existingPoint.get();
            point.setTopic(updatedPoint.getTopic());
            point.setLocationAlias(updatedPoint.getLocationAlias());
            point.setLocationDesc(updatedPoint.getLocationDesc());
            point.setImageUrl(updatedPoint.getImageUrl());
            adminMapPointRepository.save(point);
            return ResponseEntity.ok("Location updated by admin successfully");
        } else {
            return ResponseEntity.status(404).body("Location not found");
        }
    }


}
