package com.iyd.service;

import com.iyd.common.R;
import com.iyd.dto.AiGenerateDTO;

public interface AiService {
    R<?> generateContent(Long userId, AiGenerateDTO dto);
    R<?> generatePptOutline(Long userId, String topic, String points);
    R<?> chatAssist(Long userId, String question);
    R<?> analyzeStudyData(Long userId);
    R<?> getGenerateHistory(Long userId, Integer type, Integer page, Integer size);
}
