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

    // Rich question bank
    private static final List<Map<String, Object>> PK_QUESTIONS = new ArrayList<>();
    static {
        addQ("光合作用的主要产物是什么？", Arrays.asList("氧气", "氮气", "二氧化碳", "氢气"), 0, "生物", 20);
        addQ("中国最长的河流是哪一条？", Arrays.asList("黄河", "长江", "珠江", "淮河"), 1, "地理", 20);
        addQ("圆的面积公式是什么？", Arrays.asList("πr", "2πr", "πr²", "2πr²"), 2, "数学", 20);
        addQ("'床前明月光'的作者是谁？", Arrays.asList("杜甫", "白居易", "李白", "王维"), 2, "语文", 20);
        addQ("水的化学式是什么？", Arrays.asList("H₂O", "CO₂", "O₂", "NaCl"), 0, "化学", 20);
        addQ("地球绕太阳一周大约需要多长时间？", Arrays.asList("一个月", "一年", "一天", "十年"), 1, "地理", 20);
        addQ("勾股定理中，直角三角形的斜边平方等于什么？", Arrays.asList("两直角边之和", "两直角边之积", "两直角边平方和", "两直角边平方差"), 2, "数学", 20);
        addQ("'春蚕到死丝方尽'的下一句是什么？", Arrays.asList("蜡炬成灰泪始干", "化作春泥更护花", "不破楼兰终不还", "一片冰心在玉壶"), 0, "语文", 20);
        addQ("牛顿第一定律又称为什么定律？", Arrays.asList("万有引力定律", "惯性定律", "加速度定律", "作用力与反作用力定律"), 1, "物理", 20);
        addQ("Which of the following is the past tense of 'go'?", Arrays.asList("goed", "went", "gone", "going"), 1, "英语", 20);
        addQ("人体的正常体温大约是多少摄氏度？", Arrays.asList("35°C", "36.5°C", "38°C", "40°C"), 1, "生物", 20);
        addQ("世界上最大的海洋是哪一个？", Arrays.asList("大西洋", "印度洋", "太平洋", "北冰洋"), 2, "地理", 20);
        addQ("化学元素周期表中，Fe代表什么元素？", Arrays.asList("氟", "铁", "锌", "铜"), 1, "化学", 20);
        addQ("抗日战争全面爆发的标志性事件是什么？", Arrays.asList("九一八事变", "七七事变（卢沟桥事变）", "西安事变", "南京大屠杀"), 1, "历史", 20);
        addQ("在C语言中，int类型通常占用几个字节？", Arrays.asList("1字节", "2字节", "4字节", "8字节"), 2, "计算机", 20);
    }

    private static void addQ(String content, List<String> options, int answer, String subject, int score) {
        Map<String, Object> q = new LinkedHashMap<>();
        q.put("content", content);
        q.put("options", options);
        q.put("answer", answer);
        q.put("subject", subject);
        q.put("score", score);
        PK_QUESTIONS.add(q);
    }

    @Override
    public R<?> searchQuestion(QuestionDTO dto) {
        Map<String, Object> result = new HashMap<>();
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
        // Shuffle and pick 5 random questions
        List<Map<String, Object>> pool = new ArrayList<>(PK_QUESTIONS);
        Collections.shuffle(pool, new Random());
        List<Map<String, Object>> selected = pool.subList(0, Math.min(5, pool.size()));
        // Assign IDs
        for (int i = 0; i < selected.size(); i++) {
            selected.get(i).put("id", i + 1);
        }
        return R.ok(selected);
    }

    @Override
    public R<?> submitPkBattle(Long battleId, Long userId, String answers) {
        Map<String, Object> result = new HashMap<>();
        result.put("score", 85);
        result.put("rank", "当前排名第1");
        result.put("xp", "+25经验值");
        return R.ok(result);
    }
}