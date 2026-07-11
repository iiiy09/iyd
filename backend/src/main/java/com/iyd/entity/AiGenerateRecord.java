package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_generate_record")
public class AiGenerateRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer generateType;
    private String inputContent;
    private String outputContent;
    private String outputImage;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
