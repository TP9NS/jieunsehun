package com.example.ourapp.map;

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

    @GetMapping("/mymap")
    public String getMyMapPage(HttpSession session) {
        System.out.println(session.getAttribute("user_id"));
        return "mymap";  // HTML 파일의 확장자는 생략합니다.
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
}
