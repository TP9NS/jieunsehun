package com.example.ourapp.map;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    
 // 색상 목록 제공
    public List<Map<String, String>> getAvailableMarkerColors() {
        List<MyMapPoint> points = myMapPointRepository.findAll(); // 모든 데이터 가져오기
        Set<String> colors = new HashSet<>();

        // 데이터에서 색상 추출
        for (MyMapPoint point : points) {
            if (point.getMarkerColor() != null && !point.getMarkerColor().isBlank()) {
                colors.add(point.getMarkerColor());
            }
        }
        System.out.println("Extracted Colors from Database: " + colors);

        // 중복 제거 후 JSON 형태로 매핑
        return colors.stream()
                .map(color -> Map.of("value", color, "label", convertColorToKoreanLabel(color))) // JSON 형태로 매핑
                .sorted(Comparator.comparing(map -> map.get("label"))) // 알파벳 순 정렬
                .toList();
    }

    // 색상을 한글 레이블로 변환하는 도우미 메서드
    private String convertColorToKoreanLabel(String color) {
        return switch (color.toLowerCase()) {
            case "red" -> "빨강";
            case "orange" -> "주황";
            case "yellow" -> "노랑";
            case "green" -> "초록";
            case "blue" -> "파랑";
            default -> color; // 기본적으로 영어 색상명 반환
        };
    }


    // 카테고리 목록 제공
    public List<Map<String, String>> getDynamicCategories() {
        List<MyMapPoint> points = myMapPointRepository.findAll(); // 모든 데이터 가져오기
        Set<String> categories = new HashSet<>();

        // 데이터에서 카테고리 추출
        for (MyMapPoint point : points) {
            String[] categoryParts = point.getCategory().split(" > ");
            Collections.addAll(categories, categoryParts); // 계층별 카테고리 추가
        }
        System.out.println("Extracted Categories from Database: " + categories);
        // 중복 제거 후 리스트로 변환
        return categories.stream()
                .map(cat -> Map.of("value", cat, "label", cat)) // JSON 형태로 매핑
                .sorted(Comparator.comparing(map -> map.get("label"))) // 알파벳 순 정렬
                .toList();
    }
    
    public List<MyMapPoint> getLocationsByCategory(String category) {
        // Repository에서 모든 데이터를 가져온 후 필터링
        List<MyMapPoint> allLocations = myMapPointRepository.findAll();

        // 카테고리가 정확히 일치하거나 계층적 포함 관계인 항목만 필터링
        return allLocations.stream()
                .filter(location -> {
                    String[] categoryLevels = location.getCategory().split(" > "); // 카테고리 계층 분리
                    System.out.println(categoryLevels[1]);
                    for (String level : categoryLevels) {
                        if (level.equals(category)) { // 카테고리와 일치 확인
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList()); // 필터링된 데이터 반환
    }
}
