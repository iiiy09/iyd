package com.iyd.service;

import com.iyd.common.R;
import com.iyd.dto.ReciteDTO;

public interface ReciteService {
    R<?> submitRecite(Long userId, ReciteDTO dto);
    R<?> getReciteHistory(Long userId, Integer page, Integer size);
    R<?> getWordList(Long userId, String stage, String category);
    R<?> reviewWord(Long userId, Long wordId, Boolean known);
}