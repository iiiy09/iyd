package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("daily_checkin")
public class DailyCheckin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDateTime checkinDate;
    private Integer studyMinutes;
    private Integer taskCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
