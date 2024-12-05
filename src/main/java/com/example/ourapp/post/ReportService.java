package com.example.ourapp.post;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ourapp.entity.Report;
import com.example.ourapp.entity.Report.ReportType;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void reportPost(Long postId, String reason) {
        Report report = new Report(postId, reason, ReportType.POST);
        reportRepository.save(report);
    }

    public void reportComment(Long commentId, String reason) {
        Report report = new Report(commentId, reason, ReportType.COMMENT);
        reportRepository.save(report);
    }

    public List<Report> getReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }

    public List<Report> getReportsByTargetId(Long targetId) {
        return reportRepository.findByTargetId(targetId);
    }
}
