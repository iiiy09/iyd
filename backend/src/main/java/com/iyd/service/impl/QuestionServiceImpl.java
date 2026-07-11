package com.iyd.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyd.common.R;
import com.iyd.dto.QuestionDTO;
import com.iyd.entity.PkBattle;
import com.iyd.entity.QuestionError;
import com.iyd.mapper.PkBattleMapper;
import com.iyd.mapper.QuestionErrorMapper;
import com.iyd.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionErrorMapper errorMapper;
    private final PkBattleMapper pkMapper;

    @Override
    public R<?> searchQuestion(QuestionDTO dto) {
        Map<String, Object> result = new HashMap<>();
        // Simulated AI response with step-by-step analysis
        result.put("question", dto.getContent() != null ? dto.getContent() : "题目内容");
        result.put("steps", Arrays.asList(
                "第一步：审题分析，明确已知条件和所求",
                "第二步：选择解题方法，建立等量关系",
                "第三步：代入计算，得出结果",
                "第四步：验证答案，检查合理性"
        ));
        result.put("answer", "标准答案示例");
        result.put("knowledgePoints", Arrays.asList("相关知识点1", "相关知识点2"));
        result.put("commonMistakes", "常见错误：注意单位统一，检查计算过程");
        result.put("similarQuestions", "可尝试同类变式题巩固练习");
        return R.ok(result);
    }

    @Override
    public R<?> getErrorList(Long userId, Integer page, Integer size) {
        Page<QuestionError> p = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<QuestionError> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionError::getUserId, userId).orderByDesc(QuestionError::getCreateTime);
        Page<QuestionError> result = errorMapper.selectPage(p, qw);
        return R.ok(result);
    }

    @Override
    public R<?> markMastered(Long errorId) {
        QuestionError error = errorMapper.selectById(errorId);
        if (error != null) {
            error.setMastered(1);
            errorMapper.updateById(error);
        }
        return R.ok();
    }

    @Override
    public R<?> getPkQuestions(String stage) {
        List<Map<String, Object>> questions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> q = new HashMap<>();
            q.put("id", i + 1);
            q.put("content", "PK题目内容 #" + (i + 1));
            q.put("options", Arrays.asList("A. 选项A", "B. 选项B", "C. 选项C", "D. 选项D"));
            q.put("score", 20);
            questions.add(q);
        }
        return R.ok(questions);
    }

    @Override
    public R<?> submitPkBattle(Long battleId, Long userId, String answers) {
        Map<String, Object> result = new HashMap<>();
        result.put("score", 85);
        result.put("rank", "当前排名第3");
        result.put("xp", "+25经验值");
        return R.ok(result);
    }
}
