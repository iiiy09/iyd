package com.iyd.config;

import com.iyd.entity.EnglishWord;
import com.iyd.entity.StudyResource;
import com.iyd.mapper.EnglishWordMapper;
import com.iyd.mapper.StudyResourceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StudyResourceMapper resourceMapper;
    private final EnglishWordMapper wordMapper;

    @Override
    public void run(String... args) {
        seedResources();
        seedWords();
    }

    private void seedResources() {
        Long count = resourceMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("DB has resources, skip seeding");
            return;
        }
        log.info("Seeding sample resources...");
        addResource("2024中考数学真题汇编", "初中", "九年级", "数学", "真题", 2580);
        addResource("高中物理必修一课件", "高中", "高一", "物理", "课件", 1890);
        addResource("大学英语四级词汇表", "大学", "大一", "英语", "复习提纲", 3200);
        addResource("小学语文古诗词大全", "小学", "六年级", "语文", "复习提纲", 4500);
        addResource("初中英语语法精讲", "初中", "八年级", "英语", "课件", 1760);
        addResource("高中数学导数专题", "高中", "高二", "数学", "真题", 2100);
        addResource("初中化学方程式汇总", "初中", "九年级", "化学", "复习提纲", 1900);
        addResource("小学生作文素材积累", "小学", "五年级", "语文", "课件", 3200);
        addResource("高中历史时间轴", "高中", "高一", "历史", "复习提纲", 1500);
        addResource("大学高等数学课后答案", "大学", "大一", "数学", "答案", 2800);
        log.info("Seeded 10 sample resources.");
    }

    private void addResource(String name, String stage, String grade, String subj, String type, int downloads) {
        StudyResource r = new StudyResource();
        r.setResourceName(name);
        r.setStage(stage);
        r.setGrade(grade);
        r.setSubject(subj);
        r.setResourceType(type);
        r.setFileUrl("/oss/init/" + name + ".pdf");
        r.setSource("init");
        r.setAuditStatus(1);
        r.setDownloadCount(downloads);
        resourceMapper.insert(r);
        r.setFileUrl("/resource-preview.html?id=" + r.getId());
        resourceMapper.updateById(r);
    }

    private void seedWords() {
        Long wordCount = wordMapper.selectCount(null);
        if (wordCount != null && wordCount > 0) {
            log.info("DB has words, skip seeding");
            return;
        }
        log.info("Seeding English words...");

        // 中考词汇 (junior high entrance exam)
        addWord("abandon", "/əˈbændən/", "v. 放弃；遗弃", "He had to abandon the plan.", "中考", "核心词汇");
        addWord("ability", "/əˈbɪləti/", "n. 能力；才能", "She has the ability to learn quickly.", "中考", "核心词汇");
        addWord("absent", "/ˈæbsənt/", "adj. 缺席的；不在的", "He was absent from school today.", "中考", "核心词汇");
        addWord("accept", "/əkˈsept/", "v. 接受；承认", "Please accept my apology.", "中考", "核心词汇");
        addWord("achieve", "/əˈtʃiːv/", "v. 实现；达到", "Work hard to achieve your goals.", "中考", "核心词汇");
        addWord("achieve", "/əˈtʃiːv/", "v. 实现；取得", "She achieved her dream of becoming a doctor.", "中考", "核心词汇");
        addWord("advantage", "/ədˈvæntɪdʒ/", "n. 优势；有利条件", "Being tall is an advantage in basketball.", "中考", "核心词汇");
        addWord("adventure", "/ədˈventʃər/", "n. 冒险；奇遇", "They went on an exciting adventure.", "中考", "核心词汇");
        addWord("afford", "/əˈfɔːrd/", "v. 负担得起", "I can't afford to buy a new car.", "中考", "核心词汇");
        addWord("against", "/əˈɡeɪnst/", "prep. 反对；靠着", "We will play against Class A.", "中考", "核心词汇");
        addWord("agree", "/əˈɡriː/", "v. 同意；赞成", "I agree with your opinion.", "中考", "核心词汇");
        addWord("allow", "/əˈlaʊ/", "v. 允许；准许", "Smoking is not allowed here.", "中考", "核心词汇");
        addWord("although", "/ɔːlˈðoʊ/", "conj. 虽然；尽管", "Although it rained, we went out.", "中考", "核心词汇");
        addWord("announce", "/əˈnaʊns/", "v. 宣布；通知", "The teacher announced the test results.", "中考", "核心词汇");
        addWord("appear", "/əˈpɪr/", "v. 出现；似乎", "A rainbow appeared after the rain.", "中考", "核心词汇");

        // 高考词汇 (college entrance exam)
        addWord("abundant", "/əˈbʌndənt/", "adj. 丰富的；充裕的", "The region has abundant natural resources.", "高考", "高频词汇");
        addWord("accelerate", "/əkˈseləreɪt/", "v. 加速；促进", "Economic growth accelerated this year.", "高考", "高频词汇");
        addWord("accommodate", "/əˈkɒmədeɪt/", "v. 容纳；适应", "The hotel can accommodate 500 guests.", "高考", "高频词汇");
        addWord("accompany", "/əˈkʌmpəni/", "v. 陪伴；伴随", "She accompanied her mother to the hospital.", "高考", "高频词汇");
        addWord("accomplish", "/əˈkɑːmplɪʃ/", "v. 完成；实现", "We accomplished our mission successfully.", "高考", "高频词汇");
        addWord("accurate", "/ˈækjərət/", "adj. 准确的；精确的", "The data must be accurate.", "高考", "高频词汇");
        addWord("acquire", "/əˈkwaɪər/", "v. 获得；学到", "She acquired a good knowledge of English.", "高考", "高频词汇");
        addWord("adapt", "/əˈdæpt/", "v. 适应；改编", "Animals adapt to their environment.", "高考", "高频词汇");
        addWord("adequate", "/ˈædɪkwət/", "adj. 充足的；适当的", "The supply is not adequate for the demand.", "高考", "高频词汇");
        addWord("adjust", "/əˈdʒʌst/", "v. 调整；适应", "You need to adjust your study methods.", "高考", "高频词汇");

        // 四级词汇 (CET-4)
        addWord("abandon", "/əˈbændən/", "vt. 放弃；遗弃", "They abandoned the sinking ship.", "四级", "核心词汇");
        addWord("absolute", "/ˈæbsəluːt/", "adj. 绝对的；完全的", "I have absolute faith in her judgment.", "四级", "核心词汇");
        addWord("absorb", "/əbˈzɔːrb/", "v. 吸收；吸引", "Plants absorb carbon dioxide.", "四级", "核心词汇");
        addWord("abstract", "/ˈæbstrækt/", "adj. 抽象的；理论的", "The idea is too abstract for children.", "四级", "核心词汇");
        addWord("abuse", "/əˈbjuːs/", "n./v. 滥用；虐待", "This is an abuse of power.", "四级", "核心词汇");
        addWord("academic", "/ˌækəˈdemɪk/", "adj. 学术的；学院的", "She has a brilliant academic record.", "四级", "核心词汇");
        addWord("access", "/ˈækses/", "n. 通道；入口 v. 访问", "Students have access to the library.", "四级", "核心词汇");
        addWord("accident", "/ˈæksɪdənt/", "n. 事故；意外", "The accident happened at the crossroads.", "四级", "核心词汇");
        addWord("account", "/əˈkaʊnt/", "n. 账户；描述", "Please explain your account of events.", "四级", "核心词汇");
        addWord("accumulate", "/əˈkjuːmjəleɪt/", "v. 积累；积聚", "Dust accumulated on the bookshelf.", "四级", "核心词汇");

        // 六级词汇 (CET-6)
        addWord("abnormal", "/æbˈnɔːrml/", "adj. 反常的；异常的", "The test showed abnormal results.", "六级", "核心词汇");
        addWord("abolish", "/əˈbɑːlɪʃ/", "vt. 废除；废止", "Slavery was abolished in the 19th century.", "六级", "核心词汇");
        addWord("abortion", "/əˈbɔːrʃn/", "n. 流产；堕胎", "The debate on abortion continues.", "六级", "核心词汇");
        addWord("abrupt", "/əˈbrʌpt/", "adj. 突然的；唐突的", "The meeting came to an abrupt end.", "六级", "核心词汇");
        addWord("absurd", "/əbˈsɜːrd/", "adj. 荒谬的；可笑的", "The idea seems absurd to me.", "六级", "核心词汇");
        addWord("abundance", "/əˈbʌndəns/", "n. 丰富；充裕", "There is an abundance of wildlife here.", "六级", "核心词汇");
        addWord("accessory", "/əkˈsesəri/", "n. 配件；附属品", "The store sells fashion accessories.", "六级", "核心词汇");
        addWord("accommodate", "/əˈkɒmədeɪt/", "v. 容纳；向…提供住处", "The hall can accommodate 200 people.", "六级", "核心词汇");
        addWord("accountable", "/əˈkaʊntəbl/", "adj. 有责任的；应负责的", "The government should be accountable to the people.", "六级", "核心词汇");
        addWord("accumulation", "/əˌkjuːmjəˈleɪʃn/", "n. 积累；积聚物", "The accumulation of wealth is not everything.", "六级", "核心词汇");

        log.info("Seeded " + (15 + 10 + 10 + 10) + " English words across all stages.");
    }

    private void addWord(String word, String phonetic, String meaning, String example, String stage, String category) {
        EnglishWord w = new EnglishWord();
        w.setWord(word);
        w.setPhonetic(phonetic);
        w.setMeaning(meaning);
        w.setExample(example);
        w.setStage(stage);
        w.setCategory(category);
        w.setMemoryStatus(0);
        w.setReviewCount(0);
        wordMapper.insert(w);
    }
}