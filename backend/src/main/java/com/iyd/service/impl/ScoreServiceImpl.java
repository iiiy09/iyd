package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.common.R;
import com.iyd.entity.ScoreReport;
import com.iyd.mapper.ScoreReportMapper;
import com.iyd.service.DeepSeekService;
import com.iyd.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    private final ScoreReportMapper reportMapper;
    private final DeepSeekService deepSeekService;

    @Override
    public R<?> saveManualScores(Long userId, Map<String, String> scores) {
        ScoreReport report = new ScoreReport();
        report.setUserId(userId);
        report.setOriginalFile("manual_input");

        if (scores != null) {
            report.setChineseScore(scores.getOrDefault("chinese", ""));
            report.setMathScore(scores.getOrDefault("math", ""));
            report.setEnglishScore(scores.getOrDefault("english", ""));
            report.setPhysicsScore(scores.getOrDefault("physics", ""));
            report.setChemistryScore(scores.getOrDefault("chemistry", ""));
            report.setBiologyScore(scores.getOrDefault("biology", ""));
            report.setHistoryScore(scores.getOrDefault("history", ""));
            report.setGeographyScore(scores.getOrDefault("geography", ""));
            report.setPoliticsScore(scores.getOrDefault("politics", ""));
        }
        reportMapper.insert(report);
        return R.ok(report);
    }

    @Override
    public R<?> analyzeScore(Long reportId) {
        ScoreReport report = reportMapper.selectById(reportId);
        if (report == null) return R.fail("成绩报告不存在");

        // 收集所有科目成绩
        Map<String, String> scoreMap = new LinkedHashMap<>();
        scoreMap.put("语文", report.getChineseScore());
        scoreMap.put("数学", report.getMathScore());
        scoreMap.put("英语", report.getEnglishScore());
        scoreMap.put("物理", report.getPhysicsScore());
        scoreMap.put("化学", report.getChemistryScore());
        scoreMap.put("生物", report.getBiologyScore());
        scoreMap.put("历史", report.getHistoryScore());
        scoreMap.put("地理", report.getGeographyScore());
        scoreMap.put("政治", report.getPoliticsScore());

        // 过滤有效成绩
        StringBuilder scoreInfo = new StringBuilder("学生成绩如下：\n");
        int total = 0, count = 0;
        for (Map.Entry<String, String> e : scoreMap.entrySet()) {
            if (e.getValue() != null && !e.getValue().trim().isEmpty()) {
                int s = Integer.parseInt(e.getValue().trim());
                scoreInfo.append(e.getKey()).append("：").append(s).append("分\n");
                total += s;
                count++;
            }
        }

        if (count == 0) {
            return R.fail("暂无有效成绩数据，请先录入成绩");
        }

        double avg = (double) total / count;

        // 使用DeepSeek生成智能分析报告
        String reportPrompt = "你是一个专业的学习分析顾问。请根据学生的各科成绩进行详细分析。\n\n"
                            + scoreInfo.toString()
                            + "\n平均分：" + String.format("%.1f", avg) + "\n\n"
                            + "请提供以下内容（用中文）：\n"
                            + "1. 【总体评价】对学生的整体学习情况做出评价\n"
                            + "2. 【各科分析】分析每科的优势和不足\n"
                            + "3. 【薄弱环节】指出需要重点提升的科目和原因\n"
                            + "4. 【提升建议】给出具体可行的提分建议\n"
                            + "5. 【学习计划】制定一个阶段性的学习计划";

        String aiReport = deepSeekService.chat(
            "你是一个资深学习分析师，擅长从成绩数据中发现学生学习问题并给出专业建议。",
            reportPrompt
        );
        report.setAiReport(aiReport);

        // 生成学习计划
        String planPrompt = "你是一个教育规划师。请根据以下成绩数据为学生制定详细的学习计划。\n\n"
                          + scoreInfo.toString()
                          + "\n请制定一个包含每日安排、每周目标的详细学习计划。";

        String studyPlan = deepSeekService.chat(
            "你是一个教育规划专家，擅长为学生制定个性化学习计划。",
            planPrompt
        );
        report.setStudyPlan(studyPlan);

        reportMapper.updateById(report);

        return R.ok(report);
    }

    @Override
    public R<?> getReportList(Long userId) {
        LambdaQueryWrapper<ScoreReport> qw = new LambdaQueryWrapper<>();
        qw.eq(ScoreReport::getUserId, userId).orderByDesc(ScoreReport::getCreateTime);
        return R.ok(reportMapper.selectList(qw));
    }

    @Override
    public R<?> getReportDetail(Long reportId) {
        return R.ok(reportMapper.selectById(reportId));
    }
}
