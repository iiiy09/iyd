package com.iyd.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.entity.EnglishWord;
import com.iyd.entity.QuestionError;
import com.iyd.mapper.EnglishWordMapper;
import com.iyd.mapper.QuestionErrorMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EbbinghausReviewTask {
    private final EnglishWordMapper wordMapper;
    private final QuestionErrorMapper errorMapper;

    @Scheduled(cron = "0 0 8 * * ?")
    public void pushDailyWordReview() {
        log.info("=== 推送每日单词复习任务 ===");
        LambdaQueryWrapper<EnglishWord> qw = new LambdaQueryWrapper<>();
        qw.le(EnglishWord::getNextReviewTime, LocalDateTime.now());
        List<EnglishWord> words = wordMapper.selectList(qw);
        log.info("今日待复习单词数: {}", words.size());
        // In production: push notification to relevant users
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void pushErrorQuestionReview() {
        log.info("=== 推送错题复习任务 ===");
        LambdaQueryWrapper<QuestionError> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionError::getMastered, 0);
        long count = errorMapper.selectCount(qw);
        log.info("未掌握错题数: {}", count);
    }
}
