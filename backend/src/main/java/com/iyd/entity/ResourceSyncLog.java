package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resource_sync_log")
public class ResourceSyncLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private Integer newResourceCount;
    private String resourceIds;
    private String operationType;
    private Long operatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
