package com.example.ourapp.groupmap;

import com.example.ourapp.entity.GroupMapPoint;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupMapPointRepository extends JpaRepository<GroupMapPoint, Long> {
    List<GroupMapPoint> findByGroupId(Long groupId);
    @Query("SELECT DISTINCT g.markerColor FROM GroupMapPoint g WHERE g.groupId = :groupId")
    List<String> findDistinctMarkerColorsByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT DISTINCT g.category FROM GroupMapPoint g WHERE g.groupId = :groupId")
    List<String> findDistinctCategoriesByGroupId(@Param("groupId") Long groupId);

    List<GroupMapPoint> findByGroupIdAndCategory(Long groupId, String category);

    List<GroupMapPoint> findByGroupIdAndMarkerColor(Long groupId, String color);
}