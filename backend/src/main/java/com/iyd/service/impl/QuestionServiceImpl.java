package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyd.common.R;
import com.iyd.dto.QuestionDTO;
import com.iyd.entity.PkBattle;
import com.iyd.entity.QuestionError;
import com.iyd.mapper.PkBattleMapper;
import com.iyd.mapper.QuestionErrorMapper;
import com.iyd.service.DeepSeekService;
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
    private final DeepSeekService deepSeekService;

    @Override
    public R<?> searchQuestion(QuestionDTO dto) {
        String question = dto.getContent() != null ? dto.getContent().trim() : "";
        if (question.isEmpty()) {
            return R.fail("请输入你想问的问题");
        }

        String subject = dto.getSubject() != null ? dto.getSubject().trim() : "";
        String stage = dto.getStage() != null ? dto.getStage().trim() : "";

        String prompt = "你是一个专业的学习辅导AI助手，精通各学科知识。\n"
                     + "请用中文直接回答用户的问题，回答要详细准确、条理清晰。";

        String answer = deepSeekService.chat(prompt, question);

        Map<String, Object> result = new HashMap<>();
        result.put("answer", answer);
        result.put("subject", subject.isEmpty() ? "综合" : subject);
        result.put("question", question);
        result.put("stage", stage.isEmpty() ? "通用" : stage);
        return R.ok(result);
    }

    @Override
    public R<?> getErrorList(Long userId, Integer page, Integer size) {
        Page<QuestionError> p = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<QuestionError> qw = new LambdaQueryWrapper<>();
        qw.eq(QuestionError::getUserId, userId).orderByDesc(QuestionError::getCreateTime);
        return R.ok(errorMapper.selectPage(p, qw));
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
        
        // Math questions
        Map<String, Object> q1 = new HashMap<>();
        q1.put("subject", "math"); q1.put("content", "若直角三角形的两条直角边分别为3和4，则斜边长为？");
        q1.put("options", Arrays.asList("A.5", "B.6", "C.7", "D.8"));
        q1.put("answer", 0); q1.put("score", 20); q1.put("tip", "勾股定理：a²+b²=c²");
        questions.add(q1);
        
        Map<String, Object> q2 = new HashMap<>();
        q2.put("subject", "math"); q2.put("content", "一元二次方程 x²-5x+6=0 的解是？");
        q2.put("options", Arrays.asList("A.x=2或x=3", "B.x=1或x=6", "C.x=-2或x=-3", "D.x=5或x=1"));
        q2.put("answer", 0); q2.put("score", 20); q2.put("tip", "因式分解：(x-2)(x-3)=0");
        questions.add(q2);
        
        Map<String, Object> q3 = new HashMap<>();
        q3.put("subject", "math"); q3.put("content", "sin 30°的值等于？");
        q3.put("options", Arrays.asList("A.0", "B.1/2", "C.√2/2", "D.√3/2"));
        q3.put("answer", 1); q3.put("score", 20); q3.put("tip", "sin 30° = 1/2");
        questions.add(q3);
        
        Map<String, Object> q4 = new HashMap<>();
        q4.put("subject", "math"); q4.put("content", "圆的面积公式是？");
        q4.put("options", Arrays.asList("A.2πr", "B.πr²", "C.πd", "D.4πr²"));
        q4.put("answer", 1); q4.put("score", 20); q4.put("tip", "S = πr²");
        questions.add(q4);
        
        Map<String, Object> q5 = new HashMap<>();
        q5.put("subject", "math"); q5.put("content", "下列哪个数是无理数？");
        q5.put("options", Arrays.asList("A.3.14", "B.√2", "C.1/3", "D.0.5"));
        q5.put("answer", 1); q5.put("score", 20); q5.put("tip", "√2是无理数");
        questions.add(q5);
        
        // Physics questions
        Map<String, Object> q6 = new HashMap<>();
        q6.put("subject", "physics"); q6.put("content", "光在真空中的传播速度约为？");
        q6.put("options", Arrays.asList("A.3×10⁸m/s", "B.3×10⁶m/s", "C.3×10⁴m/s", "D.3×10²m/s"));
        q6.put("answer", 0); q6.put("score", 20); q6.put("tip", "光速c = 3×10⁸m/s");
        questions.add(q6);
        
        Map<String, Object> q7 = new HashMap<>();
        q7.put("subject", "physics"); q7.put("content", "下列哪个是力的单位？");
        q7.put("options", Arrays.asList("A.千克(kg)", "B.牛顿(N)", "C.焦耳(J)", "D.瓦特(W)"));
        q7.put("answer", 1); q7.put("score", 20); q7.put("tip", "力的单位是牛顿(N)");
        questions.add(q7);
        
        Map<String, Object> q8 = new HashMap<>();
        q8.put("subject", "physics"); q8.put("content", "水的密度是？");
        q8.put("options", Arrays.asList("A.0.5g/cm³", "B.1.0g/cm³", "C.1.5g/cm³", "D.2.0g/cm³"));
        q8.put("answer", 1); q8.put("score", 20); q8.put("tip", "水的密度为1.0g/cm³");
        questions.add(q8);
        
        Map<String, Object> q9 = new HashMap<>();
        q9.put("subject", "physics"); q9.put("content", "声音在空气中传播的速度约为？");
        q9.put("options", Arrays.asList("A.340m/s", "B.500m/s", "C.1000m/s", "D.3000m/s"));
        q9.put("answer", 0); q9.put("score", 20); q9.put("tip", "声速约340m/s");
        questions.add(q9);
        
        Map<String, Object> q10 = new HashMap<>();
        q10.put("subject", "physics"); q10.put("content", "下列哪种现象属于光的反射？");
        q10.put("options", Arrays.asList("A.彩虹", "B.水中倒影", "C.海市蜃楼", "D.光的色散"));
        q10.put("answer", 1); q10.put("score", 20); q10.put("tip", "水中倒影是光的反射");
        questions.add(q10);
        
        // Chemistry questions
        Map<String, Object> q11 = new HashMap<>();
        q11.put("subject", "chemistry"); q11.put("content", "水的化学式是？");
        q11.put("options", Arrays.asList("A.H₂O", "B.CO₂", "C.NaCl", "D.HCl"));
        q11.put("answer", 0); q11.put("score", 20); q11.put("tip", "水由H和O组成");
        questions.add(q11);
        
        Map<String, Object> q12 = new HashMap<>();
        q12.put("subject", "chemistry"); q12.put("content", "空气中含量最多的气体是？");
        q12.put("options", Arrays.asList("A.氧气", "B.氮气", "C.二氧化碳", "D.稀有气体"));
        q12.put("answer", 1); q12.put("score", 20); q12.put("tip", "氮气约占78%");
        questions.add(q12);
        
        Map<String, Object> q13 = new HashMap<>();
        q13.put("subject", "chemistry"); q13.put("content", "下列哪个是酸？");
        q13.put("options", Arrays.asList("A.NaOH", "B.HCl", "C.NaCl", "D.CaO"));
        q13.put("answer", 1); q13.put("score", 20); q13.put("tip", "HCl是盐酸");
        questions.add(q13);
        
        Map<String, Object> q14 = new HashMap<>();
        q14.put("subject", "chemistry"); q14.put("content", "铁在氧气中燃烧生成？");
        q14.put("options", Arrays.asList("A.FeO", "B.Fe₂O₃", "C.Fe₃O₄", "D.Fe(OH)₃"));
        q14.put("answer", 2); q14.put("score", 20); q14.put("tip", "生成Fe₃O₄");
        questions.add(q14);
        
        Map<String, Object> q15 = new HashMap<>();
        q15.put("subject", "chemistry"); q15.put("content", "pH=7的溶液呈什么性？");
        q15.put("options", Arrays.asList("A.酸性", "B.碱性", "C.中性", "D.不确定"));
        q15.put("answer", 2); q15.put("score", 20); q15.put("tip", "pH=7为中性");
        questions.add(q15);
        
        // Chinese questions
        Map<String, Object> q16 = new HashMap<>();
        q16.put("subject", "chinese"); q16.put("content", "床前明月光的下一句是？");
        q16.put("options", Arrays.asList("A.疑是地上霜", "B.举头望明月", "C.低头思故乡", "D.月是故乡明"));
        q16.put("answer", 0); q16.put("score", 20); q16.put("tip", "李白《静夜思》");
        questions.add(q16);
        
        Map<String, Object> q17 = new HashMap<>();
        q17.put("subject", "chinese"); q17.put("content", "下列哪个成语与项羽有关？");
        q17.put("options", Arrays.asList("A.卧薪尝胆", "B.破釜沉舟", "C.负荆请罪", "D.完璧归赵"));
        q17.put("answer", 1); q17.put("score", 20); q17.put("tip", "破釜沉舟出自项羽");
        questions.add(q17);
        
        Map<String, Object> q18 = new HashMap<>();
        q18.put("subject", "chinese"); q18.put("content", "但愿人长久的下一句是？");
        q18.put("options", Arrays.asList("A.千里共婵娟", "B.低头思故乡", "C.月有阴晴圆缺", "D.此事古难全"));
        q18.put("answer", 0); q18.put("score", 20); q18.put("tip", "苏轼《水调歌头》");
        questions.add(q18);
        
        Map<String, Object> q19 = new HashMap<>();
        q19.put("subject", "chinese"); q19.put("content", "鲁迅《狂人日记》是中国第一部？");
        q19.put("options", Arrays.asList("A.长篇小说", "B.短篇小说", "C.现代白话小说", "D.散文集"));
        q19.put("answer", 2); q19.put("score", 20); q19.put("tip", "第一部现代白话小说");
        questions.add(q19);
        
        Map<String, Object> q20 = new HashMap<>();
        q20.put("subject", "chinese"); q20.put("content", "下列哪个不是四大发明？");
        q20.put("options", Arrays.asList("A.造纸术", "B.地动仪", "C.印刷术", "D.火药"));
        q20.put("answer", 1); q20.put("score", 20); q20.put("tip", "地动仪不是四大发明");
        questions.add(q20);
        
        // English questions
        Map<String, Object> q21 = new HashMap<>();
        q21.put("subject", "english"); q21.put("content", "What is the meaning of apple?");
        q21.put("options", Arrays.asList("A.apple", "B.banana", "C.orange", "D.grape"));
        q21.put("answer", 0); q21.put("score", 20); q21.put("tip", "apple means apple");
        questions.add(q21);
        
        Map<String, Object> q22 = new HashMap<>();
        q22.put("subject", "english"); q22.put("content", "I ___ a student.");
        q22.put("options", Arrays.asList("A.is", "B.am", "C.are", "D.be"));
        q22.put("answer", 1); q22.put("score", 20); q22.put("tip", "I am a student.");
        questions.add(q22);
        
        Map<String, Object> q23 = new HashMap<>();
        q23.put("subject", "english"); q23.put("content", "How many days in a week?");
        q23.put("options", Arrays.asList("A.5", "B.6", "C.7", "D.8"));
        q23.put("answer", 2); q23.put("score", 20); q23.put("tip", "Seven days a week");
        questions.add(q23);
        
        Map<String, Object> q24 = new HashMap<>();
        q24.put("subject", "english"); q24.put("content", "What color is the sky?");
        q24.put("options", Arrays.asList("A.Red", "B.Green", "C.Yellow", "D.Blue"));
        q24.put("answer", 3); q24.put("score", 20); q24.put("tip", "The sky is blue");
        questions.add(q24);
        
        Map<String, Object> q25 = new HashMap<>();
        q25.put("subject", "english"); q25.put("content", "She ___ to school every day.");
        q25.put("options", Arrays.asList("A.go", "B.goes", "C.going", "D.went"));
        q25.put("answer", 1); q25.put("score", 20); q25.put("tip", "Third person singular");
        questions.add(q25);
        
        Collections.shuffle(questions, new Random());
        return R.ok(questions);
    }
    public R<?> submitPkBattle(Long battleId, Long userId, String answers) {
        PkBattle battle = new PkBattle();
        battle.setUser1Id(userId);
        battle.setQuestions(answers);
        battle.setUser1Score(randScore());
        battle.setUser2Score(randScore()/2);
        battle.setStatus(0);
        battle.setCreateTime(LocalDateTime.now());
        pkMapper.insert(battle);
        return R.ok(battle);
    }

    private int randScore() {
        return new Random().nextInt(41) + 60;
    }
}
