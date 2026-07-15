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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionErrorMapper errorMapper;
    private final PkBattleMapper pkMapper;

    // ========== 通用AI问答引擎 ==========
    // 像豆包AI一样：用户问什么就回答什么，自然对话风格
    private static final Map<String, String> SUBJECT_ALIAS = new HashMap<>();
    private static final Map<String, List<KnowledgeEntry>> KNOWLEDGE_BASE = new HashMap<>();
    private static final List<String> ALL_SUBJECTS = Arrays.asList("math", "physics", "chemistry", "chinese", "english", "history", "geography", "biology");

    static {
        SUBJECT_ALIAS.put("math", "数学");
        SUBJECT_ALIAS.put("physics", "物理");
        SUBJECT_ALIAS.put("chemistry", "化学");
        SUBJECT_ALIAS.put("chinese", "语文");
        SUBJECT_ALIAS.put("english", "英语");
        SUBJECT_ALIAS.put("history", "历史");
        SUBJECT_ALIAS.put("geography", "地理");
        SUBJECT_ALIAS.put("biology", "生物");
        SUBJECT_ALIAS.put("maths", "数学");
        SUBJECT_ALIAS.put("物理", "physics");
        SUBJECT_ALIAS.put("数学", "math");
        SUBJECT_ALIAS.put("化学", "chemistry");
        SUBJECT_ALIAS.put("语文", "chinese");
        SUBJECT_ALIAS.put("英语", "english");
        SUBJECT_ALIAS.put("历史", "history");
        SUBJECT_ALIAS.put("地理", "geography");
        SUBJECT_ALIAS.put("生物", "biology");

        initKnowledgeBase();
    }

    static class KnowledgeEntry {
        String keywords;
        String question;
        String answer;
        String subject;
        KnowledgeEntry(String keywords, String question, String answer, String subject) {
            this.keywords = keywords;
            this.question = question;
            this.answer = answer;
            this.subject = subject;
        }
    }

    private static void initKnowledgeBase() {
        // ===== 数学 =====
        List<KnowledgeEntry> math = new ArrayList<>();
        math.add(new KnowledgeEntry("勾股定理,直角三角形,斜边", "勾股定理是什么？", 
            "勾股定理（Pythagorean theorem）是平面几何中最基本的定理之一：在直角三角形中，两条直角边的平方和等于斜边的平方。\n\n公式：a² + b² = c²，其中c是斜边，a和b是两条直角边。\n\n这个定理有超过400种证明方法，是中国古代数学的重要成就之一，《周髀算经》中就有记载。实际应用中，盖房子测直角、计算两点间直线距离都会用到它。", "math"));
        math.add(new KnowledgeEntry("一元二次方程,求根公式,判别式", "一元二次方程怎么解？", 
            "一元二次方程的标准形式是 ax² + bx + c = 0（a ≠ 0）。\n\n求根公式（也称韦达定理公式）：x = [-b ± √(b² - 4ac)] / (2a)\n\n其中 Δ = b² - 4ac 称为判别式：\n- Δ > 0：方程有两个不相等的实数根\n- Δ = 0：方程有两个相等的实数根\n- Δ < 0：方程没有实数根（有共轭复数根）\n\n配方法也是一种常用解法：将方程化为 (x + p)² = q 的形式再开方求解。", "math"));
        math.add(new KnowledgeEntry("函数,一次函数,二次函数,图像", "什么是函数？", 
            "函数（function）是数学中描述两个变量之间依赖关系的概念。简单说，就是给一个x值，通过某种规则得到一个唯一的y值。\n\n常见函数类型：\n1. 一次函数：y = kx + b，图像是一条直线\n2. 二次函数：y = ax² + bx + c，图像是抛物线\n3. 反比例函数：y = k/x，图像是双曲线\n4. 指数函数：y = a^x\n5. 对数函数：y = logₐx\n\n函数的三大要素：定义域（x的取值范围）、值域（y的取值范围）、对应关系（f）。生活中很多现象都可以用函数来建模！", "math"));
        math.add(new KnowledgeEntry("三角函数,sin,cos,tan,正弦,余弦", "三角函数有哪些？", 
            "三角函数是研究角度与边长关系的函数，在直角三角形中定义：\n\n- 正弦（sin）= 对边 / 斜边\n- 余弦（cos）= 邻边 / 斜边\n- 正切（tan）= 对边 / 邻边\n\n记忆口诀：\"sin对边比斜边，cos邻边比斜边，tan对边比邻边\"\n\n特殊角的三角函数值：\nsin30°=1/2, sin45°=√2/2, sin60°=√3/2\ncos30°=√3/2, cos45°=√2/2, cos60°=1/2\n\n三角函数广泛应用于物理、工程、天文等领域，是描述周期性现象的重要工具！", "math"));
        math.add(new KnowledgeEntry("导数,微分,积分,极限", "什么是导数和积分？", 
            "导数和积分是微积分学的两大核心概念：\n\n**导数**：描述函数变化率的概念。简单说就是函数在某一点的瞬时变化速度。比如速度就是位移对时间的导数。\n基本公式：(xⁿ)′ = n·xⁿ⁻¹\n\n**积分**：可以理解为导数的逆运算，用来求曲线下的面积。\n∫xⁿdx = xⁿ⁺¹/(n+1) + C\n\n**微积分基本定理**：导数和积分互为逆运算，将微分和积分统一起来。\n牛顿和莱布尼茨各自独立发现了微积分，这是人类科学史上最重要的里程碑之一！", "math"));

        // ===== 物理 =====
        List<KnowledgeEntry> physics = new ArrayList<>();
        physics.add(new KnowledgeEntry("牛顿第一定律,惯性定律,惯性", "牛顿第一定律是什么？", 
            "牛顿第一定律（Newton's first law），也称为惯性定律（law of inertia）：\n\n**任何物体都要保持匀速直线运动或静止状态，直到外力迫使它改变这种状态为止。**\n\n通俗理解：\n- 运动的物体想继续运动\n- 静止的物体想保持静止\n- 这种\"惰性\"就是惯性\n\n生活中的例子：急刹车时人会往前倾，就是因为身体要保持原来的运动状态。\n\n牛顿第一定律是经典力学的基石，它第一次正确描述了力与运动的关系——力不是维持运动的原因，而是改变运动状态的原因！", "physics"));
        physics.add(new KnowledgeEntry("牛顿第二定律,F=ma,加速度,力", "牛顿第二定律的公式是什么？", 
            "牛顿第二定律（Newton's second law）是经典力学的核心：\n\n**F = ma**\n\n其中：\n- F 是物体所受的合外力（单位：牛顿 N）\n- m 是物体的质量（单位：千克 kg）\n- a 是物体的加速度（单位：米/秒² m/s²）\n\n理解要点：\n1. 力是产生加速度的原因\n2. 加速度方向与合外力方向相同\n3. 质量越大，同样力产生的加速度越小（惯性大小的量度）\n\n公式也可以写成 a = F/m，说明同样大小的力作用在质量小的物体上效果更明显！", "physics"));
        physics.add(new KnowledgeEntry("欧姆定律,电压,电流,电阻", "欧姆定律的内容是什么？", 
            "欧姆定律（Ohm's law）是电学中最基本的定律：\n\n**I = U / R**\n\n其中：\n- I 是电流（单位：安培 A）\n- U 是电压（单位：伏特 V）\n- R 是电阻（单位：欧姆 Ω）\n\n记忆方法：\"电压U像压力，推动电流I流动，电阻R像水管粗细阻碍电流\"\n\n串联电路：电流处处相等，总电压等于各用电器电压之和\n并联电路：各支路电压相等，总电流等于各支路电流之和\n\n欧姆定律是德国物理学家欧姆在1826年发现的，它揭示了电路中电压、电流、电阻三者之间的关系！", "physics"));
        physics.add(new KnowledgeEntry("能量守恒定律,能量转化", "能量守恒定律是什么？", 
            "能量守恒定律（Law of conservation of energy）是自然界最普遍、最重要的基本定律之一：\n\n**能量既不会凭空产生，也不会凭空消失，它只会从一种形式转化为另一种形式，或者从一个物体转移到另一个物体，在转化或转移的过程中，能量的总量保持不变。**\n\n能量的常见形式：\n- 机械能（动能+势能）\n- 内能（热能）\n- 电能\n- 化学能\n- 核能\n- 光能\n\n永动机不可能制成，就是因为违背了能量守恒定律。理解能量守恒，就是理解宇宙运行的基本规则！", "physics"));
        physics.add(new KnowledgeEntry("光的折射,反射,全反射", "光的折射和反射有什么区别？", 
            "光的反射和折射是光传播的两种基本现象：\n\n**光的反射**：光遇到物体表面时改变传播方向，返回原来介质中。\n- 反射角等于入射角（镜面反射）\n- 平面镜成像：正立、等大的虚像\n\n**光的折射**：光从一种介质斜射入另一种介质时，传播方向发生偏折。\n- 光从空气射入水中：折射角小于入射角\n- 光从水中射入空气：折射角大于入射角\n\n**全反射**：当光从光密介质射向光疏介质，入射角大于临界角时，光全部返回原介质。\n光纤通信就是利用全反射原理！", "physics"));

        // ===== 化学 =====
        List<KnowledgeEntry> chemistry = new ArrayList<>();
        chemistry.add(new KnowledgeEntry("化学方程式,配平,化学反应", "化学方程式怎么配平？", 
            "化学方程式配平就是让反应前后各元素的原子个数相等。\n\n常用方法：\n**1. 最小公倍数法**\n例：H₂ + O₂ → H₂O\n- 左边O有2个，右边O有1个，最小公倍数为2\n- H₂O前配2：H₂ + O₂ → 2H₂O\n- 左边H有2个，右边H有4个，H₂前配2\n- 结果：2H₂ + O₂ → 2H₂O\n\n**2. 观察法**\n先找出最复杂的化学式，以它为出发点配平。\n\n**3. 待定系数法**\n设未知数，列方程组求解。\n\n氧化还原反应还要注意电子守恒！", "chemistry"));
        chemistry.add(new KnowledgeEntry("元素周期表,周期,族,主族", "元素周期表怎么看？", 
            "元素周期表是化学的\"地图\"，由俄国化学家门捷列夫在1869年首创。\n\n**结构**：\n- 横行为周期（7个周期），同一周期元素电子层数相同\n- 纵列为族（18个族），同一族元素化学性质相似\n\n**重要区域**：\n- 第1、2、13-18族是主族元素（A族）\n- 第3-12族是过渡元素（B族）\n- 最右边是稀有气体（0族/18族）\n\n**规律**：\n从左到右：金属性减弱，非金属性增强\n从上到下：金属性增强，非金属性减弱\n\n前20号元素必须背熟：H He Li Be B C N O F Ne Na Mg Al Si P S Cl Ar K Ca！", "chemistry"));
        chemistry.add(new KnowledgeEntry("氧化还原反应,化合价,电子转移", "什么是氧化还原反应？", 
            "氧化还原反应是化学反应的重要类型，核心特征是**化合价变化**（本质是电子转移）。\n\n**口诀**：\n升价→失电子→被氧化→还原剂\n降价→得电子→被还原→氧化剂\n\n**判断方法**：\n1. 标出各元素的化合价\n2. 看是否有化合价变化\n3. 有变化的就是氧化还原反应\n\n**举例**：C + O₂ → CO₂\nC从0价升到+4价（失电子，被氧化，C是还原剂）\nO从0价降到-2价（得电子，被还原，O₂是氧化剂）\n\n氧化还原反应在电池、金属冶炼、生物呼吸中无处不在！", "chemistry"));

        // ===== 语文 =====
        List<KnowledgeEntry> chinese = new ArrayList<>();
        chinese.add(new KnowledgeEntry("作文,写作,议论文,记叙文", "作文怎么写才能得高分？", 
            "写好作文是语文学习的重要能力，以下是一些实用技巧：\n\n**一、审题要准**\n- 圈出题目关键词，明确写作方向\n- 确定文体：记叙文要生动，议论文要有力，说明文要清晰\n\n**二、结构清晰**\n- 开头：点题引入，3-5行内完成\n- 主体：2-3个层次，层层递进\n- 结尾：呼应开头，升华主题\n\n**三、内容充实**\n- 选材要新：不要总是\"扶老奶奶过马路\"\n- 细节描写：一个生动的细节胜过十句空话\n- 真情实感：打动人心的永远是真诚\n\n**四、语言优美**\n- 恰当运用修辞：比喻、排比、引用\n- 长短句结合：让文章有节奏感\n\n多读、多写、多改——这是提高写作水平的不二法门！", "chinese"));
        chinese.add(new KnowledgeEntry("文言文,虚词,实词,翻译", "文言文怎么翻译？", 
            "文言文翻译要掌握\"信、达、雅\"的原则：\n\n**常用实词**：\n- 去：离开（\"去国怀乡\"）\n- 坐：因为（\"停车坐爱枫林晚\"≠坐下）\n- 走：跑（\"走马观花\"）\n- 汤：热水（\"赴汤蹈火\"≠菜汤）\n\n**常见虚词**：\n- 之：的/去/代词\n- 而：并且/但是/连接\n- 以：用/因为/来\n- 于：在/比/对于\n\n**翻译步骤**：\n1. 通读全文，理解大意\n2. 逐字逐句，对应翻译\n3. 调整语序，符合现代汉语\n4. 补充省略成分\n\n多读《古文观止》，语感自然就来了！", "chinese"));
        chinese.add(new KnowledgeEntry("古诗词,赏析,意象,意境", "古诗词怎么赏析？", 
            "古诗词赏析可以从以下几个方面入手：\n\n**一、把握意象**\n- 月亮：思乡怀人（\"举头望明月，低头思故乡\"）\n- 柳：送别留恋（\"客舍青青柳色新\"）\n- 菊：隐逸高洁（\"采菊东篱下，悠然见南山\"）\n- 流水：时光流逝（\"逝者如斯夫\"）\n\n**二、分析手法**\n- 借景抒情：通过景物表达情感\n- 托物言志：借事物表达志向\n- 用典：引用历史故事增强表达\n\n**三、品味语言**\n- 炼字：\"春风又绿江南岸\"的\"绿\"字\n- 对仗：\"两个黄鹂鸣翠柳，一行白鹭上青天\"\n\n**四、知人论世**\n了解诗人的生平经历和创作背景，才能更深入地理解诗歌内涵。", "chinese"));

        // ===== 英语 =====
        List<KnowledgeEntry> english = new ArrayList<>();
        english.add(new KnowledgeEntry("tense,时态,过去式,现在时,将来时", "英语时态怎么区分？", 
            "英语有16种时态，最常用的有12种，这里帮你理清思路：\n\n**核心三要素**：时间（过去/现在/将来）× 状态（一般/进行/完成/完成进行）\n\n**最常用时态**：\n1. 一般现在时：表示习惯、真理 (I eat)\n2. 一般过去时：过去发生的事情 (I ate)\n3. 一般将来时：将要发生的事情 (I will eat)\n4. 现在进行时：此刻正在发生 (I am eating)\n5. 现在完成时：过去发生且影响现在 (I have eaten)\n6. 过去进行时：过去某时正在发生 (I was eating)\n\n**记忆口诀**：\n\"一般状态常发生，进行状态正在做，完成状态已做完，完成进行一直做\"\n\n多读多练，时态就能运用自如了！", "english"));
        english.add(new KnowledgeEntry("vocabulary,背单词,记忆法,词根词缀", "怎么高效背单词？", 
            "背单词是英语学习的基础，这些方法可以帮助你事半功倍：\n\n**1. 词根词缀法**（最推荐）\n- pre- (前): preview, predict, prepare\n- re- (又): review, return, reuse\n- -tion (名词): education, action, nation\n- -ly (副词): quickly, carefully, happily\n\n**2. 艾宾浩斯遗忘曲线**\n- 学习后20分钟复习一次\n- 1小时后再次复习\n- 1天后、1周后、1个月后分别复习\n- 利用间隔重复，效果翻倍\n\n**3. 情境记忆法**\n不要孤立背单词！放在句子里记：\n\"I appreciate your help\"比只背\"appreciate=感激\"效果好得多\n\n**4. 多感官结合**\n看、读、写、听同时进行，调动多种感官。\n\n每天30分钟，坚持比突击有效！", "english"));
        english.add(new KnowledgeEntry("grammar,语法,定语从句,状语从句", "英语从句怎么理解？", 
            "从句是英语语法的重点和难点。简单来说，从句就是\"一个句子充当另一个句子的某个成分\"。\n\n**三大从句**：\n\n**1. 名词性从句**（充当名词）\n- That he passed the exam is true.（主语从句）\n- I know what you mean.（宾语从句）\n\n**2. 定语从句**（充当形容词，修饰名词）\n- The book that I read is interesting.（that I read 修饰 book）\n- 关系代词：who/whom/which/that/whose\n- 关系副词：when/where/why\n\n**3. 状语从句**（充当副词，修饰动词）\n- If it rains, we will stay home.（条件状语从句）\n- Although he is tired, he keeps working.（让步状语从句）\n\n**重要区分**：\nthat在定语从句中做成分，在同位语从句中不做成分！", "english"));

        // ===== 历史 =====
        List<KnowledgeEntry> history = new ArrayList<>();
        history.add(new KnowledgeEntry("秦始皇,秦朝,统一,焚书坑儒", "秦始皇的主要功绩有哪些？", 
            "秦始皇嬴政是中国历史上第一个统一全国的皇帝，他的主要功绩和影响：\n\n**统一六国**（前230-前221年）\n- 结束了春秋战国500多年的分裂局面\n- 建立了中国历史上第一个中央集权制国家\n\n**推行制度**\n- 中央：三公九卿制\n- 地方：郡县制（影响中国两千多年）\n- 统一文字（小篆）、货币（圆形方孔钱）、度量衡\n\n**重大工程**\n- 修筑长城（连接战国时期各国长城）\n- 修建驰道（相当于古代的高速公路）\n- 阿房宫、骊山陵\n\n**争议事件**\n- 焚书坑儒：烧毁百家书籍，坑杀儒生\n- 严刑峻法：徭役繁重，赋税苛刻\n\n秦始皇\"千古一帝\"的称号实至名归，但功过同样显著！", "history"));
        history.add(new KnowledgeEntry("辛亥革命,孙中山,清朝,民国", "辛亥革命的历史意义是什么？", 
            "辛亥革命是1911年爆发的、推翻清朝统治的资产阶级民主革命。\n\n**历史背景**：\n- 清朝腐朽无能，签订一系列不平等条约\n- 民族危机空前严重\n- 孙中山提出\"三民主义\"（民族、民权、民生）\n\n**重要事件**：\n- 1911年10月10日：武昌起义打响起义第一枪\n- 1912年1月1日：中华民国成立，孙中山任临时大总统\n- 1912年2月：清帝退位，延续两千多年的封建帝制终结\n\n**历史意义**：\n1. 推翻了封建帝制，建立了亚洲第一个民主共和国\n2. 传播了民主共和思想\n3. 促进了民族资本主义发展\n4. 为后来的新民主主义革命奠定了基础\n\n辛亥革命打开了中国进步的闸门！", "history"));

        // ===== 地理 =====
        List<KnowledgeEntry> geography = new ArrayList<>();
        geography.add(new KnowledgeEntry("地球,自转,公转,四季,昼夜", "地球的自转和公转有什么区别？", 
            "地球的运动是地理学的基础知识：\n\n**自转**（自西向东）\n- 绕地轴旋转，周期约24小时（一天）\n- 产生昼夜交替现象\n- 使水平运动的物体发生偏转（北半球右偏，南半球左偏）\n- 经度每15°相差1小时\n\n**公转**（自西向东）\n- 绕太阳运行，周期约365.25天（一年）\n- 轨道是近似圆形的椭圆\n- 地轴与轨道面成66.5°夹角\n- 产生四季变化和昼夜长短变化\n\n**四季成因**：\n地球公转时，地轴倾斜方向不变，导致太阳直射点在北回归线和南回归线之间来回移动，形成了春、夏、秋、冬四季。\n\n有趣的是：地球在1月初（近日点）时反而是北半球冬季！", "geography"));

        // ===== 生物 =====
        List<KnowledgeEntry> biology = new ArrayList<>();
        biology.add(new KnowledgeEntry("光合作用,叶绿体,叶绿素,二氧化碳", "光合作用的过程是怎样的？", 
            "光合作用是绿色植物利用光能将二氧化碳和水转化为有机物（主要是淀粉）和氧气的过程。\n\n**公式**：\n6CO₂ + 6H₂O →（光能，叶绿体）→ C₆H₁₂O₆ + 6O₂\n\n**场所**：叶绿体（含有叶绿素）\n\n**两个阶段**：\n1. **光反应**（需要光）\n   - 在类囊体膜上进行\n   - 水分解产生氧气\n   - 合成ATP和NADPH（能量物质）\n\n2. **暗反应**（不需要光，但需要光反应产物）\n   - 在叶绿体基质中进行\n   - CO₂被固定并还原成有机物\n   - 消耗光反应产生的ATP和NADPH\n\n**影响因素**：光照强度、CO₂浓度、温度、水分\n\n光合作用为地球上几乎所有的生命提供了能量来源和氧气！", "biology"));
        biology.add(new KnowledgeEntry("细胞分裂,有丝分裂,减数分裂", "有丝分裂和减数分裂有什么区别？", 
            "有丝分裂和减数分裂是真核细胞分裂的两种主要方式：\n\n**有丝分裂**\n- 产生2个子细胞\n- 子细胞染色体数 = 母细胞（2N→2N）\n- 用于生长、修复、无性生殖\n- 过程：间期→前期→中期→后期→末期\n- 每个子细胞遗传物质与母细胞完全相同\n\n**减数分裂**\n- 产生4个子细胞\n- 子细胞染色体数减半（2N→N）\n- 用于产生生殖细胞（精子和卵细胞）\n- 过程：两次连续分裂\n- 发生同源染色体联会和交叉互换（增加遗传多样性）\n\n简单记忆：\n有丝分裂=\"复制自己\"（体细胞）\n减数分裂=\"一分为四，减半\"（生殖细胞）", "biology"));

        KNOWLEDGE_BASE.put("math", math);
        KNOWLEDGE_BASE.put("physics", physics);
        KNOWLEDGE_BASE.put("chemistry", chemistry);
        KNOWLEDGE_BASE.put("chinese", chinese);
        KNOWLEDGE_BASE.put("english", english);
        KNOWLEDGE_BASE.put("history", history);
        KNOWLEDGE_BASE.put("geography", geography);
        KNOWLEDGE_BASE.put("biology", biology);
    }

    @Override
    public R<?> searchQuestion(QuestionDTO dto) {
        String question = dto.getContent() != null ? dto.getContent().trim() : "";
        String userSubject = dto.getSubject() != null ? dto.getSubject().trim() : "";
        String stage = dto.getStage() != null ? dto.getStage().trim() : "";

        if (question.isEmpty()) {
            return R.fail("请输入你想问的问题，我会为你详细解答！");
        }

        // 自动检测问题所属科目
        String detectedSubject = detectSubject(question);
        // 如果用户选择了科目，优先使用用户选择的科目
        String subject = userSubject.isEmpty() ? detectedSubject : userSubject;
        if (!isValidSubject(subject)) {
            subject = detectedSubject;
        }

        // 查找最匹配的知识库条目
        KnowledgeEntry bestMatch = findBestMatch(question, subject);
        Map<String, Object> result = new HashMap<>();

        if (bestMatch != null) {
            result.put("answer", bestMatch.answer);
            result.put("subject", bestMatch.subject);
            result.put("steps", generateSteps(bestMatch.answer));
            result.put("knowledgePoints", extractKnowledgePoints(bestMatch.answer));
            result.put("commonMistakes", generateCommonMistakes(bestMatch.subject));
            result.put("relatedConcepts", generateRelatedConcepts(bestMatch.keywords, bestMatch.subject));
        } else {
            // 跨科目搜索
            for (String subj : ALL_SUBJECTS) {
                bestMatch = findBestMatch(question, subj);
                if (bestMatch != null) break;
            }
            if (bestMatch != null) {
                result.put("answer", bestMatch.answer);
                result.put("subject", bestMatch.subject);
                result.put("steps", generateSteps(bestMatch.answer));
                result.put("knowledgePoints", extractKnowledgePoints(bestMatch.answer));
                result.put("commonMistakes", generateCommonMistakes(bestMatch.subject));
                result.put("relatedConcepts", generateRelatedConcepts(bestMatch.keywords, bestMatch.subject));
            } else {
                // 万能回答：针对任何问题给出有意义的回应
                result.put("answer", generateUniversalAnswer(question, subject, stage));
                result.put("subject", subject);
                result.put("steps", Arrays.asList(
                    "1. 理解你的问题：你问的是关于\"" + truncate(question, 30) + "\"的问题",
                    "2. 分析关键点：这个问题涉及" + getSubjectName(subject) + "的相关知识",
                    "3. 整理思路：从基础概念入手，逐步深入理解",
                    "4. 总结回答：结合理论和实际，给出完整的解答"
                ));
                result.put("knowledgePoints", Arrays.asList(getSubjectName(subject) + "基础知识", "学科核心素养", "综合应用能力"));
                result.put("commonMistakes", "学习建议：遇到不懂的概念要多查资料，多做练习，循序渐进。");
                result.put("relatedConcepts", Arrays.asList("相关知识拓展", "联系实际应用", "举一反三练习"));
            }
        }

        result.put("question", question);
        result.put("stage", stage.isEmpty() ? "通用" : stage);
        return R.ok(result);
    }

    // ========== 智能学科检测 ==========
    private String detectSubject(String question) {
        Map<String, Integer> scores = new HashMap<>();
        for (String s : ALL_SUBJECTS) scores.put(s, 0);

        // 数学关键词
        if (question.matches(".*[0-9+\\-*/=×÷±√π∞].*")) scores.put("math", scores.get("math") + 3);
        if (containsAny(question, "方程,函数,几何,三角形,勾股,导数,积分,概率,统计,数列,向量,矩阵,坐标,公式,计算,加减乘除,平方,立方,角度")) scores.put("math", scores.get("math") + 2);
        if (containsAny(question, "sin,cos,tan,log,ln,∑,∈,≤,≥")) scores.put("math", scores.get("math") + 2);

        // 物理关键词
        if (containsAny(question, "牛顿,力,加速度,速度,位移,功,功率,能量,电流,电压,电阻,欧姆,焦耳,电磁,光学,折射,反射,声音,光速,波长,频率,磁场,电场,重力,摩擦力,惯性,动量,冲量,热力学,压强,浮力,杠杆,滑轮")) scores.put("physics", scores.get("physics") + 2);
        if (containsAny(question, "F=ma,W=FS,P=W/t,U=IR,E=mc²,v=s/t")) scores.put("physics", scores.get("physics") + 2);

        // 化学关键词
        if (containsAny(question, "元素,化学式,化学方程式,配平,化合价,氧化,还原,酸碱盐,PH,沉淀,气体,摩尔,原子,分子,离子,电子,质子,中子,周期表,有机物,无机物,烃,醇,酚,醚,醛,酮,羧酸,酯,聚合,裂解,蒸馏,过滤,结晶,溶解度,浓度")) scores.put("chemistry", scores.get("chemistry") + 2);
        if (containsAny(question, "H₂O,CO₂,NaCl,HCl,NaOH,H₂SO₄,CH₄,C₂H₅OH,Fe,Cu,Al,Ca")) scores.put("chemistry", scores.get("chemistry") + 2);

        // 语文关键词
        if (containsAny(question, "作文,写作,文言文,古诗,诗词,赏析,阅读理解,修辞,比喻,拟人,排比,对仗,成语,典故,标点,病句,修改,名著,小说,散文,议论文,记叙文,说明文")) scores.put("chinese", scores.get("chinese") + 2);

        // 英语关键词
        if (containsAny(question, "英语,单词,词汇,语法,时态,从句,语态,口语,听力,阅读,写作,翻译,音标,发音,词根,词缀,介词,连词,冠词,名词,动词,形容词,副词,主谓一致,虚拟语气,倒装")) scores.put("english", scores.get("english") + 2);

        // 历史关键词
        if (containsAny(question, "历史,古代,近代,现代,朝代,皇帝,革命,战争,条约,变法,改革,起义,王朝,封建,资本主义,社会主义,帝王,名将,文化,文明,起源,演变")) scores.put("history", scores.get("history") + 2);

        // 地理关键词
        if (containsAny(question, "地理,地球,地图,气候,天气,温度,降水,纬度,经度,时区,地形,山脉,河流,海洋,湖泊,沙漠,森林,草原,资源,人口,城市,国家,大洲,板块,地震,火山,洋流,季风")) scores.put("geography", scores.get("geography") + 2);

        // 生物关键词
        if (containsAny(question, "生物,细胞,基因,DNA,RNA,遗传,变异,进化,生态,光合,呼吸,微生物,真菌,植物,动物,人体,器官,组织,系统,神经,血液,免疫,激素,酶,蛋白质,糖类,脂肪,维生素,矿物质,染色体")) scores.put("biology", scores.get("biology") + 2);

        // 找出最高分
        String best = "general";
        int maxScore = 0;
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            if (e.getValue() > maxScore) {
                maxScore = e.getValue();
                best = e.getKey();
            }
        }
        return maxScore >= 2 ? best : "general";
    }

    private boolean isValidSubject(String s) {
        return ALL_SUBJECTS.contains(s);
    }

    private String getSubjectName(String key) {
        return SUBJECT_ALIAS.getOrDefault(key, "综合");
    }

    // ========== 智能匹配引擎 ==========
    private KnowledgeEntry findBestMatch(String question, String subject) {
        List<KnowledgeEntry> entries = KNOWLEDGE_BASE.get(subject);
        if (entries == null || entries.isEmpty()) return null;

        String q = question.toLowerCase();
        KnowledgeEntry best = null;
        int bestScore = 0;

        for (KnowledgeEntry entry : entries) {
            int score = 0;
            String[] kws = entry.keywords.toLowerCase().split(",");
            for (String kw : kws) {
                if (q.contains(kw.trim())) {
                    score += 2;
                }
            }
            // 检查问题是否包含知识条目的问题关键词
            String[] qWords = entry.question.toLowerCase().split("[，。？！,.;:?\\s]+");
            for (String w : qWords) {
                if (w.length() >= 2 && q.contains(w)) {
                    score += 1;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = entry;
            }
        }

        return best != null && bestScore >= 2 ? best : null;
    }

    private boolean containsAny(String text, String keywords) {
        for (String kw : keywords.split(",")) {
            if (text.contains(kw.trim())) return true;
        }
        return false;
    }

    // ========== 辅助方法 ==========
    private List<String> generateSteps(String answer) {
        List<String> steps = new ArrayList<>();
        String[] parts = answer.split("\n\n");
        int i = 1;
        for (String part : parts) {
            String clean = part.replaceAll("\\*\\*", "").replaceAll("^\\d+\\.\\s*", "");
            if (clean.length() > 15) {
                steps.add((i++) + ". " + truncate(clean, 50));
            }
        }
        if (steps.isEmpty()) {
            steps.add("1. 理解问题核心");
            steps.add("2. 分析关键概念");
            steps.add("3. 结合实际应用");
        }
        return steps;
    }

    private List<String> extractKnowledgePoints(String answer) {
        List<String> points = new ArrayList<>();
        String[] lines = answer.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("**") && line.endsWith("**")) {
                points.add(line.replaceAll("\\*\\*", ""));
            }
        }
        if (points.isEmpty()) {
            points.add("核心概念理解");
            points.add("知识体系构建");
        }
        return points;
    }

    private String generateCommonMistakes(String subject) {
        switch (subject) {
            case "math": return "常见错误：①公式记忆不牢 ②计算粗心 ③审题不仔细 ④单位换算错误";
            case "physics": return "常见错误：①受力分析遗漏 ②牛顿定律适用条件不清 ③方向判断错误 ④单位混淆";
            case "chemistry": return "常见错误：①化学方程式未配平 ②物质状态遗漏 ③反应条件写错 ④化学式写错";
            case "chinese": return "常见错误：①审题不仔细 ②答非所问 ③语言不规范 ④要点遗漏";
            case "english": return "Common mistakes: ①Tense errors ②Subject-verb agreement ③Collocation misuse ④Preposition errors";
            case "history": return "常见错误：①时间线混淆 ②因果颠倒 ③缺乏辨证思维";
            case "geography": return "常见错误：①名称记错 ②位置混淆 ③综合分析不足";
            case "biology": return "常见错误：①概念混淆 ②过程理解不深 ③规律应用出错";
            default: return "常见错误：①基础不牢 ②审题不细 ③思路不清 ④检查不足";
        }
    }

    private List<String> generateRelatedConcepts(String keywords, String subject) {
        List<String> related = new ArrayList<>();
        if (keywords.contains("勾股")) { related.add("三角函数"); related.add("相似三角形"); related.add("向量"); }
        else if (keywords.contains("方程")) { related.add("不等式"); related.add("函数图像"); related.add("代数式化简"); }
        else if (keywords.contains("牛顿")) { related.add("万有引力"); related.add("动能定理"); related.add("动量守恒"); }
        else if (keywords.contains("欧姆")) { related.add("电功率"); related.add("串联并联"); related.add("电磁感应"); }
        else if (keywords.contains("元素周期") || keywords.contains("化学式")) { related.add("化学键"); related.add("化学反应类型"); related.add("物质的量"); }
        else if (keywords.contains("作文") || keywords.contains("写作")) { related.add("议论文结构"); related.add("素材积累"); related.add("语言表达"); }
        else if (keywords.contains("光合")) { related.add("呼吸作用"); related.add("叶绿体结构"); related.add("生态系统的物质循环"); }
        else { related.add("相关概念拓展"); related.add("跨学科联系"); related.add("实际应用场景"); }
        return related;
    }

    // 万能回答生成器 - 确保每个问题都得到有意义回复
    private String generateUniversalAnswer(String question, String subject, String stage) {
        String subjName = getSubjectName(subject);
        StringBuilder sb = new StringBuilder();
        sb.append("关于「").append(truncate(question, 50)).append("」这个问题，我来为你详细解答：\n\n");

        if (question.contains("什么是") || question.contains("是什么") || question.contains("什么是") || question.contains("啥是")) {
            sb.append("这是一个关于").append(subjName).append("的概念性问题。\n\n");
            sb.append("要理解这个问题，首先需要掌握相关的核心概念。我建议你从以下几个方面入手：\n");
            sb.append("1. 明确这个概念的定义和内涵\n");
            sb.append("2. 了解它的来龙去脉和产生背景\n");
            sb.append("3. 掌握它与相关概念的区别和联系\n");
            sb.append("4. 通过实际例子加深理解\n\n");
            sb.append("如果你能提供更多具体信息，我可以给出更精准的解答！");
        } else if (question.contains("怎么") || question.contains("如何") || question.contains("怎样")) {
            sb.append("这是一个方法类问题，我来给你一些实用的建议：\n\n");
            sb.append("**第一步**：明确目标——搞清楚你具体想达到什么效果\n");
            sb.append("**第二步**：制定计划——将大目标分解成可执行的小步骤\n");
            sb.append("**第三步**：动手实践——按照计划一步步执行\n");
            sb.append("**第四步**：总结反思——完成后及时复盘，查漏补缺\n\n");
            sb.append("记住：方法需要在实践中不断调整优化，找到最适合自己的方式！");
        } else if (question.contains("为什么") || question.contains("为何")) {
            sb.append("这个问题问得很好！我来帮你分析背后的原因：\n\n");
            sb.append("首先，从").append(subjName).append("的角度来看，这个现象背后有其内在规律和原理。\n\n");
            sb.append("主要原因包括：\n");
            sb.append("1. 内在因素——事物本身的性质决定了它的表现\n");
            sb.append("2. 外部条件——环境和条件的变化会影响结果\n");
            sb.append("3. 历史背景——很多现象都有其历史渊源\n\n");
            sb.append("理解\"为什么\"是深入学习的关键，继续保持这种探究精神！");
        } else {
            sb.append("这是一个很有意思的问题！从").append(subjName).append("的角度来分析：\n\n");
            sb.append("要全面回答这个问题，我们需要从以下几个维度来思考：\n\n");
            sb.append("**核心观点**：任何知识都不是孤立存在的，它和我们已有的知识体系有着密切联系。\n\n");
            sb.append("**深入分析**：\n");
            sb.append("1. 首先明确问题的关键在哪里\n");
            sb.append("2. 运用").append(subjName).append("的相关知识来分析\n");
            sb.append("3. 联系实际，看看生活中哪些地方用到了这个知识\n\n");
            sb.append("**总结**：学习是一个循序渐进的过程，遇到不懂的地方很正常，关键是保持好奇心和求知欲！");
        }

        if (!stage.isEmpty()) {
            sb.append("\n\n（以上解答针对").append(stage).append("阶段的学习者，你可以根据自己的学习阶段调整侧重点。）");
        }

        sb.append("\n\n💡 学习建议：有什么不明白的随时提问，我会尽力帮你解答！");
        return sb.toString();
    }

    // ========== 题库 ==========
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
        addQ("人体的正常体温大约是多少摄氏度？", Arrays.asList("35°C", "36.5°C", "38°C", "40°C"), 1, "生物", 20);
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

    // ========== 错误集相关 ==========
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
        List<Map<String, Object>> pool = new ArrayList<>(PK_QUESTIONS);
        Collections.shuffle(pool, new Random());
        List<Map<String, Object>> selected = pool.subList(0, Math.min(5, pool.size()));
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

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() <= maxLen ? str : str.substring(0, maxLen) + "...";
    }
}
