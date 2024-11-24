package com.example.ourapp.groupmap;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ourapp.DTO.GroupMapPointDTO;
import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.GroupMapPoint;
import com.example.ourapp.group.GroupRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class GroupMapController {
	
    @GetMapping("/groupmap")
    public String getGroupMapPage(HttpSession session) {
        System.out.println(session.getAttribute("user_id"));
        return "groupmap";
    }
    @GetMapping("/getgroupmappoint")
    public ResponseEntity<?> getGroupMapPoints(@RequestParam Long groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }
        System.out.println(""+groupId);
        List<GroupMapPoint> groupMapPoints = groupMapPointRepository.findByGroupId(groupId);
        return ResponseEntity.ok(groupMapPoints);
    }
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	GroupMapPointRepository groupMapPointRepository;
	
	
	@PostMapping("/saveGroupMapPoint")
	public ResponseEntity<?> saveGroupMapPoint(@RequestBody GroupMapPointDTO groupMapPointDto, HttpSession session) {
	    Long userId = (Long) session.getAttribute("user_id");

	    if (userId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
	    }

	    // 그룹 존재 여부 확인
	    Group group = groupRepository.findById(groupMapPointDto.getGroupId())
	            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

	    // 그룹 지도에 저장
	    GroupMapPoint groupMapPoint = new GroupMapPoint(
	        userId,
	        group.getGroupId(),
	        groupMapPointDto.getCategory(),
	        groupMapPointDto.getLocationName(),
	        groupMapPointDto.getLocationDesc(),
	        groupMapPointDto.getLatitude(),
	        groupMapPointDto.getLongitude()
	    );

	    groupMapPointRepository.save(groupMapPoint);

	    return ResponseEntity.ok(Map.of("message", "그룹 지도에 장소가 성공적으로 저장되었습니다."));
	}
}
