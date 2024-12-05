package com.example.ourapp.post;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.ReportedCommentDTO;
import com.example.ourapp.DTO.ReportedPostDTO;
import com.example.ourapp.entity.Report;
import com.example.ourapp.entity.Report.ReportType;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void reportPost(Post post, String reason) {
        Report report = new Report(post.getId(), reason, ReportType.POST);
        report.setPost(post); // 관계 매핑
        reportRepository.save(report);
    }

    public void reportComment(Comment comment, String reason) {
        Report report = new Report(comment.getId(), reason, ReportType.COMMENT);
        report.setComment(comment); // 관계 매핑
        reportRepository.save(report);
    }

    public List<Report> getReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }

    public List<Report> getReportsByTargetId(Long targetId) {
        return reportRepository.findByTargetId(targetId);
    }

    public List<ReportedPostDTO> getAllReportedPosts() {
        return reportRepository.findAllReportedPosts();
    }

    public List<ReportedCommentDTO> getAllReportedComments() {
        return reportRepository.findAllReportedComments();
    }

    public List<ReportedPostDTO> getReportsByUsername(String username, ReportType type) {
        return reportRepository.findReportsByUsername(username, type);
    }

    public List<ReportedPostDTO> getReportsByReason(String reason, ReportType type) {
        return reportRepository.findReportsByReason(reason, type);
    }

    public void ignoreReport(Long reportId) {
        if (reportRepository.existsById(reportId)) {
            reportRepository.deleteById(reportId);
        } else {
            throw new IllegalArgumentException("Report ID not found: " + reportId);
        }
    }

    public void deletePost(Long postId) {
        if (reportRepository.existsByTargetIdAndType(postId, ReportType.POST)) {
            reportRepository.deleteByTargetIdAndType(postId, ReportType.POST);
        } else {
            throw new IllegalArgumentException("No reports found for post ID: " + postId);
        }
    }

    public void deleteComment(Long commentId) {
        if (reportRepository.existsByTargetIdAndType(commentId, ReportType.COMMENT)) {
            reportRepository.deleteByTargetIdAndType(commentId, ReportType.COMMENT);
        } else {
            throw new IllegalArgumentException("No reports found for comment ID: " + commentId);
        }
    }
}
