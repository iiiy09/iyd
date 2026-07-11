package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyd.common.R;
import com.iyd.dto.AiGenerateDTO;
import com.iyd.entity.AiGenerateRecord;
import com.iyd.mapper.AiGenerateRecordMapper;
import com.iyd.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final AiGenerateRecordMapper aiRecordMapper;

    @Override
    public R<?> generateContent(Long userId, AiGenerateDTO dto) {
        Map<String, Object> result = new HashMap<>();
        String output = generateSimulatedContent(dto.getGenerateType(), dto.getInputContent(), dto.getTopic());

        AiGenerateRecord record = new AiGenerateRecord();
        record.setUserId(userId);
        record.setGenerateType(dto.getGenerateType());
        record.setInputContent(dto.getInputContent());
        record.setOutputContent(output);
        aiRecordMapper.insert(record);

        result.put("recordId", record.getId());
        result.put("content", output);
        result.put("type", dto.getGenerateType());
        return R.ok(result);
    }

    private String generateSimulatedContent(Integer type, String input, String topic) {
        switch (type != null ? type : 1) {
            case 1: // 作文
                return "【" + (topic != null ? topic : "主题作文") + "】\n\n"
                        + "引言：在现代社会，" + (topic != null ? topic : "这个话题") + "引发了广泛关注与思考。\n\n"
                        + "正文：（调用大模型API生成的完整作文内容）\n"
                        + "论点清晰、论据充分，行文流畅自然。\n\n"
                        + "结尾：综上所述，我们需要以更全面的视角看待此问题。";
            case 2: // PPT大纲
                return "PPT大纲：\n"
                        + "第1页：封面 - " + topic + "\n"
                        + "第2页：目录\n"
                        + "第3-5页：核心概念讲解\n"
                        + "第6-8页：案例分析\n"
                        + "第9页：总结与展望";
            case 4: // 文案
                return "【精美文案】\n\n"
                        + "星光不问赶路人，时光不负有心人。\n"
                        + "（AI生成的完整文案内容，支持润色改写、扩写缩写、降重优化）";
            default:
                return "AI生成内容";
        }
    }

    @Override
    public R<?> generatePptOutline(Long userId, String topic, String points) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> slides = new ArrayList<>();

        String[] slideNames = {"封面页", "目录", "背景介绍", "核心概念", "详细分析(一)", "详细分析(二)", "案例研究", "对比分析", "总结与展望", "致谢"};
        for (int i = 0; i < slideNames.length; i++) {
            Map<String, String> slide = new HashMap<>();
            slide.put("page", String.valueOf(i + 1));
            slide.put("title", slideNames[i]);
            slide.put("content", "第" + (i + 1) + "页核心内容要点（AI自动生成）");
            slides.add(slide);
        }

        String outputContent = "PPT大纲 - " + topic;
        AiGenerateRecord record = new AiGenerateRecord();
        record.setUserId(userId);
        record.setGenerateType(2);
        record.setInputContent(topic);
        record.setOutputContent(outputContent);
        aiRecordMapper.insert(record);

        result.put("recordId", record.getId());
        result.put("topic", topic);
        result.put("slides", slides);
        result.put("totalPages", slides.size());
        return R.ok(result);
    }

    @Override
    public R<?> chatAssist(Long userId, String question) {
        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", "您好！我是AI学习助手。" +
                "针对您的问题「" + question + "」，我为您提供以下解答：\n" +
                "（调用大模型API回答）\n\n" +
                "如需更详细的解析，请告诉我具体需要深入了解的部分。");
        result.put("relatedTopics", Arrays.asList("相关知识1", "相关知识2", "延伸阅读"));
        return R.ok(result);
    }

    @Override
    public R<?> analyzeStudyData(Long userId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalStudyHours", 128);
        analysis.put("weakSubjects", Arrays.asList("数学-函数", "英语-语法"));
        analysis.put("strongSubjects", Arrays.asList("语文-阅读理解"));
        analysis.put("recommendation", "建议本周重点复习数学函数章节，搭配5套专项练习；英语语法每天20分钟专项训练。");
        return R.ok(analysis);
    }

    @Override
    public R<?> getGenerateHistory(Long userId, Integer type, Integer page, Integer size) {
        Page<AiGenerateRecord> p = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<AiGenerateRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(AiGenerateRecord::getUserId, userId);
        if (type != null) qw.eq(AiGenerateRecord::getGenerateType, type);
        qw.orderByDesc(AiGenerateRecord::getCreateTime);
        return R.ok(aiRecordMapper.selectPage(p, qw));
    }
}
