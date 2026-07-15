package com.iyd.config;

import com.iyd.entity.StudyResource;
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

    @Override
    public void run(String... args) {
        Long count = resourceMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("DB has resources, skip seeding");
            return;
        }
        log.info("Seeding sample resources...");
        add("2024\u4e2d\u8003\u6570\u5b66\u771f\u9898\u6c47\u7f16", "\u521d\u4e2d", "\u4e5d\u5e74\u7ea7", "\u6570\u5b66", "\u771f\u9898", 2580);
        add("\u9ad8\u4e2d\u7269\u7406\u5fc5\u4fee\u4e00\u8bfe\u4ef6", "\u9ad8\u4e2d", "\u9ad8\u4e00", "\u7269\u7406", "\u8bfe\u4ef6", 1890);
        add("\u5927\u5b66\u82f1\u8bed\u56db\u7ea7\u8bcd\u6c47\u8868", "\u5927\u5b66", "\u5927\u4e00", "\u82f1\u8bed", "\u590d\u4e60\u63d0\u7eb2", 3200);
        add("\u5c0f\u5b66\u8bed\u6587\u53e4\u8bd7\u8bcd\u5927\u5168", "\u5c0f\u5b66", "\u516d\u5e74\u7ea7", "\u8bed\u6587", "\u590d\u4e60\u63d0\u7eb2", 4500);
        add("\u521d\u4e2d\u82f1\u8bed\u8bed\u6cd5\u7cbe\u8bb2", "\u521d\u4e2d", "\u516b\u5e74\u7ea7", "\u82f1\u8bed", "\u8bfe\u4ef6", 1760);
        add("\u9ad8\u4e2d\u6570\u5b66\u5bfc\u6570\u4e13\u9898", "\u9ad8\u4e2d", "\u9ad8\u4e8c", "\u6570\u5b66", "\u771f\u9898", 2100);
        add("\u521d\u4e2d\u5316\u5b66\u65b9\u7a0b\u5f0f\u6c47\u603b", "\u521d\u4e2d", "\u4e5d\u5e74\u7ea7", "\u5316\u5b66", "\u590d\u4e60\u63d0\u7eb2", 1900);
        add("\u5c0f\u5b66\u751f\u4f5c\u6587\u7d20\u6750\u79ef\u7d2f", "\u5c0f\u5b66", "\u4e94\u5e74\u7ea7", "\u8bed\u6587", "\u8bfe\u4ef6", 3200);
        add("\u9ad8\u4e2d\u5386\u53f2\u65f6\u95f4\u8f74", "\u9ad8\u4e2d", "\u9ad8\u4e00", "\u5386\u53f2", "\u590d\u4e60\u63d0\u7eb2", 1500);
        add("\u5927\u5b66\u9ad8\u7b49\u6570\u5b66\u8bfe\u540e\u7b54\u6848", "\u5927\u5b66", "\u5927\u4e00", "\u6570\u5b66", "\u7b54\u6848", 2800);
        log.info("Seeded 10 sample resources.");
    }

    private void add(String name, String stage, String grade, String subj, String type, int downloads) {
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
        // Update fileUrl with actual ID after insert
        r.setFileUrl("/resource-preview.html?id=" + r.getId());
        resourceMapper.updateById(r);
    }
}