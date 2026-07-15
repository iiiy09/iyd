package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.common.R;
import com.iyd.entity.ScoreReport;
import com.iyd.mapper.ScoreReportMapper;
import com.iyd.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    private final ScoreReportMapper reportMapper;

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

        // Parse all subject scores
        Map<String, Integer> scoreMap = new LinkedHashMap<>();
        scoreMap.put("语文", parseScore(report.getChineseScore()));
        scoreMap.put("数学", parseScore(report.getMathScore()));
        scoreMap.put("英语", parseScore(report.getEnglishScore()));
        scoreMap.put("物理", parseScore(report.getPhysicsScore()));
        scoreMap.put("化学", parseScore(report.getChemistryScore()));
        scoreMap.put("生物", parseScore(report.getBiologyScore()));
        scoreMap.put("历史", parseScore(report.getHistoryScore()));
        scoreMap.put("地理", parseScore(report.getGeographyScore()));
        scoreMap.put("政治", parseScore(report.getPoliticsScore()));

        // Filter subjects with valid scores
        Map<String, Integer> validScores = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : scoreMap.entrySet()) {
            if (e.getValue() > 0) {
                validScores.put(e.getKey(), e.getValue());
            }
        }

        if (validScores.isEmpty()) {
            return R.fail("暂无有效成绩数据，请先录入成绩");
        }

        // Calculate statistics
        int total = 0, count = 0, maxScore = 0, minScore = 100;
        String bestSubject = "", worstSubject = "";
        List<String> aboveAvg = new ArrayList<>();
        List<String> belowAvg = new ArrayList<>();

        for (Map.Entry<String, Integer> e : validScores.entrySet()) {
            int s = e.getValue();
            total += s;
            count++;
            if (s > maxScore) { maxScore = s; bestSubject = e.getKey(); }
            if (s < minScore) { minScore = s; worstSubject = e.getKey(); }
        }
        double avg = count > 0 ? (double) total / count : 0;

        for (Map.Entry<String, Integer> e : validScores.entrySet()) {
            if (e.getValue() >= avg) aboveAvg.add(e.getKey());
            else belowAvg.add(e.getKey());
        }

        double totalScore = total;
        double fullMark = count * 100.0;
        double overallRate = totalScore / fullMark * 100;

        // Generate intelligent analysis report
        StringBuilder sb = new StringBuilder();
        sb.append("【学情分析报告】\n\n");
        sb.append("一、总体评价\n");
        if (overallRate >= 85) {
            sb.append("  整体成绩优秀，各科发展均衡，继续保持！\n");
        } else if (overallRate >= 70) {
            sb.append("  整体成绩良好，部分科目仍有提升空间，继续加油！\n");
        } else if (overallRate >= 60) {
            sb.append("  整体成绩中等，需要针对薄弱科目重点突破。\n");
        } else {
            sb.append("  整体成绩有待提高，建议制定详细的学习计划，从基础抓起。\n");
        }
        sb.append("  总分率：").append(String.format("%.1f%%", overallRate)).append("\n\n");

        sb.append("二、成绩概况\n");
        sb.append("  优势科目：").append(bestSubject).append("（").append(maxScore).append("分）\n");
        sb.append("  薄弱科目：").append(worstSubject).append("（").append(minScore).append("分）\n");
        sb.append("  平均分：").append(String.format("%.1f", avg)).append("\n");
        sb.append("  总分：").append(total).append(" / ").append((int)fullMark).append("\n\n");

        // Score level distribution
        sb.append("三、成绩等级分布\n");
        Map<String, List<String>> levels = new LinkedHashMap<>();
        levels.put("优秀（90-100）", new ArrayList<>());
        levels.put("良好（70-89）", new ArrayList<>());
        levels.put("及格（60-69）", new ArrayList<>());
        levels.put("不及格（<60）", new ArrayList<>());
        for (Map.Entry<String, Integer> e : validScores.entrySet()) {
            int s = e.getValue();
            if (s >= 90) levels.get("优秀（90-100）").add(e.getKey());
            else if (s >= 70) levels.get("良好（70-89）").add(e.getKey());
            else if (s >= 60) levels.get("及格（60-69）").add(e.getKey());
            else levels.get("不及格（<60）").add(e.getKey());
        }
        for (Map.Entry<String, List<String>> e : levels.entrySet()) {
            sb.append("  ").append(e.getKey()).append("：");
            if (e.getValue().isEmpty()) {
                sb.append("无\n");
            } else {
                sb.append(String.join("、", e.getValue())).append("\n");
            }
        }
        sb.append("\n");

        sb.append("四、薄弱知识点定位\n");
        if (worstSubject.equals("数学") || worstSubject.equals("物理")) {
            sb.append("  ").append(worstSubject).append("：核心公式和定理掌握不牢固，建议从基础概念入手，配合典型例题练习。\n");
        } else if (worstSubject.equals("语文") || worstSubject.equals("英语")) {
            sb.append("  ").append(worstSubject).append("：需要加强积累，建议每日坚持阅读和背诵，建立错题本定期回顾。\n");
        } else if (worstSubject.equals("化学") || worstSubject.equals("生物")) {
            sb.append("  ").append(worstSubject).append("：实验题和综合应用题是难点，建议注重知识点之间的联系，多做综合训练。\n");
        } else {
            sb.append("  ").append(worstSubject).append("：该科目需要系统梳理知识框架，找出薄弱章节重点攻克。\n");
        }
        if (!belowAvg.isEmpty()) {
            sb.append("  低于平均分的科目：").append(String.join("、", belowAvg)).append("\n");
            sb.append("  建议优先从这些科目入手提升，合理分配学习时间。\n");
        }
        sb.append("\n");

        sb.append("五、提升建议\n");
        sb.append("  1. 每日专项练习：针对薄弱科目，每天安排30分钟专项训练\n");
        sb.append("  2. 错题整理：建立错题本，每周定期复盘，避免同类错误\n");
        sb.append("  3. 时间管理：合理分配各科学习时间，优势科目保持，薄弱科目重点突破\n");
        sb.append("  4. 寻求帮助：遇到难题及时向老师请教，或使用AI助手进行答疑\n");
        sb.append("  5. 模拟测试：定期进行模拟测试，检验学习效果，及时调整学习策略\n");

        report.setAiReport(sb.toString());

        // Generate study plan
        StringBuilder plan = new StringBuilder();
        plan.append("【阶段性提分学习计划】\n\n");
        plan.append("周目标：重点攻克").append(worstSubject).append("薄弱点，巩固其他科目\n\n");
        plan.append("每日安排：\n");
        plan.append("- 早晨30分钟：").append(worstSubject).append("专题训练\n");
        plan.append("- 上午1小时：优势科目维持性练习\n");
        plan.append("- 下午1小时：薄弱科目重点提升\n");
        plan.append("- 晚上30分钟：错题回顾与知识点整理\n\n");
        plan.append("周期检测：\n");
        plan.append("- 每周进行一次模拟测试\n");
        plan.append("- 对比分析前后成绩变化\n");
        plan.append("- 根据测试结果动态调整学习计划\n");

        report.setStudyPlan(plan.toString());
        reportMapper.updateById(report);

        return R.ok(report);
    }

    private int parseScore(String score) {
        if (score == null || score.trim().isEmpty()) return 0;
        try { return Integer.parseInt(score.trim()); }
        catch (NumberFormatException e) { return 0; }
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
