package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("score_report")
public class ScoreReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String originalFile;
    private String chineseScore;
    private String mathScore;
    private String englishScore;
    private String physicsScore;
    private String chemistryScore;
    private String biologyScore;
    private String historyScore;
    private String geographyScore;
    private String politicsScore;
    private String aiReport;
    private String studyPlan;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
