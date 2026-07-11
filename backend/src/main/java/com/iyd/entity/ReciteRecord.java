package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("recite_record")
public class ReciteRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String originalText;
    private String userAnswer;
    private String handwrittenImage;
    private String reciteVideo;
    private Integer checkType;
    private Double score;
    private String errorDetails;
    private String suggestion;
    private String subject;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
