package com.iyd.dto;

import lombok.Data;
import java.util.List;

@Data
public class ResourceQueryDTO {
    private String stage;
    private String grade;
    private String subject;
    private String resourceType;
    private Integer page;
    private Integer size;
    private String keyword;
    private Integer auditStatus;
    private String source;
}
