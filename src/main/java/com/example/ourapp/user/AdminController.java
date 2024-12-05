package com.example.ourapp.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ourapp.DTO.ReportedCommentDTO;
import com.example.ourapp.DTO.ReportedPostDTO;
import com.example.ourapp.post.ReportService;
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ReportService reportService;

    @Autowired
    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        List<ReportedPostDTO> reportedPosts = reportService.getAllReportedPosts();
        model.addAttribute("reportedPosts", reportedPosts);
        return "admin-reports";
    }
    @PostMapping("/posts/ignore")
    public String ignorePostReport(@RequestParam Long reportId) {
        reportService.ignoreReport(reportId);
        return "redirect:/admin/reports";
    }

    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam Long postId) {
        reportService.deletePost(postId);
        return "redirect:/admin/reports";
    }

    @PostMapping("/comments/ignore")
    public String ignoreCommentReport(@RequestParam Long reportId) {
        reportService.ignoreReport(reportId);
        return "redirect:/admin/reports";
    }

    @PostMapping("/comments/delete")
    public String deleteComment(@RequestParam Long commentId) {
        reportService.deleteComment(commentId);
        return "redirect:/admin/reports";
    }
}
