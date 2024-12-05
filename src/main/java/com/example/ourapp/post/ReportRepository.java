package com.example.ourapp.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.Report;
import com.example.ourapp.entity.Report.ReportType;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // 특정 타입의 신고 내역 조회 (POST 또는 COMMENT)
    List<Report> findByType(ReportType type);

    // 특정 대상 ID의 신고 내역 조회
    List<Report> findByTargetId(Long targetId);
}
