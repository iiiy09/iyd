package com.iyd.service;

import com.iyd.common.R;

public interface ScoreService {
    R<?> uploadScoreFile(Long userId, String fileUrl);
    R<?> analyzeScore(Long reportId);
    R<?> getReportList(Long userId);
    R<?> getReportDetail(Long reportId);
}
