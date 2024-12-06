package com.example.ourapp.post;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.ReportDTO;
import com.example.ourapp.entity.Report;
import com.example.ourapp.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public void reportPost(Long postId, String reason, Long reportedBy) {
        Report report = new Report(postId, reason, Report.ReportType.POST, reportedBy);
        reportRepository.save(report);
    }

    public void reportComment(Long commentId, String reason, Long reportedBy) {
        // 댓글 ID를 기반으로 게시글 ID 조회
        Long postId = commentRepository.findPostIdByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        Report report = new Report(postId, reason, Report.ReportType.COMMENT, reportedBy);
        reportRepository.save(report);
    }
    
    public Long getPostIdByCommentId(Long commentId) {
        // 댓글 ID로 게시글 ID 조회
        return reportRepository.findPostIdByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
    }

    public List<Report> getReportsByType(Report.ReportType type) {
        return reportRepository.findByType(type);
    }

    public List<Report> getReportsByTargetId(Long targetId) {
        return reportRepository.findByTargetId(targetId);
    }

    public List<ReportDTO> getAllPostReports() {
        return reportRepository.findByType(Report.ReportType.POST)
                .stream()
                .map(report -> {
                    String username = userService.findUserById(report.getReportedBy()).getUsername();
                    return new ReportDTO(
                            report.getId(),
                            report.getTargetId(),
                            report.getReason(),
                            report.getType().name(),
                            report.getReportedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            username,
                            report.getReportedBy()
                    );
                })
                .toList();
    }

    public List<ReportDTO> getAllCommentReports() {
        return reportRepository.findByType(Report.ReportType.COMMENT)
                .stream()
                .map(report -> {
                    String username = userService.findUserById(report.getReportedBy()).getUsername();
                    return new ReportDTO(
                            report.getId(),
                            report.getTargetId(),
                            report.getReason(),
                            report.getType().name(),
                            report.getReportedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            username,
                            report.getReportedBy()
                    );
                })
                .toList();
    }
 // 게시글 신고 삭제
    public void deletePostReport(Long id) {
        reportRepository.deleteById(id);
    }

    // 댓글 신고 삭제
    public void deleteCommentReport(Long id) {
        reportRepository.deleteById(id);
    }
    
    public void deleteReport(String type, Long id) {
        // 삭제 로직: type에 따라 분기 처리
        if (type.equals("post") || type.equals("comment")) {
            reportRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("잘못된 타입입니다: " + type);
        }
    }
    
    
}
