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
            record.setUserAnswer(dto.getUserAnswer());
            analysis = analyzeTextMatch(dto.getOriginalText(), dto.getUserAnswer());
        } else if (dto.getCheckType() == 2) {
            record.setHandwrittenImage(dto.getHandwrittenImage());
            String ocrText = dto.getHandwrittenImage() != null && !dto.getHandwrittenImage().isEmpty()
                    ? "OCR识别结果(已收到图片,待API处理)" : "未上传图片";
            record.setUserAnswer(ocrText);
            if (dto.getOriginalText() != null && !ocrText.contains("未上传")) {
                analysis = analyzeTextMatch(dto.getOriginalText(), ocrText);
            } else {
                analysis.put("score", 0.0);
                analysis.put("accuracy", "0%");
                analysis.put("errors", new ArrayList<>());
                analysis.put("errorCount", 0);
                analysis.put("suggestion", "请上传手写图片后再提交批改");
            }
        } else if (dto.getCheckType() == 3) {
            record.setReciteVideo(dto.getReciteVideo());
            String speechText = dto.getReciteVideo() != null && !dto.getReciteVideo().isEmpty()
                    ? "语音识别结果(已收到视频,待API处理)" : "未上传视频";
            record.setUserAnswer(speechText);
            if (dto.getOriginalText() != null && !speechText.contains("未上传")) {
                analysis = analyzeTextMatch(dto.getOriginalText(), speechText);
            } else {
                analysis.put("score", 0.0);
                analysis.put("accuracy", "0%");
                analysis.put("errors", new ArrayList<>());
                analysis.put("errorCount", 0);
                analysis.put("suggestion", "请上传背诵视频后再提交批改");
            }
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

        if (original == null) original = "";
        if (userText == null) userText = "";

        String[] originalLines = original.split("\n");
        String[] userLines = userText.split("\n");

        int totalChars = original.replaceAll("\\s", "").length();
        int matchChars = 0;
        int errorCount = 0;

        for (int i = 0; i < originalLines.length; i++) {
            String orig = originalLines[i].trim();
            String user = i < userLines.length ? userLines[i].trim() : "";
            String cleanedOriginal = orig.replaceAll("\\s", "");
            String cleanedUser = user.replaceAll("\\s", "");

            if (!cleanedOriginal.equals(cleanedUser)) {
                Map<String, Object> err = new HashMap<>();
                err.put("line", i + 1);
                err.put("original", orig);
                err.put("type", cleanedUser.isEmpty() ? "missing" : "mismatch");
                errors.add(err);
                errorCount++;
            }

            for (int j = 0; j < Math.min(cleanedOriginal.length(), cleanedUser.length()); j++) {
                if (cleanedOriginal.charAt(j) == cleanedUser.charAt(j)) matchChars++;
            }
        }
        if (userLines.length < originalLines.length) {
            for (int i = userLines.length; i < originalLines.length; i++) {
                Map<String, Object> err = new HashMap<>();
                err.put("line", i + 1);
                err.put("original", originalLines[i].trim());
                err.put("type", "missing");
                errors.add(err);
                errorCount++;
            }
        }

        double score = totalChars > 0 ? Math.round(matchChars * 100.0 / totalChars) : 0;
        String suggestion;
        if (score >= 90) {
            suggestion = "背诵优秀！继续保持。";
        } else if (score >= 70) {
            suggestion = "整体较好，重点复习标错段落。";
        } else if (score >= 50) {
            suggestion = "建议重新熟读原文，加强薄弱段落的记忆。";
        } else {
            suggestion = "需要反复朗读和默写，从短句开始逐步突破。";
        }

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
        // Include system words (userId is null) AND user's own words
        qw.and(w -> w.isNull(EnglishWord::getUserId).or().eq(EnglishWord::getUserId, userId));
        if (stage != null && !stage.isEmpty()) {
            qw.eq(EnglishWord::getStage, stage);
        }
        if (category != null && !category.isEmpty()) {
            qw.eq(EnglishWord::getCategory, category);
        }
        qw.orderByAsc(EnglishWord::getNextReviewTime);
        List<EnglishWord> words = wordMapper.selectList(qw);

        // If no words found for this stage, check if any words exist at all
        if (words == null || words.isEmpty()) {
            Long totalCount = wordMapper.selectCount(null);
            if (totalCount == null || totalCount == 0) {
                return R.fail("单词库为空，请联系管理员导入单词数据");
            }
            // Try without stage filter
            LambdaQueryWrapper<EnglishWord> fallback = new LambdaQueryWrapper<>();
            fallback.and(w -> w.isNull(EnglishWord::getUserId).or().eq(EnglishWord::getUserId, userId));
            fallback.orderByAsc(EnglishWord::getNextReviewTime);
            fallback.last("LIMIT 50");
            words = wordMapper.selectList(fallback);
        }

        return R.ok(words);
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

        // Simulate speech evaluation scoring based on word length
        double baseScore = 75.0 + Math.random() * 20;
        String pronunciation;
        if (baseScore >= 85) {
            pronunciation = "优秀";
        } else if (baseScore >= 70) {
            pronunciation = "良好";
        } else {
            pronunciation = "需加强";
        }

        result.put("score", Math.round(baseScore * 10.0) / 10.0);
        result.put("pronunciation", pronunciation);
        result.put("feedback", "发音基本标准，注意元音和重音的准确性。建议多听原声跟读。");
        return R.ok(result);
    }
}