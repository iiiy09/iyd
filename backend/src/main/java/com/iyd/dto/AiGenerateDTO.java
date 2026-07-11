package com.iyd.dto;

import lombok.Data;

@Data
public class AiGenerateDTO {
    private Integer generateType;
    private String inputContent;
    private String topic;
    private String style;
}
