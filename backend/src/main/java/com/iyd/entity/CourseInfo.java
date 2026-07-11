package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_info")
public class CourseInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseName;
    private String stage;
    private String grade;
    private String subject;
    private String semester;
    private String videoUrl;
    private String coverUrl;
    private Integer duration;
    private String description;
    private String teacherName;
    private String category;
    private Integer playCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
