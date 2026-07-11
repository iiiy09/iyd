package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("question_error")
public class QuestionError {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String questionContent;
    private String questionImage;
    private String answer;
    private String userAnswer;
    private String knowledgePoint;
    private String errorReason;
    private Integer errorCount;
    private Integer mastered;
    private String subject;
    private String stage;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
