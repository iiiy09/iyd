package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyd.common.R;
import com.iyd.dto.AiGenerateDTO;
import com.iyd.entity.AiGenerateRecord;
import com.iyd.mapper.AiGenerateRecordMapper;
import com.iyd.service.AiService;
import com.iyd.service.DeepSeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final AiGenerateRecordMapper aiRecordMapper;
    private final DeepSeekService deepSeekService;

    @Override
    public R<?> generateContent(Long userId, AiGenerateDTO dto) {
        Map<String, Object> result = new HashMap<>();
        String output = generateFullContent(dto.getGenerateType(), dto.getTopic(), dto.getInputContent());
        
        AiGenerateRecord record = new AiGenerateRecord();
        record.setUserId(userId);
        record.setGenerateType(dto.getGenerateType());
        record.setInputContent(dto.getInputContent());
        record.setOutputContent(output);
        aiRecordMapper.insert(record);
        
        result.put("recordId", record.getId());
        result.put("content", output);
        result.put("type", dto.getGenerateType());
        result.put("topic", dto.getTopic());
        return R.ok(result);
    }

    private String generateFullContent(Integer type, String topic, String input) {
        String prompt;
        String userMessage;

        switch (type != null ? type : 1) {
            case 1: // 作文/文案创作
                prompt = "你是一个专业的写作助手，擅长写作文、文案、周记等。请根据用户的要求创作高质量的内容。";
                userMessage = (topic != null && !topic.isEmpty() ? "主题：" + topic + "\n" : "") 
                           + (input != null && !input.isEmpty() ? "要求：" + input : "请写一篇关于" + (topic != null ? topic : "青春与梦想") + "的文章");
                break;
            case 2: // PPT大纲
                prompt = "你是一个专业的PPT设计助手，请根据用户提供的主题生成详细的PPT大纲，包括封面、目录、各部分内容和总结。";
                userMessage = "生成关于\"" + (input != null && !input.isEmpty() ? input : (topic != null ? topic : "默认主题")) + "\"的PPT大纲";
                break;
            case 3: // 学习报告
                prompt = "你是一个学习分析专家，请根据用户提供的信息生成详细的学习报告。";
                userMessage = "生成学习报告：" + (input != null && !input.isEmpty() ? input : (topic != null ? topic : "学习情况"));
                break;
            case 4: // 文案
                prompt = "你是一个创意文案写手，擅长写吸引人的文案。请根据用户要求创作文案。";
                userMessage = "创作关于\"" + (input != null && !input.isEmpty() ? input : (topic != null ? topic : "学习")) + "\"的文案";
                break;
            case 5: // 日记
                prompt = "你是一个善于记录生活的日记作者。请用日记的形式写出今天的感受和收获。";
                userMessage = "写一篇关于" + (input != null && !input.isEmpty() ? input : (topic != null ? topic : "学习")) + "的日记";
                break;
            case 6: // AI刷题答疑 / AI智能助手
                prompt = "你是一个全科学习辅导AI助手，精通数学、物理、化学、语文、英语、历史、地理、生物等学科。"
                       + "请直接、详细地回答用户的学习问题。用中文回答，条理清晰，通俗易懂。"
                       + "如果用户问的是具体学科问题（如数学公式、物理定律），请给出准确的解释和示例。"
                       + "如果是学习方法问题，请给出实用的建议和步骤。";
                userMessage = input != null && !input.isEmpty() ? input : 
                              (topic != null && !topic.isEmpty() ? "请介绍关于" + topic + "的知识" : "请给我一些学习建议");
                break;
            default:
                prompt = "你是一个智能助手，请回答用户的问题。";
                userMessage = input != null ? input : "请给我一些学习建议";
        }

        return deepSeekService.chat(prompt, userMessage);
    }

    @Override
    public R<?> generatePptOutline(Long userId, String topic, String points) {
        String prompt = "你是一个专业的PPT设计助手。请生成详细的PPT大纲。";
        String msg = "主题：" + topic + "\n" + (points != null && !points.isEmpty() ? "核心要点：" + points : "");
        String content = deepSeekService.chat(prompt, msg);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("topic", topic);
        result.put("outline", content);
        result.put("additionalPoints", points != null ? Arrays.asList(points.split(",")) : new ArrayList<>());
        return R.ok(result);
    }

    @Override
    public R<?> chatAssist(Long userId, String question) {
        if (question == null || question.trim().isEmpty()) {
            return R.fail("请输入你想问的问题！");
        }

        String prompt = "你是一个全科学习辅导AI助手，精通各学科知识。"
                       + "请直接回答用户的学习问题，回答要详细、准确、通俗易懂。"
                       + "如果是概念解释，请给出定义和例子。"
                       + "如果是解题方法，请给出步骤。"
                       + "如果是学习建议，请给出具体可执行的方案。";
        String answer = deepSeekService.chat(prompt, question);

        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", answer);
        result.put("subject", detectSubject(question));
        return R.ok(result);
    }

    private String detectSubject(String question) {
        if (question == null) return "";
        Map<String, String> subjects = new HashMap<>();
        subjects.put("数学", "math");
        subjects.put("物理", "physics");
        subjects.put("化学", "chemistry");
        subjects.put("语文", "chinese");
        subjects.put("英语", "english");
        subjects.put("历史", "history");
        subjects.put("地理", "geography");
        subjects.put("生物", "biology");
        for (Map.Entry<String, String> e : subjects.entrySet()) {
            if (question.contains(e.getKey())) return e.getValue();
        }
        return "";
    }

    @Override
    public R<?> analyzeStudyData(Long userId) {
        String prompt = "你是一个学习数据分析师，请根据以下学习数据生成分析报告和建议。";
        String msg = "本周累计学习12.5小时，完成28个任务，正确率82.5%，"
                   + "优势科目：数学、英语，薄弱科目：物理、化学。"
                   + "请给出详细的学习分析和改进建议。";
        String analysis = deepSeekService.chat(prompt, msg);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> analysisMap = new LinkedHashMap<>();
        analysisMap.put("analysis", analysis);
        result.put("analysis", analysisMap);
        result.put("userId", userId);
        return R.ok(result);
    }

    @Override
    public R<?> getGenerateHistory(Long userId, Integer type, Integer page, Integer size) {
        Page<AiGenerateRecord> p = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<AiGenerateRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(AiGenerateRecord::getUserId, userId);
        if (type != null) {
            qw.eq(AiGenerateRecord::getGenerateType, type);
        }
        qw.orderByDesc(AiGenerateRecord::getCreateTime);
        Page<AiGenerateRecord> result = aiRecordMapper.selectPage(p, qw);
        return R.ok(result);
    }
}
