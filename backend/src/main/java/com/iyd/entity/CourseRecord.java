package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_record")
public class CourseRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long courseId;
    private Integer watchedDuration;
    private Integer totalDuration;
    private Integer status;
    private LocalDateTime lastWatchTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
