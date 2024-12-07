package com.example.ourapp.post;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.ReportDTO;
import com.example.ourapp.entity.Comment;
import com.example.ourapp.entity.Post;
import com.example.ourapp.entity.Report;
import com.example.ourapp.entity.User;
import com.example.ourapp.user.UserRepository;
import com.example.ourapp.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepositoy;
    public void reportPost(Long postId, String reason, Long reportedBy) {
        // 게시글 신고 시, 해당 게시글에 속한 모든 댓글을 가져옵니다.
        List<Comment> comments = commentRepository.findByPostId(postId);
        Optional<Post> post = postRepositoy.findById(postId);
        String username = post.map(Post::getUsername)
                .orElseThrow(() -> new RuntimeException("Post or User not found"));
        Optional<User> user = userRepository.findByUsername(username);
        Long userId = user.map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
        Report postReport = new Report(postId, reason, Report.ReportType.POST, reportedBy, postId,userId);
        postReport.setHidden(false); // 기본 숨김 여부는 false로 설정
        reportRepository.save(postReport); // 게시글 신고를 저장

        // 게시글에 속한 댓글 신고
        /*for (Comment comment : comments) {
            // 댓글 신고 시, 댓글 ID와 게시글 ID를 함께 저장
            Report commentReport = new Report(comment.getId(), reason, Report.ReportType.COMMENT, reportedBy, postId);
            commentReport.setHidden(false); // 기본 숨김 여부는 false로 설정
            reportRepository.save(commentReport); // 댓글 신고를 저장
        }
        */
    }


    public void reportComment(Long commentId, String reason, Long reportedBy) {
        // 댓글 ID로 해당 댓글을 조회하여 게시글 ID를 가져옵니다.
        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        // 댓글이 속한 게시글의 ID
        Long postId = comment.getPost().getId(); // 게시글 ID 추출
        
       
        String userName=comment.getUsername();
        Optional<User> user = userRepository.findByUsername(userName);
        Long userId = user.map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
        // 댓글 신고 시, 댓글 ID와 게시글 ID를 함께 저장
        Report report = new Report(commentId, reason, Report.ReportType.COMMENT, reportedBy, postId,userId);
        report.setHidden(false); // 기본 숨김 여부는 false

        // 신고 레포트를 DB에 저장
        reportRepository.save(report);
    }


    
    public Long getPostIdByCommentId(Long commentId) {
        return commentRepository.findPostIdByCommentId(commentId)
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
                            report.getReportedBy(),
                            report.isHidden(), // 숨김 여부 매핑
                            report.getPostId(),
                            report.getUserId()
                    );
                })
                .toList();
    }

    public List<ReportDTO> getAllCommentReports() {
        return reportRepository.findByType(Report.ReportType.COMMENT)
                .stream()
                .map(report -> {
                    String username = userService.findUserById(report.getReportedBy()).getUsername();
                    Long postId = commentRepository.findPostIdByCommentId(report.getTargetId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
                    return new ReportDTO(
                            report.getId(),
                            postId, // 실제로 표시되는 '대상 ID'는 게시글 ID로 설정
                            report.getReason(),
                            report.getType().name(),
                            report.getReportedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            username,
                            report.getReportedBy(),
                            report.isHidden(),
                            report.getPostId(),
                            report.getUserId()
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
    
 // Fetch hidden reports related to posts
    public List<ReportDTO> getHiddenPostReports() {
        return reportRepository.findReportsByPostHidden(Report.ReportType.POST, true)
                .stream()
                .map(this::mapToReportDTO)
                .toList();
    }

    // Fetch visible reports related to posts
    public List<ReportDTO> getVisiblePostReports() {
        return reportRepository.findReportsByPostHidden(Report.ReportType.POST, false)
                .stream()
                .map(this::mapToReportDTO)
                .toList();
    }

    // Fetch hidden reports related to comments
    public List<ReportDTO> getHiddenCommentReports() {
        return reportRepository.findReportsByCommentHidden(Report.ReportType.COMMENT, true)
            .stream()
            .map(this::mapToReportDTO)
            .toList();
    }



    // Fetch visible reports related to comments
    public List<ReportDTO> getVisibleCommentReports() {
        return reportRepository.findReportsByCommentHidden(Report.ReportType.COMMENT, false)
                .stream()
                .map(this::mapToReportDTO)
                .toList();
    }
    private ReportDTO mapToReportDTO(Report report) {
        String username = userService.findUserById(report.getReportedBy()).getUsername();
        return new ReportDTO(
            report.getId(),
            report.getTargetId(),
            report.getReason(),
            report.getType().name(),
            report.getReportedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            username,
            report.getReportedBy(),
            report.isHidden(),
            report.getPostId(),
            report.getUserId()
        );
    }
}