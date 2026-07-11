package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_collection")
public class UserCollection {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long resourceId;
    private Integer collectionType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
