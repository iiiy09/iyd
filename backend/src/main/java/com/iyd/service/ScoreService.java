package com.iyd.service;

import com.iyd.common.R;
import java.util.Map;

public interface ScoreService {
    R<?> saveManualScores(Long userId, Map<String, String> scores);
    R<?> analyzeScore(Long reportId);
    R<?> getReportList(Long userId);
    R<?> getReportDetail(Long reportId);
}