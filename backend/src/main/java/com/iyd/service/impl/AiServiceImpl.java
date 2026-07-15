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
        return "【" + t + "】\n\n"
            + t + "是一个值得深入思考的主题，它关乎每个人的成长与发展。\n"
            + "首先，从个人层面来看，对" + t + "的理解和把握至关重要。\n"
            + "它教会我们如何在复杂的环境中保持独立思考，如何在困难面前坚持不懈。\n\n"
            + "其次，从更广阔的视角来看，" + t + "所蕴含的价值同样不可忽视。\n"
            + "它让我们明白，真正的成功来自于持之以恒的努力和积累。\n\n"
            + "总之，" + t + "是我们成长道路上重要的精神力量。\n"
            + "愿我们都能在学习和实践中不断加深对" + t + "的理解，\n"
            + "以更加坚定的步伐走向美好的未来。";
    }

    private String writePpt(String t) {
        return "PPT大纲：" + t + "\n\n"
            + "封面页：\n  标题：" + t + "\n  副标题：学生智能学习系统出品\n\n"
            + "目录页：\n  1. 引言  2. 核心内容  3. 案例分析  4. 总结\n\n"
            + "第一部分：引言\n  背景介绍与意义\n\n"
            + "第二部分：核心内容\n  基本概念、主要特点、方法与应用\n\n"
            + "第三部分：案例分析\n  典型案例分析与数据对比\n\n"
            + "第四部分：总结与展望\n  核心结论与未来方向";
    }

    private String writeReport(String t) {
        return "学习报告：" + t + "\n\n"
            + "一、学习概况\n  本周主要学习了" + t + "相关内容。\n\n"
            + "二、掌握情况\n  已掌握核心概念，需要加强综合应用。\n\n"
            + "三、存在问题\n  1. 部分概念理解不够深入\n  2. 解题速度有待提高\n\n"
            + "四、改进计划\n  1. 针对薄弱环节专项训练\n  2. 每周完成模拟测试\n  3. 建立错题本";
    }

    private String writeCopy(String t) {
        return "【文案】" + t + "\n\n"
            + "标题：" + t + "，成就更好的自己\n\n"
            + "正文：在学习的道路上，每一步都算数。\n"
            + t + "不是终点，而是新的起点。\n"
            + "坚持，是最好的天赋。\n\n"
            + "立即开始学习之旅，加入iyd智能学习系统。";
    }

    private String writeDiary(String t) {
        return "学习日记：" + t + "\n\n"
            + "今天主要学习了" + t + "的相关知识。\n"
            + "通过学习和思考，我对这个主题有了更深入的理解。\n\n"
            + "遇到的困难：\n"
            + "- 某些概念比较抽象，需要更多时间消化\n"
            + "- 时间管理还需优化\n\n"
            + "下周计划：\n"
            + "1. 继续深入学习\n"
            + "2. 完成配套练习\n"
            + "3. 整理笔记，做好知识归纳";
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
        String answer = generateSmartAnswer(question);
        result.put("answer", answer);
        result.put("question", question);
        return R.ok(result);
    }

    private String generateSmartAnswer(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "您好！请提出您在学习中遇到的问题，我会为您详细解答。";
        }
        String q = question.toLowerCase().trim();
        StringBuilder sb = new StringBuilder();
        sb.append("=== 问题分析 ===\n");
        sb.append("您问的是：").append(question).append("\n\n");

        if (q.contains("数学") || q.contains("函数") || q.contains("方程") || q.contains("几何") || q.contains("公式")) {
            sb.append("=== 数学解答 ===\n");
            if (q.contains("函数")) {
                sb.append("函数是数学中的重要概念，表示两个变量之间的对应关系。\n");
                sb.append("一镒函数：y = kx + b，图像是一条直线\n");
                sb.append("二镒函数：y = ax2 + bx + c，图像是抛物线\n");
                sb.append("学习建议：多做函数图像绘制练习\n");
            } else if (q.contains("方程")) {
                sb.append("方程是含有未知数的等式。\n");
                sb.append("一镒方程：ax + b = 0\n");
                sb.append("二镒方程：ax2 + bx + c = 0\n");
                sb.append("求根公式：x = [-b sqrt(b2-4ac)] / 2a\n");
            } else if (q.contains("几何")) {
                sb.append("几何研究形状、大小和位置关系。\n");
                sb.append("重点：三角形全等/相似、圆的性质、勾股定理\n");
            } else {
                sb.append("数学学习建议：\n");
                sb.append("1. 理解基础概念\n");
                sb.append("2. 多做练习题\n");
                sb.append("3. 建立错题本\n");
                sb.append("4. 归纳总结解题方法\n");
            }
        }
        else if (q.contains("英语") || q.contains("english") || q.contains("单词") || q.contains("语法") || q.contains("阅读")) {
            sb.append("=== 英语解答 ===\n");
            if (q.contains("单词") || q.contains("词汇")) {
                sb.append("单词记忆方法：\n");
                sb.append("使用艾宾浩斯遗忘曲线规划复习\n");
                sb.append("结合例句、词根词缀法记忆\n");
                sb.append("推荐每天记忆15-20个新单词\n");
            } else if (q.contains("语法")) {
                sb.append("语法学习要点：\n");
                sb.append("掌握核心时态：一般现在时、一般过去时、现在完成时\n");
                sb.append("学会分析句子结构\n");
                sb.append("多做语法练习题\n");
            } else if (q.contains("阅读")) {
                sb.append("阅读提升方法：\n");
                sb.append("先看题干，带着问题读文章\n");
                sb.append("注意首尾段和每段首句\n");
                sb.append("每天坚持阅读英文文章\n");
            } else {
                sb.append("英语学习建议：\n");
                sb.append("1. 每天背单词\n");
                sb.append("2. 多听英文材料\n");
                sb.append("3. 大胆开口说英语\n");
                sb.append("4. 阅读英文书籍\n");
            }
        }
        else if (q.contains("物理") || q.contains("力学") || q.contains("电学") || q.contains("运动")) {
            sb.append("=== 物理解答 ===\n");
            sb.append("物理学习建议：\n");
            sb.append("理解基本概念和物理定律\n");
            sb.append("掌握公式的适用条件和推导\n");
            sb.append("多做实验题和综合题\n");
            if (q.contains("力学")) {
                sb.append("力学重点：牛顿三定律、受力分析、功和能\n");
            } else if (q.contains("电学")) {
                sb.append("电学重点：欧姆定律、串并联电路、电功率\n");
            }
        }
        else if (q.contains("语文") || q.contains("作文") || q.contains("古诗") || q.contains("文言文")) {
            sb.append("=== 语文解答 ===\n");
            if (q.contains("作文")) {
                sb.append("写作提升方法：\n");
                sb.append("审题立意，明确中心\n");
                sb.append("规划结构，开头结尾要出彩\n");
                sb.append("多读范文，积累素材\n");
                sb.append("勤于练笔\n");
            } else if (q.contains("文言文")) {
                sb.append("文言文学习技巧：\n");
                sb.append("掌握常见虚词：之、其、而、以、于\n");
                sb.append("积累实词，注意古今异义\n");
                sb.append("多读多背，培养语感\n");
            } else {
                sb.append("语文学习建议：\n");
                sb.append("1. 重视课本基础\n");
                sb.append("2. 广泛阅读\n");
                sb.append("3. 勤做笔记\n");
                sb.append("4. 关注时事\n");
            }
        }
        else if (q.contains("化学") || q.contains("元素") || q.contains("反应")) {
            sb.append("=== 化学解答 ===\n");
            sb.append("熟悉元素周期表\n");
            sb.append("掌握化学方程式配平\n");
            sb.append("理解物质分类：酸、碱、盐、氧化物\n");
            sb.append("重视实验题\n");
        }
        else if (q.contains("学习计划") || q.contains("规划") || q.contains("提高") || q.contains("效率") || q.contains("方法")) {
            sb.append("=== 学习规划建议 ===\n");
            sb.append("高效学习方法：\n");
            sb.append("1. 制定计划，分解小目标\n");
            sb.append("2. 番茄工作法：25+5分钟\n");
            sb.append("3. 费曼学习法：复述所学\n");
            sb.append("4. 间隔重复，定期复习\n");
            sb.append("5. 主动回忆，合书回想\n");
            if (q.contains("提高")) {
                sb.append("提分建议：\n");
                sb.append("分析薄弱科目\n");
                sb.append("建立错题本\n");
                sb.append("做真题模拟\n");
            }
        }
        else {
            sb.append("=== 学习解答 ===\n");
            sb.append("这是一个很好的问题！以下是我的建议：\n\n");
            sb.append("从基础知识入手，理解概念\n");
            sb.append("通过练习巩固，发现薄弱环节\n");
            sb.append("定期复习总结，形成知识体系\n\n");
            sb.append("如需更具体解答，请提供科目和更多细节。\n");
        }

        sb.append("\n温馨提示：学习需要循序渐进，保持耐心和恒心！");
        sb.append("如有疑问欢迎继续提问。");
        return sb.toString();
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
        analysis.put("suggestion", "建议加强物理和化学的基础概念学习");
        analysis.put("weeklyTrend", "学习效率提升15%");
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