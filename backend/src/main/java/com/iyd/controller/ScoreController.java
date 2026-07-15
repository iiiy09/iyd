package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
public class ScoreController {
    private final ScoreService scoreService;
    private final JwtUtil jwtUtil;

    @PostMapping("/upload")
    public R<?> uploadScoreFile(@RequestHeader("Authorization") String token,
                                 @RequestParam String fileUrl) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return scoreService.uploadScoreFile(userId, fileUrl);
    }

    @PostMapping("/manual")
    public R<?> saveManualScores(@RequestHeader("Authorization") String token,
                                  @RequestBody Map<String, String> scores) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return scoreService.saveManualScores(userId, scores);
    }

    @PostMapping("/{reportId}/analyze")
    public R<?> analyzeScore(@PathVariable Long reportId) {
        return scoreService.analyzeScore(reportId);
    }

    @GetMapping("/reports")
    public R<?> getReportList(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return scoreService.getReportList(userId);
    }

    @GetMapping("/report/{reportId}")
    public R<?> getReportDetail(@PathVariable Long reportId) {
        return scoreService.getReportDetail(reportId);
    }
}
