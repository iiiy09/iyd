package com.iyd.service;

import com.iyd.common.R;
import com.iyd.dto.QuestionDTO;

public interface QuestionService {
    R<?> searchQuestion(QuestionDTO dto);
    R<?> getErrorList(Long userId, Integer page, Integer size);
    R<?> markMastered(Long errorId);
    R<?> getPkQuestions(String stage);
    R<?> submitPkBattle(Long battleId, Long userId, String answers);
}
