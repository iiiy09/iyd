package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_task")
public class StudyTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String taskContent;
    private Integer estimatedMinutes;
    private LocalDateTime deadline;
    private Integer status;
    private LocalDateTime checkinTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
