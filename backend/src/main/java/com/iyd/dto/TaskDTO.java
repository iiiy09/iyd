package com.iyd.dto;

import lombok.Data;

@Data
public class TaskDTO {
    private String taskContent;
    private Integer estimatedMinutes;
    private String deadline;
}
