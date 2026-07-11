package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("english_word")
public class EnglishWord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String word;
    private String phonetic;
    private String meaning;
    private String example;
    private String stage;
    private String category;
    private Integer memoryStatus;
    private Long userId;
    private Integer reviewCount;
    private LocalDateTime lastReviewTime;
    private LocalDateTime nextReviewTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
