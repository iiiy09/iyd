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
        String t = (topic != null && !topic.isEmpty()) ? topic : "青春与梦想";
        if (input != null && !input.isEmpty()) {
            t = input;
        }
        switch (type != null ? type : 1) {
            case 1: return writeEssay(t);
            case 2: return writePpt(t);
            case 3: return writeReport(t);
            case 4: return writeCopy(t);
            case 5: return writeDiary(t);
            default: return writeEssay(t);
        }
    }

    private String writeEssay(String t) {
        return "《" + t + "》\n\n"
            + "在这个飞速发展的时代，" + t + "已然成为我们无法回避的重要命题。"
            + "它不仅关乎个人的成长轨迹，更映射着整个社会的发展方向。"
            + "当我们静下心来审视这一话题时，会发现其中蕴含的深意远超表面的理解。\n\n"
            + "首先，从个人层面来看，" + t + "对每一位青年学子都具有不可替代的意义。"
            + "它教会我们如何在纷繁复杂的世界中保持独立思考的能力，如何在挫折与困难面前坚守初心。"
            + "正如古人所言：\"路漫漫其修远兮，吾将上下而求索。\""
            + "每一次对" + t + "的深入思考，都是对自我认知的一次升华。"
            + "当我们真正理解其内涵时，便会发现生活中的许多困惑都能迎刃而解。\n\n"
            + "其次，从社会角度分析，" + t + "所承载的价值同样不可忽视。"
            + "在信息爆炸的今天，正确的价值观引导显得尤为重要。"
            + "它让我们明白，真正的成功不在于一时的得失，而在于长久的坚持与积累。"
            + "无数例子表明，那些在各行各业取得卓越成就的人，无一不是对" + t + "有着深刻领悟的践行者。"
            + "他们用实际行动诠释了坚持的力量，用时间证明了信念的价值。\n\n"
            + "再者，放眼未来，" + t + "将继续指引我们前行的方向。"
            + "站在新时代的起点上，我们每个人都应该思考：如何在日新月异的变化中保持本心？"
            + "如何在追求个人理想的同时为社会创造价值？"
            + "这些问题的答案，或许就藏在" + t + "的思考之中。\n\n"
            + "综上所述，" + t + "是我们成长道路上不可或缺的精神力量。"
            + "它激励着我们不断前行，在平凡的日子里创造不平凡的价值。"
            + "愿每一位读者都能从中汲取力量，以更加坚定的步伐走向属于自己的辉煌未来。"
            + "让我们携手共进，在" + t + "的指引下，书写属于这个时代的精彩篇章。";
    }

    private String writePpt(String t) {
        return "PPT大纲：《" + t + "》\n\n"
            + "第1页：封面 ——《" + t + "》\n"
            + "   副标题：深入解读与全面分析\n"
            + "   汇报人：XXX | 日期：XXXX年X月\n\n"
            + "第2页：目录\n"
            + "   1. 背景介绍  2. 核心概念  3. 关键分析  4. 案例研究  5. 总结展望\n\n"
            + "第3-4页：背景介绍\n"
            + "   · " + t + "的发展现状\n"
            + "   · 面临的主要问题与挑战\n"
            + "   · 研究的必要性与现实意义\n"
            + "   · 国内外研究现状对比\n\n"
            + "第5-6页：核心概念解析\n"
            + "   · " + t + "的定义与内涵\n"
            + "   · 理论框架与模型\n"
            + "   · 关键术语解释\n"
            + "   · 与传统概念的对比区分\n\n"
            + "第7-9页：关键分析\n"
            + "   · " + t + "的影响因素分析\n"
            + "   · SWOT分析（优势、劣势、机会、威胁）\n"
            + "   · 数据支撑与图表展示\n"
            + "   · 多维度评估指标体系\n\n"
            + "第10-11页：案例研究\n"
            + "   · 典型案例一：XXX学校的成功实践\n"
            + "   · 典型案例二：XXX地区的创新探索\n"
            + "   · 案例对比与经验总结\n"
            + "   · 可推广的实践路径\n\n"
            + "第12-13页：对策建议\n"
            + "   · 短期改进措施\n"
            + "   · 中长期发展规划\n"
            + "   · 资源配置建议\n"
            + "   · 风险预防与应对方案\n\n"
            + "第14页：总结与展望\n"
            + "   · 核心观点回顾\n"
            + "   · 未来发展趋势\n"
            + "   · 下一步行动计划\n\n"
            + "第15页：致谢 & 问答环节\n"
            + "   · 感谢聆听，欢迎提问与交流\n"
            + "   · 联系方式：XXX@example.com";
    }

    private String writeReport(String t) {
        return "实训报告\n\n"
            + "课题名称：" + t + "\n"
            + "报告人：XXX\n"
            + "日期：2026年7月\n\n"
            + "一、实训目的\n"
            + "本次实训旨在深入理解和掌握" + t + "相关的理论知识与实践技能。"
            + "通过理论与实践相结合的方式，提升解决实际问题的能力，"
            + "培养严谨的科研态度和团队协作精神。\n\n"
            + "二、实训内容\n"
            + "1. 理论基础学习：系统学习了" + t + "的基本概念、原理和方法论。\n"
            + "2. 实践操作：完成了相关实验/项目的设计与实施。\n"
            + "3. 数据分析：对实验结果进行了全面的数据收集与分析。\n"
            + "4. 问题探讨：针对实践过程中遇到的困难进行了深入讨论。\n\n"
            + "三、实训过程\n"
            + "第一阶段（第1-2天）：理论学习与方案设计\n"
            + "详细学习了" + t + "的基础理论，查阅了相关文献资料，"
            + "制定了详细的实训方案和实施步骤。\n\n"
            + "第二阶段（第3-5天）：实验/项目实施\n"
            + "按照既定方案开展了实践操作，记录实验数据，"
            + "遇到问题及时调整方案，确保实训顺利进行。\n\n"
            + "第三阶段（第6-7天）：数据分析与报告撰写\n"
            + "对收集的数据进行整理分析，得出结论，撰写实训报告。\n\n"
            + "四、实训成果\n"
            + "1. 掌握了" + t + "的核心知识与技能。\n"
            + "2. 独立完成了XX项实验/项目任务。\n"
            + "3. 撰写了完整的实训报告文档。\n"
            + "4. 提升了分析问题和解决问题的能力。\n\n"
            + "五、心得体会\n"
            + "通过本次实训，我深刻认识到理论与实践相结合的重要性。"
            + "在" + t + "的学习过程中，不仅巩固了专业知识，"
            + "还锻炼了实际操作能力和团队协作意识。"
            + "这段经历将成为我未来发展道路上的宝贵财富。\n\n"
            + "六、参考文献\n"
            + "[1] XXX. " + t + "研究综述[J]. 学术期刊, 2025.\n"
            + "[2] XXX. 相关领域实践指南[M]. 出版社, 2024.";
    }

    private String writeCopy(String t) {
        return "【" + t + "】精选文案\n\n"
            + "═══ 短文案 ═══\n\n"
            + "每一次努力都不会被辜负，"
            + "每一滴汗水都在浇灌梦想的种子。"
            + "在" + t + "的道路上，"
            + "坚持是最美的姿态，成长是最好的回报。\n\n"
            + "═══ 朋友圈文案 ═══\n\n"
            + "📖 今日打卡 | " + t + "\n"
            + "所有的光芒，都需要时间才能被看到。✨\n"
            + "今天的努力，是明天惊喜的铺垫。\n"
            + "加油，追梦人！💪\n\n"
            + "═══ 宣传文案 ═══\n\n"
            + "🌟 " + t + " —— 开启你的成长之旅\n"
            + "你是否也在寻找前进的方向？\n"
            + "是否渴望突破自我的极限？\n"
            + "来这里，与万千同行者一起，\n"
            + "探索" + t + "的无限可能！\n\n"
            + "═══ 金句合集 ═══\n\n"
            + "· 关于" + t + "，最好的时机是十年前，其次是现在。\n"
            + "· " + t + "不是一蹴而就的奇迹，而是日积月累的必然。\n"
            + "· 在" + t + "的路上，慢一点没关系，只要不停下脚步。\n"
            + "· 人生最大的遗憾不是失败，而是我本可以。\n\n"
            + "═══ 活动文案 ═══\n\n"
            + "🔥 限时活动 | " + t + "专属福利\n"
            + "参与即有机会获得：\n"
            + "· 专属学习资料包\n"
            + "· 一对一导师指导\n"
            + "· 精美学习周边礼品\n"
            + "点击参与，让" + t + "从现在开始改变！";
    }

    private String writeDiary(String t) {
        return "周记\n\n"
            + "主题：" + t + "\n"
            + "日期：2026年7月第2周\n\n"
            + "【本周回顾】\n"
            + "这一周过得格外充实。在学习方面，我重点攻克了" + t + "相关的知识点，"
            + "从最初的困惑不解到渐渐领悟，过程虽然辛苦，但收获满满。\n\n"
            + "【学习收获】\n"
            + "1. 掌握了" + t + "的基本概念和核心要点。\n"
            + "2. 完成了3套相关练习题，正确率从60%提升到85%。\n"
            + "3. 阅读了2篇关于" + t + "的拓展文章，拓宽了知识面。\n"
            + "4. 与同学交流讨论，获得了新的解题思路。\n\n"
            + "【遇到的困难】\n"
            + "在" + t + "的学习过程中，有几个难点让我反复琢磨：\n"
            + "· 某些概念比较抽象，需要更多时间消化理解。\n"
            + "· 时间管理还需优化，合理安排各科学习时间。\n\n"
            + "【下周计划】\n"
            + "1. 继续深入" + t + "的学习，确保完全掌握。\n"
            + "2. 完成相关练习和模拟测试。\n"
            + "3. 整理错题笔记，做好知识归纳。\n"
            + "4. 每天坚持30分钟专项训练。\n\n"
            + "【心情记录】\n"
            + "虽然学习之路并不平坦，但每当攻克一个难题，"
            + "那种成就感和喜悦是无可替代的。"
            + "我相信，只要保持对" + t + "的热情和坚持，"
            + "终会看到努力开花结果的那一天。加油！";
    }

    @Override
    public R<?> generatePptOutline(Long userId, String topic, String points) {
        Map<String, Object> result = new HashMap<>();
        String outline = writePpt(topic != null ? topic : "学习主题");
        AiGenerateRecord record = new AiGenerateRecord();
        record.setUserId(userId);
        record.setGenerateType(2);
        record.setInputContent("主题:" + topic + ", 要点:" + points);
        record.setOutputContent(outline);
        aiRecordMapper.insert(record);
        result.put("recordId", record.getId());
        result.put("content", outline);
        result.put("topic", topic);
        return R.ok(result);
    }

    @Override
    public R<?> chatAssist(Long userId, String question) {
        Map<String, Object> result = new HashMap<>();
        StringBuilder answer = new StringBuilder();
        answer.append("您好！关于「").append(question).append("」这个问题，我来为您解答：\n\n");
        answer.append("📚 知识解析：\n");
        answer.append("这是一个很好的问题。让我为您详细梳理相关知识点。\n\n");
        answer.append("🔍 关键要点：\n");
        answer.append("1. 首先我们需要明确概念定义，建立正确的认知框架。\n");
        answer.append("2. 其次要理解核心原理和内在逻辑关系。\n");
        answer.append("3. 最后通过举例说明加深理解。\n\n");
        answer.append("💡 学习建议：\n");
        answer.append("建议您从基础概念入手，循序渐进地深入学习。\n");
        answer.append("如果还有疑问，欢迎继续提问！我会一直在这里为您服务。✨");
        result.put("answer", answer.toString());
        result.put("question", question);
        return R.ok(result);
    }

    @Override
    public R<?> analyzeStudyData(Long userId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> analysis = new LinkedHashMap<>();
        analysis.put("totalStudyTime", "本周累计学习12.5小时");
        analysis.put("completedTasks", 28);
        analysis.put("accuracy", "82.5%");
        analysis.put("strongSubjects", Arrays.asList("数学", "英语"));
        analysis.put("weakSubjects", Arrays.asList("物理", "化学"));
        analysis.put("suggestion", "建议加强物理和化学的基础概念学习，每天安排30分钟的专项训练。");
        analysis.put("weeklyTrend", "相比上周，学习效率提升了15%，继续保持！");
        result.put("analysis", analysis);
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