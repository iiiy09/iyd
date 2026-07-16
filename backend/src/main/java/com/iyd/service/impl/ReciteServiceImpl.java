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
import com.iyd.service.DeepSeekService;
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
    private final DeepSeekService deepSeekService;

    @Override
    public R<?> submitRecite(Long userId, ReciteDTO dto) {
        ReciteRecord record = new ReciteRecord();
        record.setUserId(userId);
        record.setOriginalText(dto.getOriginalText());
        record.setCheckType(dto.getCheckType() != null ? dto.getCheckType() : 1);

        if (dto.getCheckType() == 1 && dto.getUserAnswer() != null) {
            record.setUserAnswer(dto.getUserAnswer());
        }

        // 使用DeepSeek进行智能批改
        Map<String, Object> analysis = analyzeWithAI(dto.getOriginalText(), dto.getUserAnswer(), dto.getCheckType());

        record.setScore((Double) analysis.getOrDefault("score", 0.0));
        record.setErrorDetails(JSONUtil.toJsonStr(analysis.get("errors")));
        record.setSuggestion((String) analysis.getOrDefault("suggestion", ""));
        record.setSubject(dto.getSubject());
        recordMapper.insert(record);

        analysis.put("recordId", record.getId());
        return R.ok(analysis);
    }

    private Map<String, Object> analyzeWithAI(String original, String userAnswer, Integer checkType) {
        Map<String, Object> result = new HashMap<>();

        if (original == null || original.trim().isEmpty()) {
            result.put("score", 0.0);
            result.put("accuracy", "0%");
            result.put("errors", new ArrayList<>());
            result.put("errorCount", 0);
            result.put("suggestion", "请提供背诵原文");
            return result;
        }

        if (checkType == 1 && (userAnswer == null || userAnswer.trim().isEmpty())) {
            result.put("score", 0.0);
            result.put("accuracy", "0%");
            result.put("errors", new ArrayList<>());
            result.put("errorCount", 0);
            result.put("suggestion", "请填写默写内容");
            return result;
        }

        if (checkType == 1) {
            // 文字批改 - 使用DeepSeek智能分析
            String prompt = "你是一个专业的语文/英语背诵批改老师。请对比原文和默写内容，进行详细的批改分析。\n\n"
                         + "批改要求：\n"
                         + "1. 逐行对比原文和默写内容\n"
                         + "2. 找出所有错误（漏写、错字、多字等）\n"
                         + "3. 计算准确率（精确到小数点后1位）\n"
                         + "4. 给出评分（百分制）\n"
                         + "5. 给出改进建议\n\n"
                         + "请严格按照以下JSON格式返回结果（不要包含其他文字）：\n"
                         + "{\"score\": 分数, \"accuracy\": \"准确率%\", \"errors\": [{\"line\": 行号, \"original\": \"原文内容\", \"type\": \"missing/mismatch\"}], \"errorCount\": 错误数量, \"suggestion\": \"学习建议\"}";

            String msg = "原文：\n" + original + "\n\n默写内容：\n" + userAnswer;
            String aiResponse = deepSeekService.chat(prompt, msg);

            try {
                // 尝试解析JSON
                Map<String, Object> aiResult = JSONUtil.toBean(aiResponse, Map.class);
                result.put("score", Double.parseDouble(aiResult.getOrDefault("score", 0).toString()));
                result.put("accuracy", aiResult.getOrDefault("accuracy", "0%"));
                result.put("errors", aiResult.getOrDefault("errors", new ArrayList<>()));
                result.put("errorCount", Integer.parseInt(aiResult.getOrDefault("errorCount", 0).toString()));
                result.put("suggestion", aiResult.getOrDefault("suggestion", "继续加油！"));
            } catch (Exception e) {
                // JSON解析失败，使用简单文本匹配作为备选
                result = simpleTextMatch(original, userAnswer);
            }
        } else {
            // 拍照/视频批改（已简化，提示用户功能）
            result.put("score", 0.0);
            result.put("accuracy", "0%");
            result.put("errors", new ArrayList<>());
            result.put("errorCount", 0);
            result.put("suggestion", "请使用文字批改模式上传背诵内容");
        }

        return result;
    }

    private Map<String, Object> simpleTextMatch(String original, String userText) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        if (original == null) original = "";
        if (userText == null) userText = "";

        String[] originalLines = original.split("\n");
        String[] userLines = userText.split("\n");

        int totalChars = original.replaceAll("\\s", "").length();
        int matchChars = 0;

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
            }

            for (int j = 0; j < Math.min(cleanedOriginal.length(), cleanedUser.length()); j++) {
                if (cleanedOriginal.charAt(j) == cleanedUser.charAt(j)) matchChars++;
            }
        }

        double score = totalChars > 0 ? Math.round(matchChars * 100.0 / totalChars) : 0;
        String suggestion;
        if (score >= 90) suggestion = "背诵优秀！继续保持。";
        else if (score >= 70) suggestion = "整体较好，重点复习标错段落。";
        else if (score >= 50) suggestion = "建议重新熟读原文，加强薄弱段落的记忆。";
        else suggestion = "需要反复朗读和默写，从短句开始逐步突破。";

        result.put("score", score);
        result.put("accuracy", score + "%");
        result.put("errors", errors);
        result.put("errorCount", errors.size());
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
        qw.and(w -> w.isNull(EnglishWord::getUserId).or().eq(EnglishWord::getUserId, userId));
        if (stage != null && !stage.isEmpty()) {
            qw.eq(EnglishWord::getStage, stage);
        }
        if (category != null && !category.isEmpty()) {
            qw.eq(EnglishWord::getCategory, category);
        }
        qw.orderByAsc(EnglishWord::getNextReviewTime);
        List<EnglishWord> words = wordMapper.selectList(qw);

        if (words == null || words.isEmpty()) {
            Long totalCount = wordMapper.selectCount(null);
            if (totalCount == null || totalCount == 0) {
                return R.fail("单词库为空，请联系管理员导入单词数据");
            }
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
        int[] intervals = {1, 2, 4, 7, 15, 30};
        int idx = Math.min(word.getReviewCount(), intervals.length - 1);
        word.setNextReviewTime(LocalDateTime.now().plusDays(intervals[idx]));
        word.setMemoryStatus(known ? 1 : 0);
        wordMapper.updateById(word);
        return R.ok();
    }
}
