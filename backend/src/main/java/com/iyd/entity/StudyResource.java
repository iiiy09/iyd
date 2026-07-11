package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_resource")
public class StudyResource {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String resourceName;
    private String stage;
    private String grade;
    private String subject;
    private String resourceType;
    private String fileUrl;
    private String source;
    private Integer auditStatus;
    private Long uploaderId;
    private Integer downloadCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
