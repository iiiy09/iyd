package com.iyd.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("note_folder")
public class NoteFolder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String folderName;
    private Long parentId;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
