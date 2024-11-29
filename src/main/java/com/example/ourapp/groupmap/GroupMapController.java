package com.example.ourapp.groupmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ourapp.DTO.GroupMapPointDTO;
import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.GroupMapPoint;
import com.example.ourapp.entity.GroupMember;
import com.example.ourapp.entity.User;
import com.example.ourapp.group.GroupMemberRepository;
import com.example.ourapp.group.GroupRepository;
import com.example.ourapp.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class GroupMapController {
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	GroupMapPointRepository groupMapPointRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	GroupMemberRepository groupMemberRepository;
	@Autowired
	GroupMapService groupMapService;
	
    @GetMapping("/groupmap")
    public String getGroupMapPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        model.addAttribute("userId", userId);
        return "groupmap";
    }
    @GetMapping("/getgroupmappoint")
    public ResponseEntity<?> getGroupMapPoints(@RequestParam Long groupId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }

        // 현재 사용자의 그룹 내 권한 조회
        Optional<GroupMember> member = groupMemberRepository.findByGroup_groupIdAndUser_userId(groupId, userId);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "해당 그룹의 멤버가 아닙니다."));
        }

        // GroupMapPoint 정보 가져오기
        List<GroupMapPoint> points = groupMapPointRepository.findByGroupId(groupId);

        // 응답 데이터 구성
        List<Map<String, Object>> response = points.stream().map(point -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", point.getId());
            map.put("address",point.getAddress());
            map.put("userId", point.getUserId());
            map.put("groupId", point.getGroupId());
            map.put("category", point.getCategory());
            map.put("alias",point.getLocationAlias());
            map.put("locationName", point.getLocationName());
            map.put("locationDesc", point.getLocationDesc());
            map.put("latitude", point.getLatitude());
            map.put("longitude", point.getLongitude());
            map.put("searchTime", point.getSearchTime());

            // 추가한 사용자 이름
            String addedBy = userRepository.findById(point.getUserId())
                                           .map(User::getName)
                                           .orElse("알 수 없음");
            map.put("addedBy", addedBy);

            // 현재 사용자의 그룹 내 권한 추가
            map.put("userPermission", member.get().getPermission());

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

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
            groupMapPointDto.getLongitude(),
            groupMapPointDto.getMarkerColor() // markerColor를 추가
        );

        groupMapPoint.setLocationAlias(groupMapPointDto.getLocationAlias());
        groupMapPoint.setPhone(groupMapPointDto.getPhone());
        groupMapPoint.setAddress(groupMapPointDto.getAddress());

        groupMapPointRepository.save(groupMapPoint);

        return ResponseEntity.ok(Map.of("message", "그룹 지도에 장소가 성공적으로 저장되었습니다."));
    }


	
    @PutMapping("/groupmap/update")
    public ResponseEntity<?> updateGroupMapPoint(@RequestParam Long id, @RequestBody Map<String, String> updates, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        GroupMapPoint groupMapPoint = groupMapPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소를 찾을 수 없습니다."));

        groupMapPoint.setLocationAlias(updates.get("locationAlias"));
        groupMapPoint.setLocationDesc(updates.get("locationDesc"));
        groupMapPointRepository.save(groupMapPoint);

        return ResponseEntity.ok(Map.of("message", "수정되었습니다."));
    }

    @DeleteMapping("/groupmap/delete")
    public ResponseEntity<?> deleteGroupMapPoint(@RequestParam Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        System.out.println("호추우우루 삭제");
        groupMapPointRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    }
    
 // 수정 락 설정
    @PostMapping("/groupmap/lock")
    public ResponseEntity<?> lockGroupMapPoint(@RequestParam Long pointId) {
    	 System.out.print("여기까지 ㅇㅋ"+pointId);
        try {
        	 System.out.print("여기까지 ㅇㅋ2");
            groupMapService.lockGroupMapPoint(pointId);
            System.out.print("여기까지 ㅇㅋ4");
            return ResponseEntity.ok().body("락 설정 완료");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 다른 사용자가 수정 중입니다.");
        } catch (Exception e) {
        	System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("락 설정 중 오류 발생");
        }
    }

    // 수정 락 해제
    @PostMapping("/groupmap/unlock")
    public ResponseEntity<?> unlockGroupMapPoint(@RequestParam Long pointId) {
        try {
            groupMapService.unlockGroupMapPoint(pointId);
            return ResponseEntity.ok().body("락 해제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("락 해제 중 오류 발생");
        }
    }
}
