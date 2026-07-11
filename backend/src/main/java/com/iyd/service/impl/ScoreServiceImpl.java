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
    public R<?> uploadScoreFile(Long userId, String fileUrl) {
        ScoreReport report = new ScoreReport();
        report.setUserId(userId);
        report.setOriginalFile(fileUrl);
        report.setChineseScore("85");
        report.setMathScore("92");
        report.setEnglishScore("78");
        report.setPhysicsScore("88");
        report.setChemistryScore("90");
        reportMapper.insert(report);
        return R.ok(report);
    }

    @Override
    public R<?> analyzeScore(Long reportId) {
        ScoreReport report = reportMapper.selectById(reportId);
        if (report == null) return R.fail("成绩报告不存在");

        StringBuilder aiReport = new StringBuilder();
        aiReport.append("【学情分析报告】\n\n");
        aiReport.append("总体评价：成绩较为均衡，理科优势明显。\n\n");

        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("语文", parseScore(report.getChineseScore()));
        scores.put("数学", parseScore(report.getMathScore()));
        scores.put("英语", parseScore(report.getEnglishScore()));
        scores.put("物理", parseScore(report.getPhysicsScore()));
        scores.put("化学", parseScore(report.getChemistryScore()));

        int total = 0, count = 0, max = 0, min = 100;
        String strong = "", weak = "";
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            if (e.getValue() == 0) continue;
            total += e.getValue();
            count++;
            if (e.getValue() > max) { max = e.getValue(); strong = e.getKey(); }
            if (e.getValue() < min) { min = e.getValue(); weak = e.getKey(); }
        }
        double avg = count > 0 ? total * 1.0 / count : 0;

        aiReport.append("优势学科：").append(strong).append("（").append(max).append("分）\n");
        aiReport.append("薄弱学科：").append(weak).append("（").append(min).append("分）\n");
        aiReport.append("平均分：").append(String.format("%.1f", avg)).append("\n\n");
        aiReport.append("薄弱知识点定位：").append(weak).append("相关核心知识点掌握不足\n");
        aiReport.append("提升建议：1.每日专项练习30分钟 2.整理错题本定期复盘 3.寻求老师针对性辅导\n");

        report.setAiReport(aiReport.toString());

        StringBuilder plan = new StringBuilder();
        plan.append("【阶段性提分学习计划】\n");
        plan.append("周目标：重点攻克").append(weak).append("薄弱点\n");
        plan.append("每日安排：\n- 早晨30分钟：").append(weak).append("专项练习\n- 下午1小时：综合练习\n- 晚上30分钟：错题复习");
        report.setStudyPlan(plan.toString());
        reportMapper.updateById(report);

        return R.ok(report);
    }

    private int parseScore(String score) {
        try { return score != null ? Integer.parseInt(score) : 0; }
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
