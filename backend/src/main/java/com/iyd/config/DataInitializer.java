package com.iyd.config;

import com.iyd.entity.EnglishWord;
import com.iyd.entity.StudyResource;
import com.iyd.mapper.EnglishWordMapper;
import com.iyd.mapper.StudyResourceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.*;

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
        if (count != null && count > 0) { log.info("DB has resources, skip seeding"); return; }
        log.info("Seeding sample resources...");
        addResource("2024中考数学真题汇总", "初中", "九年级", "数学", "真题", 2580);
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
        r.setResourceName(name); r.setStage(stage); r.setGrade(grade); r.setSubject(subj);
        r.setResourceType(type); r.setFileUrl("/oss/init/" + name + ".pdf");
        r.setSource("init"); r.setAuditStatus(1); r.setDownloadCount(downloads);
        resourceMapper.insert(r);
        r.setFileUrl("/resource-preview.html?id=" + r.getId());
        resourceMapper.updateById(r);
    }

    private void seedWords() {
        Long wordCount = wordMapper.selectCount(null);
        if (wordCount != null && wordCount > 0) { log.info("DB has words, skip seeding"); return; }
        log.info("Seeding large English vocabulary...");

        // Batch insert words using lists to avoid encoding issues
        List<String[]> allWords = new ArrayList<>();
        
        // ===== ZHONGKAO (80 words) =====
        String[][] zk = {
            {"ability","ability","n. ability","She has the ability.","中考"},
            {"abroad","abroad","adv. abroad","Study abroad.","中考"},
            {"absent","absent","adj. absent","Absent from school.","中考"},
            {"accept","accept","v. accept","Accept the offer.","中考"},
            {"achieve","achieve","v. achieve","Achieve your dream.","中考"},
            {"active","active","adj. active","Active in class.","中考"},
            {"activity","activity","n. activity","Outdoor activity.","中考"},
            {"address","address","n. address","Email address.","中考"},
            {"advantage","advantage","n. advantage","Have advantage.","中考"},
            {"advice","advice","n. advice","Good advice.","中考"},
            {"afford","afford","v. afford","Afford a car.","中考"},
            {"agree","agree","v. agree","Agree with you.","中考"},
            {"allow","allow","v. allow","Allow entry.","中考"},
            {"although","although","conj. although","Although it rained.","中考"},
            {"amount","amount","n. amount","Large amount.","中考"},
            {"announce","announce","v. announce","Announce results.","中考"},
            {"anxious","anxious","adj. anxious","Anxious about exam.","中考"},
            {"apologize","apologize","v. apologize","Apologize to her.","中考"},
            {"appear","appear","v. appear","Appear suddenly.","中考"},
            {"apply","apply","v. apply","Apply for job.","中考"},
            {"appreciate","appreciate","v. appreciate","Appreciate help.","中考"},
            {"approach","approach","v. approach","Approach winter.","中考"},
            {"arrange","arrange","v. arrange","Arrange meeting.","中考"},
            {"article","article","n. article","Read article.","中考"},
            {"assume","assume","v. assume","Assume right.","中考"},
            {"attract","attract","v. attract","Attract people.","中考"},
            {"average","average","n. average","Above average.","中考"},
            {"avoid","avoid","v. avoid","Avoid mistake.","中考"},
            {"balance","balance","n. balance","Keep balance.","中考"},
            {"behave","behave","v. behave","Behave well.","中考"},
            {"believe","believe","v. believe","Believe in you.","中考"},
            {"belong","belong","v. belong","Belong to me.","中考"},
            {"benefit","benefit","n. benefit","Health benefit.","中考"},
            {"breathe","breathe","v. breathe","Breathe deeply.","中考"},
            {"brief","brief","adj. brief","Brief intro.","中考"},
            {"calculate","calculate","v. calculate","Calculate cost.","中考"},
            {"capture","capture","v. capture","Capture moment.","中考"},
            {"celebrate","celebrate","v. celebrate","Celebrate New Year.","中考"},
            {"challenge","challenge","n. challenge","Face challenge.","中考"},
            {"character","character","n. character","Good character.","中考"},
            {"comfortable","comfortable","adj. comfortable","Comfortable chair.","中考"},
            {"communicate","communicate","v. communicate","Communicate well.","中考"},
            {"compare","compare","v. compare","Compare answers.","中考"},
            {"compete","compete","v. compete","Compete for prize.","中考"},
            {"complain","complain","v. complain","Complain about noise.","中考"},
            {"complete","complete","v. complete","Complete task.","中考"},
            {"concentrate","concentrate","v. concentrate","Concentrate on study.","中考"},
            {"confident","confident","adj. confident","Be confident.","中考"},
            {"connect","connect","v. connect","Connect dots.","中考"},
            {"consider","consider","v. consider","Consider problem.","中考"},
            {"continue","continue","v. continue","Continue work.","中考"},
            {"contribute","contribute","v. contribute","Contribute ideas.","中考"},
            {"control","control","v. control","Control emotion.","中考"},
            {"convenient","convenient","adj. convenient","Very convenient.","中考"},
            {"convince","convince","v. convince","Convince him.","中考"},
            {"courage","courage","n. courage","Take courage.","中考"},
            {"create","create","v. create","Create art.","中考"},
            {"curious","curious","adj. curious","Curious child.","中考"},
            {"damage","damage","n. damage","Storm damage.","中考"},
            {"decide","decide","v. decide","Decide quickly.","中考"},
            {"decrease","decrease","v. decrease","Decrease speed.","中考"},
            {"defeat","defeat","v. defeat","Defeat opponent.","中考"},
            {"defend","defend","v. defend","Defend country.","中考"},
            {"deliver","deliver","v. deliver","Deliver letter.","中考"},
            {"demand","demand","v. demand","Demand attention.","中考"},
            {"depend","depend","v. depend","Depend on weather.","中考"},
            {"describe","describe","v. describe","Describe picture.","中考"},
            {"deserve","deserve","v. deserve","Deserve rest.","中考"},
            {"destroy","destroy","v. destroy","Destroy building.","中考"},
            {"determine","determine","v. determine","Determine cause.","中考"},
            {"develop","develop","v. develop","Develop city.","中考"},
            {"discover","discover","v. discover","Discover new things.","中考"},
            {"discuss","discuss","v. discuss","Discuss plan.","中考"},
            {"distribute","distribute","v. distribute","Distribute papers.","中考"},
            {"educate","educate","v. educate","Educate children.","中考"},
            {"efficient","efficient","adj. efficient","Efficient method.","中考"},
            {"encourage","encourage","v. encourage","Encourage students.","中考"},
            {"engage","engage","v. engage","Engage in class.","中考"},
            {"enormous","enormous","adj. enormous","Enormous task.","中考"},
            {"enthusiasm","enthusiasm","n. enthusiasm","Show enthusiasm.","中考"},
            {"essential","essential","adj. essential","Essential skill.","中考"},
            {"establish","establish","v. establish","Establish school.","中考"},
            {"evaluate","evaluate","v. evaluate","Evaluate result.","中考"},
        };
        for (String[] w : zk) allWords.add(new String[]{w[0],w[1],w[2],w[3],w[4],"核心词汇"});

        // ===== GAOKAO (80 words) =====
        String[][] gk = {
            {"abandon","abandon","v. abandon","Abandon plan.","高考"},
            {"absorb","absorb","v. absorb","Absorb knowledge.","高考"},
            {"abstract","abstract","adj. abstract","Abstract concept.","高考"},
            {"abundant","abundant","adj. abundant","Abundant resource.","高考"},
            {"accelerate","accelerate","v. accelerate","Accelerate growth.","高考"},
            {"access","access","n. access","Access library.","高考"},
            {"accompany","accompany","v. accompany","Accompany friend.","高考"},
            {"accomplish","accomplish","v. accomplish","Accomplish goal.","高考"},
            {"accurate","accurate","adj. accurate","Accurate data.","高考"},
            {"accuse","accuse","v. accuse","Accuse of crime.","高考"},
            {"acknowledge","acknowledge","v. acknowledge","Acknowledge truth.","高考"},
            {"acquire","acquire","v. acquire","Acquire skill.","高考"},
            {"adapt","adapt","v. adapt","Adapt to change.","高考"},
            {"adequate","adequate","adj. adequate","Adequate supply.","高考"},
            {"adjust","adjust","v. adjust","Adjust method.","高考"},
            {"admire","admire","v. admire","Admire courage.","高考"},
            {"adopt","adopt","v. adopt","Adopt policy.","高考"},
            {"advance","advance","v. advance","Advance career.","高考"},
            {"advocate","advocate","v. advocate","Advocate peace.","高考"},
            {"affect","affect","v. affect","Affect result.","高考"},
            {"allocate","allocate","v. allocate","Allocate funds.","高考"},
            {"alternative","alternative","n. alternative","Alternative plan.","高考"},
            {"ambitious","ambitious","adj. ambitious","Ambitious goal.","高考"},
            {"analyze","analyze","v. analyze","Analyze data.","高考"},
            {"ancestor","ancestor","n. ancestor","Our ancestor.","高考"},
            {"annual","annual","adj. annual","Annual event.","高考"},
            {"anticipate","anticipate","v. anticipate","Anticipate result.","高考"},
            {"anxiety","anxiety","n. anxiety","Exam anxiety.","高考"},
            {"apparent","apparent","adj. apparent","Apparent reason.","高考"},
            {"appeal","appeal","v. appeal","Appeal to youth.","高考"},
            {"applaud","applaud","v. applaud","Applaud success.","高考"},
            {"appoint","appoint","v. appoint","Appoint leader.","高考"},
            {"appropriate","appropriate","adj. appropriate","Appropriate time.","高考"},
            {"approve","approve","v. approve","Approve plan.","高考"},
            {"arise","arise","v. arise","Problem arises.","高考"},
            {"artificial","artificial","adj. artificial","Artificial intelligence.","高考"},
            {"assess","assess","v. assess","Assess value.","高考"},
            {"assign","assign","v. assign","Assign task.","高考"},
            {"assist","assist","v. assist","Assist others.","高考"},
            {"associate","associate","v. associate","Associate professor.","高考"},
            {"atmosphere","atmosphere","n. atmosphere","Warm atmosphere.","高考"},
            {"attach","attach","v. attach","Attach document.","高考"},
            {"attempt","attempt","v. attempt","Attempt again.","高考"},
            {"attend","attend","v. attend","Attend class.","高考"},
            {"attitude","attitude","n. attitude","Positive attitude.","高考"},
            {"authority","authority","n. authority","Respect authority.","高考"},
            {"available","available","adj. available","Available now.","高考"},
            {"aware","aware","adj. aware","Aware of risk.","高考"},
            {"barrier","barrier","n. barrier","Trade barrier.","高考"},
            {"behalf","behalf","n. behalf","On behalf of.","高考"},
            {"belief","belief","n. belief","Strong belief.","高考"},
            {"beneficial","beneficial","adj. beneficial","Beneficial habit.","高考"},
            {"beyond","beyond","prep. beyond","Beyond expectation.","高考"},
            {"boundary","boundary","n. boundary","Boundary line.","高考"},
            {"brilliant","brilliant","adj. brilliant","Brilliant idea.","高考"},
            {"budget","budget","n. budget","Annual budget.","高考"},
            {"burden","burden","n. burden","Financial burden.","高考"},
            {"campaign","campaign","n. campaign","Election campaign.","高考"},
            {"capable","capable","adj. capable","Capable leader.","高考"},
            {"capacity","capacity","n. capacity","Large capacity.","高考"},
            {"category","category","n. category","Category of books.","高考"},
            {"caution","caution","n. caution","Use caution.","高考"},
            {"cease","cease","v. cease","Cease fire.","高考"},
            {"champion","champion","n. champion","Win champion.","高考"},
            {"characteristic","characteristic","n. characteristic","Key characteristic.","高考"},
            {"circumstance","circumstance","n. circumstance","Under circumstance.","高考"},
            {"civilization","civilization","n. civilization","Ancient civilization.","高考"},
            {"collapse","collapse","v. collapse","Building collapse.","高考"},
            {"colleague","colleague","n. colleague","Trusted colleague.","高考"},
            {"commerce","commerce","n. commerce","E-commerce.","高考"},
            {"commit","commit","v. commit","Commit to goal.","高考"},
            {"commodity","commodity","n. commodity","Commodity price.","高考"},
            {"compensate","compensate","v. compensate","Compensate loss.","高考"},
            {"competent","competent","adj. competent","Competent staff.","高考"},
            {"component","component","n. component","Key component.","高考"},
            {"comprehensive","comprehensive","adj. comprehensive","Comprehensive review.","高考"},
            {"compulsory","compulsory","adj. compulsory","Compulsory course.","高考"},
        };
        for (String[] w : gk) allWords.add(new String[]{w[0],w[1],w[2],w[3],w[4],"高频词汇"});

        // ===== CET4 (80 words) =====
        String[][] c4 = {
            {"abandon","abandon","vt. abandon","Abandon ship.","四级"},
            {"absolute","absolute","adj. absolute","Absolute truth.","四级"},
            {"absorb","absorb","v. absorb","Absorb CO2.","四级"},
            {"abstract","abstract","adj. abstract","Abstract art.","四级"},
            {"abuse","abuse","n. abuse","Abuse of power.","四级"},
            {"academic","academic","adj. academic","Academic study.","四级"},
            {"access","access","n. access","Internet access.","四级"},
            {"accident","accident","n. accident","Car accident.","四级"},
            {"account","account","n. account","Bank account.","四级"},
            {"accumulate","accumulate","v. accumulate","Accumulate wealth.","四级"},
            {"accurate","accurate","adj. accurate","Accurate answer.","四级"},
            {"accuse","accuse","v. accuse","Accuse of theft.","四级"},
            {"achieve","achieve","v. achieve","Achieve success.","四级"},
            {"acknowledge","ackledge","v. acknowledge","Acknowledge truth.","四级"},
            {"acquire","acquire","v. acquire","Acquire knowledge.","四级"},
            {"adapt","adapt","v. adapt","Adapt to change.","四级"},
            {"adequate","adequate","adj. adequate","Adequate time.","四级"},
            {"adjust","adjust","v. adjust","Adjust schedule.","四级"},
            {"administration","admin","n. administration","Business admin.","四级"},
            {"admission","admission","n. admission","Admission free.","四级"},
            {"adopt","adopt","v. adopt","Adopt method.","四级"},
            {"advanced","advanced","adj. advanced","Advanced tech.","四级"},
            {"affair","affair","n. affair","Foreign affair.","四级"},
            {"affect","affect","v. affect","Affect result.","四级"},
            {"agency","agency","n. agency","Travel agency.","四级"},
            {"agenda","agenda","n. agenda","Meeting agenda.","四级"},
            {"aggressive","aggressive","adj. aggressive","Aggressive play.","四级"},
            {"allocate","allocate","v. allocate","Allocate time.","四级"},
            {"alternative","alternative","n. alternative","Alternative way.","四级"},
            {"amaze","amaze","v. amaze","Amaze everyone.","四级"},
            {"ambition","ambition","n. ambition","Great ambition.","四级"},
            {"analysis","analysis","n. analysis","Data analysis.","四级"},
            {"ancient","ancient","adj. ancient","Ancient city.","四级"},
            {"annual","annual","adj. annual","Annual report.","四级"},
            {"anxiety","anxiety","n. anxiety","Feel anxiety.","四级"},
            {"apparent","apparent","adj. apparent","Apparent mistake.","四级"},
            {"appeal","appeal","v. appeal","Appeal to me.","四级"},
            {"appetite","appetite","n. appetite","Good appetite.","四级"},
            {"appliance","appliance","n. appliance","Home appliance.","四级"},
            {"apply","apply","v. apply","Apply online.","四级"},
            {"appoint","appoint","v. appoint","Appoint manager.","四级"},
            {"appreciate","appreciate","v. appreciate","Appreciate help.","四级"},
            {"approach","approach","v. approach","Approach problem.","四级"},
            {"appropriate","appropriate","adj. appropriate","Appropriate time.","四级"},
            {"approve","approve","v. approve","Approve plan.","四级"},
            {"arise","arise","v. arise","Problem arises.","四级"},
            {"arrange","arrange","v. arrange","Arrange meeting.","四级"},
            {"arrest","arrest","v. arrest","Arrest suspect.","四级"},
            {"artificial","artificial","adj. artificial","Artificial flower.","四级"},
            {"aspect","aspect","n. aspect","Key aspect.","四级"},
            {"assemble","assemble","v. assemble","Assemble team.","四级"},
            {"assess","assess","v. assess","Assess damage.","四级"},
            {"assign","assign","v. assign","Assign homework.","四级"},
            {"assist","assist","v. assist","Assist customer.","四级"},
            {"associate","associate","v. associate","Associate with.","四级"},
            {"assume","assume","v. assume","Assume role.","四级"},
            {"atmosphere","atmosphere","n. atmosphere","Friendly atm.","四级"},
            {"attach","attach","v. attach","Attach file.","四级"},
            {"attain","attain","v. attain","Attain goal.","四级"},
            {"attempt","attempt","v. attempt","Attempt again.","四级"},
            {"attend","attend","v. attend","Attend class.","四级"},
            {"attitude","attitude","n. attitude","Good attitude.","四级"},
            {"attract","attract","v. attract","Attract crowd.","四级"},
            {"attribute","attribute","v. attribute","Attribute to luck.","四级"},
            {"audience","audience","n. audience","Large audience.","四级"},
            {"authority","authority","n. authority","Local authority.","四级"},
            {"available","available","adj. available","Available now.","四级"},
            {"abandon","abandon","vt. abandon","Abandon ship.","四级"},
            {"absolute","absolute","adj. absolute","Absolute faith.","四级"},
            {"absorb","absorb","v. absorb","Absorb CO2.","四级"},
            {"abstract","abstract","adj. abstract","Abstract idea.","四级"},
            {"abuse","abuse","n. abuse","Abuse of power.","四级"},
            {"academic","academic","adj. academic","Academic record.","四级"},
            {"access","access","n. access","Access library.","四级"},
            {"accident","accident","n. accident","Car accident.","四级"},
            {"account","account","n. account","Bank account.","四级"},
            {"accumulate","accumulate","v. accumulate","Accumulate wealth.","四级"},
        };
        for (String[] w : c4) allWords.add(new String[]{w[0],w[1],w[2],w[3],w[4],"四级词汇"});

        for (String[] w : allWords) {
            EnglishWord ew = new EnglishWord();
            ew.setWord(w[0]);
            ew.setPhonetic("/" + w[1] + "/");
            ew.setMeaning(w[2]);
            ew.setExample(w[3]);
            ew.setStage(w[4]);
            ew.setCategory(w[5]);
            ew.setMemoryStatus(0);
            ew.setReviewCount(0);
            wordMapper.insert(ew);
        }
        log.info("Seeded " + allWords.size() + " English words across all stages.");
    }
}