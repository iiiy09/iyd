package com.iyd.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyd.common.R;
import com.iyd.dto.ReciteDTO;
import com.iyd.entity.EnglishWord;
import com.iyd.entity.ReciteRecord;
import com.iyd.mapper.EnglishWordMapper;
import com.iyd.mapper.ReciteRecordMapper;
import com.iyd.service.ReciteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReciteServiceImpl implements ReciteService {
    private final ReciteRecordMapper recordMapper;
    private final EnglishWordMapper wordMapper;

    @Override
    public R<?> submitRecite(Long userId, ReciteDTO dto) {
        ReciteRecord record = new ReciteRecord();
        record.setUserId(userId);
        record.setOriginalText(dto.getOriginalText());
        record.setCheckType(dto.getCheckType() != null ? dto.getCheckType() : 1);

        Map<String, Object> analysis = new HashMap<>();

        if (dto.getCheckType() == 1 && dto.getUserAnswer() != null) {
            // Text-based checking
            record.setUserAnswer(dto.getUserAnswer());
            analysis = analyzeTextMatch(dto.getOriginalText(), dto.getUserAnswer());
        } else if (dto.getCheckType() == 2 && dto.getHandwrittenImage() != null) {
            // OCR-based checking
            record.setHandwrittenImage(dto.getHandwrittenImage());
            String ocrText = "OCR识别结果(调用OCR API)";
            record.setUserAnswer(ocrText);
            analysis = analyzeTextMatch(dto.getOriginalText(), ocrText);
        } else if (dto.getCheckType() == 3 && dto.getReciteVideo() != null) {
            // Video/speech-based checking
            record.setReciteVideo(dto.getReciteVideo());
            String speechText = "语音识别结果(调用语音API)";
            record.setUserAnswer(speechText);
            analysis = analyzeTextMatch(dto.getOriginalText(), speechText);
        }

        record.setScore((Double) analysis.getOrDefault("score", 0.0));
        record.setErrorDetails(JSONUtil.toJsonStr(analysis.get("errors")));
        record.setSuggestion((String) analysis.getOrDefault("suggestion", ""));
        record.setSubject(dto.getSubject());
        recordMapper.insert(record);

        analysis.put("recordId", record.getId());
        return R.ok(analysis);
    }

    private Map<String, Object> analyzeTextMatch(String original, String userText) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        String[] originalLines = original.split("\n");
        String[] userLines = userText.split("\n");

        int totalChars = original.replaceAll("\\s", "").length();
        int matchChars = 0;
        int errorCount = 0;

        for (int i = 0; i < originalLines.length; i++) {
            String orig = originalLines[i].trim();
            String user = i < userLines.length ? userLines[i].trim() : "";
            if (!orig.equals(user)) {
                Map<String, Object> err = new HashMap<>();
                err.put("line", i + 1);
                err.put("original", orig);
                err.put("userInput", user);
                err.put("type", user.isEmpty() ? "missing" : "mismatch");
                errors.add(err);
                errorCount++;
            }
            if (!orig.isEmpty()) {
                for (int j = 0; j < Math.min(orig.length(), user.length()); j++) {
                    if (orig.charAt(j) == user.charAt(j)) matchChars++;
                }
            }
        }
        if (userLines.length < originalLines.length) {
            for (int i = userLines.length; i < originalLines.length; i++) {
                Map<String, Object> err = new HashMap<>();
                err.put("line", i + 1);
                err.put("original", originalLines[i].trim());
                err.put("userInput", "");
                err.put("type", "missing");
                errors.add(err);
                errorCount++;
            }
        }

        double score = totalChars > 0 ? Math.round(matchChars * 100.0 / totalChars) : 0;
        String suggestion = score >= 90 ? "背诵优秀！继续保持。" :
                score >= 70 ? "整体较好，重点复习标错段落。" :
                        "建议重新熟读原文，加强薄弱段落的记忆。";

        result.put("score", score);
        result.put("accuracy", score + "%");
        result.put("errors", errors);
        result.put("errorCount", errorCount);
        result.put("suggestion", suggestion);
        return result;
    }

    @Override
    public R<?> getReciteHistory(Long userId, Integer page, Integer size) {
        Page<ReciteRecord> p = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<ReciteRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(ReciteRecord::getUserId, userId).orderByDesc(ReciteRecord::getCreateTime);
        return R.ok(recordMapper.selectPage(p, qw));
    }

    @Override
    public R<?> getWordList(Long userId, String stage, String category) {
        LambdaQueryWrapper<EnglishWord> qw = new LambdaQueryWrapper<>();
        qw.eq(EnglishWord::getUserId, userId);
        if (stage != null) qw.eq(EnglishWord::getStage, stage);
        if (category != null) qw.eq(EnglishWord::getCategory, category);
        qw.orderByAsc(EnglishWord::getNextReviewTime);
        return R.ok(wordMapper.selectList(qw));
    }

    @Override
    public R<?> reviewWord(Long userId, Long wordId, Boolean known) {
        EnglishWord word = wordMapper.selectById(wordId);
        if (word == null) return R.fail("单词不存在");
        word.setReviewCount(word.getReviewCount() + 1);
        word.setLastReviewTime(LocalDateTime.now());
        // Ebbinghaus: next review interval based on review count
        int[] intervals = {1, 2, 4, 7, 15, 30};
        int idx = Math.min(word.getReviewCount(), intervals.length - 1);
        word.setNextReviewTime(LocalDateTime.now().plusDays(intervals[idx]));
        word.setMemoryStatus(known ? 1 : 0);
        wordMapper.updateById(word);
        return R.ok();
    }

    @Override
    public R<?> speechEvaluate(Long userId, String word, String audioUrl) {
        Map<String, Object> result = new HashMap<>();
        result.put("word", word);
        result.put("score", 85.5);
        result.put("pronunciation", "良好");
        result.put("feedback", "发音较标准，注意元音/æ/的口型");
        return R.ok(result);
    }
}
